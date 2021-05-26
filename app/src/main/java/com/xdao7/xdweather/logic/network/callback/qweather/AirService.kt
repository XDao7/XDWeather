package com.xdao7.xdweather.logic.network.callback.qweather

import com.xdao7.xdweather.logic.model.response.qweather.AirResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirService {

    @GET("air/now?")
    fun getAir(@Query("location") id: String, @Query("key") key: String): Call<AirResponse>
}