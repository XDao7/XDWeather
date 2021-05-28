package com.xdao7.xdweather.logic.model.response.qweather

data class AirResponse(val code: Int, val now: Now) {

    data class Now(
        val aqi: String,
        val level: Int,
        val category: String,
        val pm10: String,
        val pm2p5: String,
        val no2: String,
        val so2: String,
        val co: String,
        val o3: String
    )
}
