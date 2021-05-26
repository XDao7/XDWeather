package com.xdao7.xdweather

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Network
import android.os.Build
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
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.xdao7.xdweather.databinding.ActivityWeatherBinding
import com.xdao7.xdweather.databinding.ItemForecastBinding
import com.xdao7.xdweather.logic.model.Weather
import com.xdao7.xdweather.logic.model.getSky
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.ui.weather.WeatherViewModel
import com.xdao7.xdweather.utils.*
import com.xdao7.xdweather.view.BaseScrollAnimtorView

class WeatherActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context) {
            startActivity<WeatherActivity>(context) {}
        }

        const val SEARCH_PLACE_REQUEST_CODE = 0
    }

    lateinit var binding: ActivityWeatherBinding

    private lateinit var scrollRect: Rect

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decorView.windowInsetsController?.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
            } else {
                val option: Int = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                decorView.systemUiVisibility = option
            }
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        DisplayMetrics().apply {
            scrollRect = Rect(0, 0, widthPixels, heightPixels)
        }

        binding.toolbar.setPadding(0, getStatusBarHeight(), 0, 0)

        initViews()
        initData()
        initListener()
        refreshWeather()
    }

    override fun onDestroy() {
        destroyViews()
        NetworkUtils.removeNetworkCallback(WeatherActivity::class.java.simpleName)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == SEARCH_PLACE_REQUEST_CODE) {
            if (binding.dlHome.isDrawerOpen(GravityCompat.START)) {
                binding.dlHome.closeDrawers()
            }
            val location: Location? = data?.getParcelableExtra("place")
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

    private fun initViews() {
        binding.apply {
            appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                srlWeather.isEnabled = verticalOffset >= 0
                if (verticalOffset < 0) {
                    loadViewAnimator()
                }
            })
            btnCity.setOnClickListener {
                dlHome.openDrawer(GravityCompat.START)
            }
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
                    refreshWeather()
                }
            }
            svWeather.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                loadViewAnimator()
            })
            if (!isNetworkAvailable()) {
                flNetwork.visibility = View.VISIBLE
                Glide.with(this@WeatherActivity).load(R.drawable.ic_network).into(imgNetwork)
            }
        }
    }

    private fun destroyViews() {
        binding.includeWind.apply {
            windmillsBig.stopRotate()
            windmillsSmall.stopRotate()
        }
    }

    private fun initData() {
        viewModel.apply {
            initData()
            weatherLiveData.observe(this@WeatherActivity) { result ->
                val weather = result.getOrNull()
                if (weather != null) {
                    updateCityInfo(weather.cityInfo)
                    showWeatherInfo(weather)
                    sendRefreshCity()
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
        }
    }

    private fun initListener() {
        NetworkUtils.addNetworkCallback(
            WeatherActivity::class.java.simpleName,
            object : NetworkUtils.IConnectivityCallback {
                override fun onAvailable(network: Network?) {
                    runOnUiThread {
                        if (viewModel.needRefresh) {
                            refreshWeather()
                        }
                        if (binding.flNetwork.visibility == View.VISIBLE) {
                            binding.flNetwork.visibility = View.GONE
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

    fun refreshWeather() {
        if (!isNetworkAvailable()) {
            binding.srlWeather.showSnackbar(
                R.string.str_network_disconnected,
                R.string.str_network_setting
            ) {
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
            binding.srlWeather.isRefreshing = false
            viewModel.needRefresh = true
        } else {
            viewModel.refreshWeather()
            binding.srlWeather.isRefreshing = true
        }
    }

    private fun showWeatherInfo(weather: Weather) {
        val realtime = weather.realtime
        val daily = weather.daily
        val life = weather.life
        val air = weather.air

        binding.apply {
            textPlaceName.text = if (viewModel.city.position < 0) {
                viewModel.location.name
            } else {
                viewModel.city.locations[viewModel.city.position].name
            }
            textTemp.text = getString(R.string.str_temp, realtime.temp)
            textSky.text = realtime.text
            textWeek.text = getString(getWeek())
            clBg.setBackgroundResource(getSky(realtime.icon).bg)

            includeForecast.apply {
                llForecast.removeAllViews()
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
                        textSky.text = sky.info
                        textTemperature.text =
                            getString(R.string.str_temp_limit, skycon.tempMin, skycon.tempMax)
                    }
                    llForecast.addView(itemForecastBinding.root)
                }
            }

            includeAir.apply {
                roundBarAir.firstText = air.category
                roundBarAir.secondText = air.aqi.toString()
                roundBarAir.chooseColor(air.level)
                roundBarAir.setProgress(air.aqi.toFloat())
                pm10.text = air.pm10.toString()
                pm2p5.text = air.pm2p5.toString()
                no2.text = air.no2.toString()
                so2.text = air.so2.toString()
                co.text = air.co.toString()
                o3.text = air.o3.toString()
            }

            includeLifeIndex.apply {
                roundBarHumidity.apply {
                    firstText = ""
                    secondText = "${realtime.humidity}%"
                    chooseColor()
                    setProgress(realtime.humidity.toFloat())
                }
                textFeelsLike.text = getString(R.string.str_feels_like, realtime.feelsLike)
                textColdRisk.text = life.flu.brief
                textDressing.text = life.dressing.brief
                textUltraviolet.text = life.uv.brief
                textCarWashing.text = life.carWashing.brief
            }

            includeWind.apply {
                textWindDir.text = realtime.windDir
                textWindScale.text = getString(R.string.str_wind_scale, realtime.windScale)
                windmillsBig.startRotate()
                windmillsSmall.startRotate()
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

    private fun sendRefreshCity() {
        val intent = Intent("com.xdao7.xdweather.refresh.city")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun loadViewAnimator() {
        binding.apply {
            startAnimator(includeAir.roundBarAir)
            startAnimator(includeLifeIndex.roundBarHumidity)
            includeSunMoon.apply {
                startAnimator(sun)
                startAnimator(moon)
            }
        }
    }

    private fun <T : BaseScrollAnimtorView> startAnimator(view: T) {
        view.apply {
            if (getLocalVisibleRect(scrollRect)) {
                startAnimator()
            } else {
                isNeedAnimator = true
            }
        }
    }
}