package com.xdao7.xdweather.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.xdao7.xdweather.logic.model.response.qweather.DailyResponse

class DailyViewModel : ViewModel() {

    lateinit var dailyWeather: ArrayList<DailyResponse.Daily>
}