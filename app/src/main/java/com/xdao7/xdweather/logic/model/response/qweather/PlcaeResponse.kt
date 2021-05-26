package com.xdao7.xdweather.logic.model.response.qweather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PlaceResponse(val code: Int, val location: List<Location>)

@Parcelize
data class Location(
    val name: String, val id: String, val lat: Float, val lon: Float,
    val adm2: String, val adm1: String, val country: String
) : Parcelable