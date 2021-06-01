package com.xdao7.xdweather.logic.network.callback.qweather

import com.xdao7.xdweather.logic.model.response.qweather.AirResponse
import com.xdao7.xdweather.logic.model.response.qweather.DailyResponse
import com.xdao7.xdweather.logic.model.response.qweather.LifeResponse
import com.xdao7.xdweather.logic.model.response.qweather.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather/now?")
    fun getRealtimeWeather(
        @Query("location") id: String,
        @Query("key") key: String
    ): Call<RealtimeResponse>

    @GET("weather/3d?")
    fun getDailyWeather(
        @Query("location") id: String,
        @Query("key") key: String
    ): Call<DailyResponse>

    @GET("air/now?")
    fun getAir(@Query("location") id: String, @Query("key") key: String): Call<AirResponse>

    /**
     * 获取生活指数
     * type中数值对应关系如下：
     * 2 -> 洗车指数
     * 3 -> 穿衣指数
     * 5 -> 紫外线指数
     * 9 -> 感冒指数
     * 详细列表见https://dev.qweather.com/docs/api/indices/
     */
    @GET("indices/1d?")
    fun getLifeSuggestion(
        @Query("location") id: String,
        @Query("key") key: String,
        @Query("type") type: String = "2,3,5,9"
    ): Call<LifeResponse>
}