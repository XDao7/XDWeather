package com.xdao7.xdweather.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xdao7.xdweather.R
import com.xdao7.xdweather.databinding.ItemDailyBinding
import com.xdao7.xdweather.logic.model.getSky
import com.xdao7.xdweather.logic.model.response.qweather.DailyResponse
import com.xdao7.xdweather.utils.getWeek

class DailyAdapter(
    private val context: Context,
    private val dailyList: ArrayList<DailyResponse.Daily>
) :
    RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemDailyBinding) : RecyclerView.ViewHolder(binding.root) {
        val textWeek = binding.textWeek
        val textDate = binding.textDate
        val textTempMax = binding.textTempMax
        val textTempMin = binding.textTempMin
        val imageDay = binding.imageDay
        val textDay = binding.textDay
        val textDay360 = binding.textDay360
        val textDayDirection = binding.textDayDirection
        val textDayScale = binding.textDayScale
        val textDaySpeed = binding.textDaySpeed
        val imageNight = binding.imageNight
        val textNight = binding.textNight
        val textNight360 = binding.textNight360
        val textNightDirection = binding.textNightDirection
        val textNightScale = binding.textNightScale
        val textNightSpeed = binding.textNightSpeed
        val textPrecip = binding.textPrecip
        val textUvIndex = binding.textUvIndex
        val textHumidity = binding.textHumidity
        val textPressure = binding.textPressure
        val textVis = binding.textVis
        val textCloud = binding.textCloud
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val daily = dailyList[position]
        val daySky = getSky(daily.iconDay)
        val nightSky = getSky(daily.iconNight)
        holder.apply {
            textWeek.text = context.getString(getWeek(position))
            textDate.text = daily.fxDate
            textTempMax.text = daily.tempMax.toString()
            textTempMin.text = context.getString(R.string.str_daily_temp, daily.tempMin)
            imageDay.setImageResource(daySky.smallIcon)
            textDay.text = daySky.info
            textDay360.text = context.getString(R.string.str_daily_360, daily.wind360Day)
            textDayDirection.text = daily.windDirDay
            textDayScale.text = context.getString(R.string.str_daily_scale, daily.windScaleDay)
            textDaySpeed.text = context.getString(R.string.str_daily_speed, daily.windSpeedDay)
            imageNight.setImageResource(nightSky.smallIcon)
            textNight.text = nightSky.info
            textNight360.text = context.getString(R.string.str_daily_360, daily.wind360Night)
            textNightDirection.text = daily.windDirNight
            textNightScale.text = context.getString(R.string.str_daily_scale, daily.windScaleNight)
            textNightSpeed.text = context.getString(R.string.str_daily_speed, daily.windSpeedNight)
            textPrecip.text = context.getString(R.string.str_daily_precip, daily.precip)
            textUvIndex.text = context.getString(R.string.str_daily_uvIndex, daily.uvIndex)
            textHumidity.text = context.getString(R.string.str_daily_humidity, daily.humidity)
            textPressure.text = context.getString(R.string.str_daily_pressure, daily.pressure)
            textVis.text = context.getString(R.string.str_daily_vis, daily.vis)
            textCloud.text = context.getString(R.string.str_daily_cloud, daily.cloud)
        }
    }

    override fun getItemCount() = dailyList.size
}