package com.xdao7.xdweather.logic.model.response.hyper

data class PlaceResponse(val results: List<Place>)

data class Place(val id: String, val name: String, val path: String)
