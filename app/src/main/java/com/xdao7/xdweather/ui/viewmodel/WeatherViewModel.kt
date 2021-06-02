package com.xdao7.xdweather.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.xdao7.xdweather.logic.Repository
import com.xdao7.xdweather.logic.model.City
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.logic.model.response.qweather.RealtimeResponse
import com.xdao7.xdweather.utils.LocationUtils

class WeatherViewModel : ViewModel() {

    private val cityLiveData = MutableLiveData<City>()
    private val capitalLiveData = MutableLiveData<Location>()
    private val searchLiveData = MutableLiveData<String>()

    val city = City()

    var needRefresh = false

    val capital: Location = Location("北京", "101010100", 39.90498f, 116.40528f, "北京", "北京市", "中国")

    val weatherLiveData = Transformations.switchMap(cityLiveData) { city ->
        Repository.refreshWeather(city)
    }
    val defaultLiveData = Transformations.switchMap(capitalLiveData) { location ->
        Repository.refreshWeather(location)
    }
    val locationLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    /**
     * 刷新定位
     *
     * @param block 无法获取定位后需要执行的方法
     */
    fun refreshLocation(block: () -> Unit) {
        LocationUtils.getLocation { location ->
            if (location != null) {
                searchLocation("${location.longitude},${location.latitude}")
            } else {
                block()
            }
        }
    }

    /**
     * 刷新天气数据
     */
    fun refreshWeather() {
        needRefresh = false
        if (city.locations.isNotEmpty()) {
            cityLiveData.value = city
        } else {
            capitalLiveData.value = capital
        }
    }

    /**
     * 根据定位查询城市
     */
    private fun searchLocation(query: String) {
        searchLiveData.postValue(query)
    }

    /**
     * 获取保存的城市信息
     */
    fun initData() {
        if (isLocationSaved()) {
            val saveLocations = getLocations()
            if (saveLocations.isNotEmpty()) {
                city.position = 0
                city.locations.addAll(getLocations())
            }
        }
    }

    /**
     * 添加定位城市
     */
    fun addLocation(location: Location) {
        city.apply {
            location.isCurrentLocation = true
            locations.apply {
                if (isNotEmpty() && this[0].isCurrentLocation) {
                    locations.removeAt(0)
                } else if (position > 0) {
                    position += 1
                } else {
                    position = 0
                }
                add(0, location)
            }
        }
    }

    /**
     * 手动添加城市
     */
    fun addCity(location: Location) {
        city.apply {
            val size = if (locations.isNotEmpty() && locations[0].isCurrentLocation) {
                5
            } else {
                4
            }
            if (locations.size < size) {
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
            saveCity()
        }
    }

    /**
     * 删除城市
     */
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

    /**
     * 更新城市天气数据
     */
    fun updateCityInfo(info: ArrayList<RealtimeResponse.Now>) {
        city.apply {
            if (position < 0) {
                position = 0
            }
            this.info = info
        }
    }

    /**
     * 保存城市列表
     */
    private fun saveCity() {
        Repository.saveCity(city)
    }

    /**
     * 记录当前选中的城市
     */
    fun savePosition(position: Int) {
        city.position = position
    }

    private fun getLocations() = Repository.getLocations()

    private fun isLocationSaved() = Repository.isLocationsSaved()
}