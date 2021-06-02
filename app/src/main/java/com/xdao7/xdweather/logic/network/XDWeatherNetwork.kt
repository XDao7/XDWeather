package com.xdao7.xdweather.logic.network

import com.xdao7.xdweather.BuildConfig
import com.xdao7.xdweather.logic.network.callback.qweather.WeatherService
import com.xdao7.xdweather.logic.network.callback.qweather.PlaceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object XDWeatherNetwork {

    private val placeService = ServiceCreator.create<PlaceService>(ServiceCreator.TYPE_GEO)
    private val weatherService = ServiceCreator.create<WeatherService>(ServiceCreator.TYPE_QWEATHER)

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query, BuildConfig.PRIVATE_QWEATHER_KEY).await()

    suspend fun getRealtimeWeather(id: String) = weatherService.getRealtimeWeather(id, BuildConfig.PRIVATE_QWEATHER_KEY).await()

    suspend fun getHourlyWeather(id: String) = weatherService.getHourlyWeather(id, BuildConfig.PRIVATE_QWEATHER_KEY).await()

    suspend fun getDailyWeather(id: String) = weatherService.getDailyWeather(id, BuildConfig.PRIVATE_QWEATHER_KEY).await()

    suspend fun getAir(id: String) = weatherService.getAir(id, BuildConfig.PRIVATE_QWEATHER_KEY).await()

    suspend fun getLifeSuggestion(id: String) = weatherService.getLifeSuggestion(id, BuildConfig.PRIVATE_QWEATHER_KEY).await()

    suspend fun getWarning(id: String) = weatherService.getWaring(id, BuildConfig.PRIVATE_QWEATHER_KEY).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("Response body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}