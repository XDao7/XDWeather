package com.xdao7.xdweather.logic.model

import com.xdao7.xdweather.logic.model.response.qweather.*

data class Weather(
    val realtime: RealtimeResponse.Now,
    val hourly: List<HourlyResponse.Hourly>,
    val daily: ArrayList<DailyResponse.Daily>,
    val air: AirResponse.Now,
    val life: List<LifeResponse.Daily>,
    val cityInfo: ArrayList<RealtimeResponse.Now>,
    val warning: List<WarningResponse.Warning>?
)
