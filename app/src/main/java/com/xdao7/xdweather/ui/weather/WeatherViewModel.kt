package com.xdao7.xdweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.xdao7.xdweather.logic.Repository
import com.xdao7.xdweather.logic.model.City
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.logic.model.response.qweather.RealtimeResponse

class WeatherViewModel : ViewModel() {

    private val cityLiveData = MutableLiveData<City>()
    private val locationLiveData = MutableLiveData<Location>()

    val city = City()

    var needRefresh = false

    lateinit var location: Location

    val weatherLiveData = Transformations.switchMap(cityLiveData) { city ->
        Repository.refreshWeather(city)
    }
    val defaultLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location)
    }

    fun refreshWeather() {
        needRefresh = false
        if (city.position >= 0) {
            if (!city.isInit) {
                initData()
            }
            cityLiveData.value = city
        } else {
            locationLiveData.value = location
        }
    }

    fun initData() {
        if (isPositionSaved() && !city.isInit) {
            city.position = getPosition()
            city.locations = getLocations()
            city.isInit = true
        }
        location = Location("北京", "101010100", 39.90498f, 116.40528f, "北京", "北京市", "中国")
    }

    fun addCity(location: Location) {
        city.apply {
            if (position < 0) {
                locations.clear()
                info.clear()
            }
            if (locations.size < 5) {
                for (i in locations.indices) {
                    if (locations[i].id == location.id) {
                        position = i
                        savePosition(i)
                        return
                    }
                }
                locations.add(location)
                position = locations.size - 1
            }
            if (!isInit) {
                isInit = true
            }
            saveCity()
        }
    }

    fun deleteCity(position: Int) {
        city.apply {
            locations.removeAt(position)
            info.removeAt(position)
            if (this.position == position) {
                needRefresh = true
                if (locations.size == 0) {
                    this.position = -1
                } else {
                    this.position = 0
                }
            } else if (this.position > position) {
                this.position -= 1
            }
            saveCity()
        }
    }

    fun updateCityInfo(info: ArrayList<RealtimeResponse.Now>) {
        city.info = info
    }

    private fun saveCity() {
        city.isInit = false
        Repository.saveCity(city)
    }

    fun savePosition(position: Int) {
        city.position = position
        Repository.savePosition(position)
    }

    private fun isPositionSaved() = Repository.isPositionSaved()

    private fun getLocations() = Repository.getLocations()

    private fun getPosition() = Repository.getPosition()
}