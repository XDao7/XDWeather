package com.xdao7.xdweather.logic

import androidx.lifecycle.liveData
import com.xdao7.xdweather.logic.dao.PlaceDao
import com.xdao7.xdweather.logic.model.City
import com.xdao7.xdweather.logic.model.Weather
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.logic.model.response.qweather.RealtimeResponse
import com.xdao7.xdweather.logic.network.XDWeatherNetwork
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire {
        val placeResponse = XDWeatherNetwork.searchPlaces(query)
        val places = placeResponse.location
        if (places.isNotEmpty()) {
            Result.success(places)
        } else {
            Result.failure(object : RuntimeException("place not found") {})
        }
    }

    fun refreshWeather(city: City) = fire {
        coroutineScope {
            val chooseCity = city.locations[city.position]
            val deferredHourly = async {
                XDWeatherNetwork.getHourlyWeather(chooseCity.id)
            }
            val deferredDaily = async {
                XDWeatherNetwork.getDailyWeather(chooseCity.id)
            }
            val deferredLife = async {
                XDWeatherNetwork.getLifeSuggestion(chooseCity.id)
            }
            val deferredAir = async {
                XDWeatherNetwork.getAir(chooseCity.id)
            }
            val deferredList: ArrayList<Deferred<RealtimeResponse>> = ArrayList()
            for (i in city.locations.indices) {
                deferredList.add(async {
                    XDWeatherNetwork.getRealtimeWeather(city.locations[i].id)
                })
            }

            val hourlyResponse = deferredHourly.await()
            val dailyResponse = deferredDaily.await()
            val lifeResponse = deferredLife.await()
            val airResponse = deferredAir.await()
            val cityWeather: ArrayList<RealtimeResponse.Now> = ArrayList()
            val realtimeResponse = deferredList[city.position].await()
            for (i in city.locations.indices) {
                if (i == city.position) {
                    cityWeather.add(realtimeResponse.now)
                } else {
                    cityWeather.add(deferredList[i].await().now)
                }
            }

            val weather = Weather(
                realtimeResponse.now,
                hourlyResponse.hourly,
                dailyResponse.daily,
                airResponse.now,
                lifeResponse.daily,
                cityWeather
            )
            Result.success(weather)
        }
    }

    fun refreshWeather(location: Location) = fire {
        coroutineScope {
            val deferredRealtime = async {
                XDWeatherNetwork.getRealtimeWeather(location.id)
            }
            val deferredHourly = async {
                XDWeatherNetwork.getHourlyWeather(location.id)
            }
            val deferredDaily = async {
                XDWeatherNetwork.getDailyWeather(location.id)
            }
            val deferredAir = async {
                XDWeatherNetwork.getAir(location.id)
            }
            val deferredLife = async {
                XDWeatherNetwork.getLifeSuggestion(location.id)
            }

            val realtimeResponse = deferredRealtime.await()
            val hourlyResponse = deferredHourly.await()
            val dailyResponse = deferredDaily.await()
            val airResponse = deferredAir.await()
            val lifeResponse = deferredLife.await()

            val weather = Weather(
                realtimeResponse.now,
                hourlyResponse.hourly,
                dailyResponse.daily,
                airResponse.now,
                lifeResponse.daily,
                ArrayList()
            )
            Result.success(weather)
        }
    }

    private fun <T> fire(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend () -> Result<T>
    ) = liveData(context) {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun saveCity(city: City) {
        PlaceDao.saveLocations(city.locations)
    }

    fun getLocations() = PlaceDao.getLocations()

    fun isLocationsSaved() = PlaceDao.isLocationsSaved()
}