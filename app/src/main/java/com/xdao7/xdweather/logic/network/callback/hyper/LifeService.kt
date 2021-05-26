package com.xdao7.xdweather.logic.network.callback.hyper

import com.xdao7.xdweather.logic.model.response.hyper.LifeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LifeService {

    @GET("life/suggestion.json?")
    fun getLifeSuggestion(@Query("key") key: String, @Query("location") location: String): Call<LifeResponse>
}