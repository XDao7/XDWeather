package com.xdao7.xdweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.xdao7.xdweather.utils.NetworkUtils

class XDWeatherApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        NetworkUtils.register(this)
    }
}