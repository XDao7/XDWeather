package com.xdao7.xdweather.logic.network.callback.qweather

import com.xdao7.xdweather.logic.model.response.qweather.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("lookup?")
    fun searchPlaces(@Query("location") query: String, @Query("key") key: String): Call<PlaceResponse>
}