package com.xdao7.xdweather.logic.model.response.qweather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class DailyResponse(val code: Int, val daily: ArrayList<Daily>) {

    @Parcelize
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
        val wind360Day: Int,
        val windDirDay: String,
        val windScaleDay: String,
        val windSpeedDay: Int,
        val wind360Night: Int,
        val windDirNight: String,
        val windScaleNight: String,
        val windSpeedNight: Int,
        val tempMax: Int,
        val tempMin: Int,
        val humidity: Int,
        val precip: Float,
        val uvIndex: Int,
        val pressure: Int,
        val vis: Int,
        val cloud: Int
    ) : Parcelable
}
