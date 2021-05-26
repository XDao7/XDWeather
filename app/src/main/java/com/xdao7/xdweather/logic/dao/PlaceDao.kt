package com.xdao7.xdweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xdao7.xdweather.XDWeatherApplication
import com.xdao7.xdweather.logic.model.response.qweather.Location

object PlaceDao {

    private fun sharePreferences() = XDWeatherApplication.context.getSharedPreferences("xd_weather", Context.MODE_PRIVATE)

    fun saveLocations(locations: ArrayList<Location>) {
        sharePreferences().edit {
            putString("locations", Gson().toJson(locations))
        }
    }

    fun getLocations(): ArrayList<Location> {
        val placeJson = sharePreferences().getString("locations", "")
        return Gson().fromJson(placeJson, object : TypeToken<List<Location>>(){}.type)
    }

    fun isLocationsSaved() = sharePreferences().contains("locations")

    fun savePosition(position: Int) {
        sharePreferences().edit {
            putInt("position", position)
        }
    }

    fun getPosition(): Int {
        return sharePreferences().getInt("position", 0)
    }

    fun isPositionSaved(): Boolean {
        return if (sharePreferences().contains("position")) {
            getPosition() >= 0
        } else {
            false
        }

    }
}