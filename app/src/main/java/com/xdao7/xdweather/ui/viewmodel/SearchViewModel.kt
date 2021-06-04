package com.xdao7.xdweather.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.xdao7.xdweather.logic.Repository
import com.xdao7.xdweather.logic.model.response.qweather.Location

class SearchViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Location>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query: String ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }
}