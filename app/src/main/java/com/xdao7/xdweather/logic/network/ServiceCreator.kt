package com.xdao7.xdweather.logic.network

import android.util.Log
import com.xdao7.xdweather.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceCreator {

    const val TYPE_GEO = 0
    const val TYPE_QWEATHER = 1

    private const val BASE_QWEATHER_GEO_URL = "https://geoapi.qweather.com/v2/city/"
    private const val BASE_QWEATHER_URL = "https://devapi.qweather.com/v7/"

    private val client = OkHttpClient.Builder().run {
        connectTimeout(6, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)
        readTimeout(6, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            addInterceptor(getOkHttpInterceptor())
        }
        build()
    }

    private val geoRetrofit = Retrofit.Builder()
        .baseUrl(BASE_QWEATHER_GEO_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val qweatherRetrofit = Retrofit.Builder()
        .baseUrl(BASE_QWEATHER_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun getOkHttpInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message: String ->
            Log.d("XDao7", "Https Message:$message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun <T> create(type: Int, serviceClass: Class<T>): T = when(type) {
        TYPE_GEO -> geoRetrofit.create(serviceClass)
        TYPE_QWEATHER -> qweatherRetrofit.create(serviceClass)
        else -> qweatherRetrofit.create(serviceClass)
    }

    inline fun <reified T> create(type: Int): T = create(type, T::class.java)
}