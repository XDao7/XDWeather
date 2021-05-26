package com.xdao7.xdweather.logic.model.response.qweather

data class AirResponse(val code: Int, val now: Now) {

    data class Now(
        val aqi: Int,
        val level: Int,
        val category: String,
        val pm10: Float,
        val pm2p5: Float,
        val no2: Float,
        val so2: Float,
        val co: Float,
        val o3: Float
    )
}
