package com.xdao7.xdweather.logic.network.callback.hyper

import com.xdao7.xdweather.logic.model.response.hyper.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("location/search.json?")
    fun searchPlaces(@Query("key") key: String, @Query("q") query: String): Call<PlaceResponse>
}