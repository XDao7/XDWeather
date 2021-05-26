package com.xdao7.xdweather.logic.network.callback.hyper

import com.xdao7.xdweather.logic.model.response.hyper.DailyResponse
import com.xdao7.xdweather.logic.model.response.hyper.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather/now.json?")
    fun getRealtimeWeather(@Query("key") key: String, @Query("location") id: String): Call<RealtimeResponse>

    @GET("weather/daily.json?")
    fun getDailyWeather(@Query("key") key: String, @Query("location") id: String): Call<DailyResponse>
}