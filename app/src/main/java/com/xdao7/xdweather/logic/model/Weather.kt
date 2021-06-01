package com.xdao7.xdweather.logic.model

import com.xdao7.xdweather.logic.model.response.qweather.DailyResponse
import com.xdao7.xdweather.logic.model.response.qweather.LifeResponse
import com.xdao7.xdweather.logic.model.response.qweather.AirResponse
import com.xdao7.xdweather.logic.model.response.qweather.RealtimeResponse

data class Weather(
    val realtime: RealtimeResponse.Now,
    val daily: List<DailyResponse.Daily>,
    val air: AirResponse.Now,
    val life: List<LifeResponse.Daily>,
    val cityInfo: ArrayList<RealtimeResponse.Now>
)
