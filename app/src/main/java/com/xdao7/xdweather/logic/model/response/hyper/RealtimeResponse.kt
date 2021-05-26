package com.xdao7.xdweather.logic.model.response.hyper

data class RealtimeResponse(val results: List<Result>) {

    data class Result(val location: Location, val now: Now)

    data class Location(val id: String, val name: String, val path: String)

    data class Now(
        val text: String,
        val code: Int,
        val temperature: Int
    )
}
