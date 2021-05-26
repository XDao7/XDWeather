package com.xdao7.xdweather.logic.model

import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.logic.model.response.qweather.RealtimeResponse

data class City(var position: Int = -1) {
    var locations: ArrayList<Location> = ArrayList()
    var info: ArrayList<RealtimeResponse.Now> = ArrayList()
    var isInit = false
}