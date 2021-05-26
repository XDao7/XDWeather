package com.xdao7.xdweather.logic.model.response.hyper

import com.google.gson.annotations.SerializedName

data class LifeResponse(val results: List<Result>) {

    data class Result(val suggestion: Suggestion)

    data class Suggestion(@SerializedName("car_washing") val carWashing: Life, val dressing: Life, val flu: Life,
                          val sport: Life, val travel: Life, val uv: Life
    )

    data class Life(val brief: String)
}