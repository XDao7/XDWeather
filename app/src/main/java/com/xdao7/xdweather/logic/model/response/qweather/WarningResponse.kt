package com.xdao7.xdweather.logic.model.response.qweather

data class WarningResponse(val code: Int, val warning: List<Warning>) {

    data class Warning(
        val id: String,
        val title: String,
        val level: String,
        val typeName: String
    )
}