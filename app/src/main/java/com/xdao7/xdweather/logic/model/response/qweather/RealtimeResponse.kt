package com.xdao7.xdweather.logic.model.response.qweather

data class RealtimeResponse(val code: Int, val now: Now) {

    data class Now(
        val temp: Int, val feelsLike: Int, val icon: Int, val text: String,
        val windDir: String, val windScale: Int, val humidity: Int
    )
}