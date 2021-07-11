package com.xdao7.xdweather.logic.model.response.qweather

data class HourlyResponse(val code: Int, val hourly: List<Hourly>) {

    data class Hourly(
        val fxTime: String,
        val temp: Int,
        val icon: Int,
        val text: String,
        val pop: Int?
    )
}
