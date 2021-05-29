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
        val saveLocations: ArrayList<Location> = ArrayList()
        if (locations.isNotEmpty() && locations[0].isCurrentLocation) {
            for (i in 1 until locations.size) {
                saveLocations.add(locations[i])
            }
        } else {
            saveLocations.addAll(locations)
        }
        sharePreferences().edit {
            putString("locations", Gson().toJson(saveLocations))
        }
    }

    fun getLocations(): ArrayList<Location> {
        val placeJson = sharePreferences().getString("locations", "")
        return Gson().fromJson(placeJson, object : TypeToken<List<Location>>(){}.type)
    }

    fun isLocationsSaved() = sharePreferences().contains("locations")
}