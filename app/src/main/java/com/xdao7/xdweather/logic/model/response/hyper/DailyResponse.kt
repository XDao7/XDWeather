package com.xdao7.xdweather.logic.model.response.hyper

import com.google.gson.annotations.SerializedName

data class DailyResponse(val results: List<Result>) {

    data class Result(val daily: List<Daily>)

    data class Daily(
        val date: String,
        @SerializedName("text_day") val textDay:String, @SerializedName("code_day") val codeDay: Int,
        @SerializedName("text_night") val textNight:String, @SerializedName("code_night") val codeNight: Int,
        val high: Int, val low: Int,
        @SerializedName("wind_direction") val windDirection: String, @SerializedName("wind_scale") val windScale: Int
    )
}
