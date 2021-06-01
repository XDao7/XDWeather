package com.xdao7.xdweather.logic.model.response.qweather

data class LifeResponse(val code: Int, val daily: List<Daily>) {

    data class Daily(
        val date: String,
        val type: Int,
        val name: String,
        val level: Int,
        val category: String,
        val text: String
    )
}