package com.xdao7.xdweather

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.xdao7.xdweather.databinding.ActivityWeatherBinding
import com.xdao7.xdweather.databinding.ItemForecastBinding
import com.xdao7.xdweather.logic.model.*
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.ui.adapter.HourlyAdapter
import com.xdao7.xdweather.ui.dialog.WarningDialog
import com.xdao7.xdweather.ui.viewmodel.WeatherViewModel
import com.xdao7.xdweather.utils.*
import com.xdao7.xdweather.view.BaseScrollAnimatorView

class WeatherActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context, locationPermission: Boolean) {
            startActivity<WeatherActivity>(context) {
                putExtra(INTENT_LOCATION_PERMISSION, locationPermission)
            }
        }

        const val SEARCH_PLACE_REQUEST_CODE = 0
    }

    lateinit var binding: ActivityWeatherBinding

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var scrollRect: Rect
    private val scrollViews: ArrayList<BaseScrollAnimatorView> = ArrayList()
    private var locationPermission = false

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFullScreenMode(window)

        DisplayMetrics().apply {
            scrollRect = Rect(0, 0, widthPixels, heightPixels)
        }

        locationPermission = intent.getBooleanExtra(INTENT_LOCATION_PERMISSION, false)

        initData()
        initListener()
        initViews()
    }

    override fun onDestroy() {
        NetworkUtils.removeNetworkCallback(WeatherActivity::class.java.simpleName)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == SEARCH_PLACE_REQUEST_CODE) {
            if (binding.dlHome.isDrawerOpen(GravityCompat.START)) {
                binding.dlHome.closeDrawers()
            }
            val location: Location? = data?.getParcelableExtra(INTENT_PLACE)
            if (location != null) {
                viewModel.addCity(location)
                refreshWeather()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (binding.dlHome.isDrawerOpen(GravityCompat.START)) {
            binding.dlHome.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    private fun initData() {
        viewModel.apply {
            initData()
            weatherLiveData.observe(this@WeatherActivity) { result ->
                val weather = result.getOrNull()
                if (weather != null) {
                    showWeatherInfo(weather)
                    R.string.str_refresh_success.showToast()
                } else {
                    R.string.str_no_weather.showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
                binding.srlWeather.isRefreshing = false
            }
            defaultLiveData.observe(this@WeatherActivity) { result ->
                val weather = result.getOrNull()
                if (weather != null) {
                    showWeatherInfo(weather)
                    R.string.str_refresh_success.showToast()
                } else {
                    R.string.str_no_weather.showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
                binding.srlWeather.isRefreshing = false
            }
            locationLiveData.observe(this@WeatherActivity) { result ->
                val locations = result.getOrNull()
                if (locations != null && locations.isNotEmpty()) {
                    viewModel.addLocation(locations[0])
                } else {
                    R.string.str_location_search_fail.showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
                hideCreateTip()
                refreshWeather()
            }
        }
    }

    private fun initListener() {
        NetworkUtils.addNetworkCallback(
            WeatherActivity::class.java.simpleName,
            object : NetworkUtils.IConnectivityCallback {
                override fun onAvailable(network: Network?) {
                    runOnUiThread {
                        binding.apply {
                            if (flNetwork.visibility == View.VISIBLE) {
                                if (locationPermission) {
                                    imageTip.loadResources(R.drawable.ic_location)
                                    refreshLocation()
                                } else {
                                    hideCreateTip()
                                }
                            }
                        }
                        if (viewModel.needRefresh) {
                            refreshWeather()
                        }
                    }
                }

                override fun onLost(network: Network?) {
                    runOnUiThread {
                        viewModel.needRefresh = true
                    }
                }
            })
    }

    private fun initViews() {
        binding.apply {
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
            appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                srlWeather.isEnabled = verticalOffset >= 0
                if (verticalOffset < 0) {
                    loadViewAnimator()
                }
            })
            btnCity.setOnClickListener {
                dlHome.openDrawer(GravityCompat.START)
            }
            dlHome.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            dlHome.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

                override fun onDrawerOpened(drawerView: View) {}

                override fun onDrawerClosed(drawerView: View) {
                    if (viewModel.needRefresh && isNetworkAvailable()) {
                        refreshWeather()
                    }
                }

                override fun onDrawerStateChanged(newState: Int) {}
            })
            srlWeather.apply {
                setColorSchemeResources(R.color.colorBlue)
                setOnRefreshListener {
                    if (locationPermission) {
                        refreshLocation()
                    } else {
                        refreshWeather()
                    }
                }
            }
            svWeather.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                loadViewAnimator()
            })
            if (isNetworkAvailable()) {
                if (locationPermission) {
                    imageTip.loadResources(R.drawable.ic_location)
                    refreshLocation()
                } else {
                    refreshWeather()
                }
            } else {
                imageTip.loadResources(R.drawable.ic_network)
                showNoNetwork()
            }
            includeForecast.rvHourly.apply {
                layoutManager =
                    LinearLayoutManager(this@WeatherActivity, RecyclerView.HORIZONTAL, false)
                hourlyAdapter = HourlyAdapter(this@WeatherActivity)
                adapter = hourlyAdapter
                isNestedScrollingEnabled = false
            }
            scrollViews.add(includeAir.roundBarAir)
            scrollViews.add(includeLifeIndex.roundBarHumidity)
            scrollViews.add(includeSunMoon.sun)
            scrollViews.add(includeSunMoon.moon)
        }
    }

    /**
     * ????????????
     */
    fun refreshLocation() {
        binding.srlWeather.isRefreshing = true
        if (isNetworkAvailable()) {
            viewModel.apply {
                needRefresh = false
                refreshLocation {
                    runOnUiThread {
                        hideCreateTip()
                        R.string.str_location_fail.showToast()
                        refreshWeather()
                    }
                }
            }
        } else {
            showNoNetwork()
        }
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    fun refreshWeather() {
        if (isNetworkAvailable()) {
            viewModel.refreshWeather()
            binding.srlWeather.isRefreshing = true
        } else {
            showNoNetwork()
            binding.srlWeather.isRefreshing = false
        }
    }

    /**
     * ????????????
     */
    private fun showWeatherInfo(weather: Weather) {
        val realtime = weather.realtime
        val hourly = weather.hourly
        val daily = weather.daily
        val life = weather.life
        val air = weather.air
        val warning = weather.warning

        binding.apply {
            textPlaceName.text = if (viewModel.city.position < 0) {
                viewModel.capital.name
            } else {
                viewModel.city.locations[viewModel.city.position].name
            }
            textTemp.text = getString(R.string.str_temp, realtime.temp)
            textSky.text = realtime.text
            textWeek.text = getString(getWeek())
            if (warning != null && warning.isNotEmpty()) {
                textWarning.visibility = View.VISIBLE
                textWarning.text =
                    getString(R.string.str_waring, warning[0].typeName, warning[0].level)
                textWarning.setOnClickListener {
                    val warningDialog = WarningDialog(this@WeatherActivity, warning)
                    warningDialog.show()
                }
            } else {
                textWarning.visibility = View.GONE
            }
            clBg.setBackgroundResource(
                if (isDay()) {
                    getSky(realtime.icon).bgDay
                } else {
                    getSky(realtime.icon).bgNight
                }
            )

            includeForecast.apply {
                llForecast.removeAllViews()
                hourlyAdapter.hourly = hourly
                hourlyAdapter.notifyDataSetChanged()
                val days = daily.size
                for (i in 0 until days) {
                    val skycon = daily[i]
                    val itemForecastBinding = ItemForecastBinding.inflate(
                        LayoutInflater.from(this@WeatherActivity),
                        llForecast,
                        false
                    ).apply {
                        textDate.text = skycon.fxDate
                        val sky = getSky(skycon.iconDay)
                        imageSky.setImageResource(sky.smallIcon)
                        textSky.text = skycon.textDay
                        textTemperature.text =
                            getString(R.string.str_temp_limit, skycon.tempMin, skycon.tempMax)
                    }
                    llForecast.addView(itemForecastBinding.root)
                }
                textDetails.setOnClickListener {
                    DailyActivity.actionStart(this@WeatherActivity, daily)
                }
            }

            includeAir.apply {
                roundBarAir.firstText = air.category
                roundBarAir.secondText = air.aqi
                roundBarAir.chooseColor(air.level)
                roundBarAir.setProgress(air.aqi.toFloat())
                pm10.text = air.pm10
                pm2p5.text = air.pm2p5
                no2.text = air.no2
                so2.text = air.so2
                co.text = air.co
                o3.text = air.o3
            }

            includeLifeIndex.apply {
                roundBarHumidity.apply {
                    firstText = ""
                    secondText = "${realtime.humidity}%"
                    setProgress(realtime.humidity.toFloat())
                }
                textFeelsLike.text = getString(R.string.str_feels_like, realtime.feelsLike)
                for (suggestion in life) {
                    when (suggestion.type) {
                        LIFE_SPORT -> textSport.text = suggestion.category
                        LIFE_CAR_WASHING -> textCarWashing.text = suggestion.category
                        LIFE_DRESSING -> textDressing.text = suggestion.category
                        LIFE_ULTRAVIOLET -> textUltraviolet.text = getString(
                            R.string.str_ultraviolet,
                            suggestion.level,
                            suggestion.category
                        )
                        LIFE_COLD_RISK -> textColdRisk.text = suggestion.category
                    }
                }
            }

            includeWind.apply {
                textWindDir.text = realtime.windDir
                textWindScale.text = getString(R.string.str_wind_scale, realtime.windScale)
            }

            includeSunMoon.apply {
                textMoonPhase.text = daily[0].moonPhase
                sun.setTime(daily[0].sunrise, daily[0].sunset)
                moon.setTime(daily[0].moonrise, daily[0].moonset)
            }

            appBarLayout.visibility = View.VISIBLE
            svWeather.visibility = View.VISIBLE
        }
    }

    /**
     * ?????????????????????
     */
    private fun showNoNetwork() {
        viewModel.needRefresh = true
        binding.srlWeather.showSnackbar(
            R.string.str_network_disconnected,
            R.string.str_network_setting
        ) {
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }

    /**
     * ????????????UI???????????????????????????
     */
    private fun hideCreateTip() {
        binding.apply {
            if (flNetwork.visibility == View.VISIBLE) {
                flNetwork.visibility = View.GONE
                dlHome.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    /**
     * ????????????
     */
    private fun loadViewAnimator() {
        for (view in scrollViews) {
            view.startAnimator(scrollRect)
        }
    }
}