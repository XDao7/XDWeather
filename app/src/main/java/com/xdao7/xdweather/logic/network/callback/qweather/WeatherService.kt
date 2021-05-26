package com.xdao7.xdweather.logic.network.callback.qweather

import com.xdao7.xdweather.logic.model.response.qweather.DailyResponse
import com.xdao7.xdweather.logic.model.response.qweather.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather/now?")
    fun getRealtimeWeather(@Query("location") id: String, @Query("key") key: String): Call<RealtimeResponse>

    @GET("weather/3d?")
    fun getDailyWeather(@Query("location") id: String, @Query("key") key: String): Call<DailyResponse>
}