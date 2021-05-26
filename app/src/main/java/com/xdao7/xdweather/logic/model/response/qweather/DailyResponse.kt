package com.xdao7.xdweather.logic.model.response.qweather

data class DailyResponse(val code: Int, val daily: List<Daily>) {

    data class Daily(
        val fxDate: String,
        val sunrise: String,
        val sunset: String,
        val moonrise: String,
        val moonset: String,
        val moonPhase: String,
        val iconDay: Int,
        val textDay: String,
        val iconNight: Int,
        val textNight: String,
        val tempMax: Int,
        val tempMin: Int
    )
}
