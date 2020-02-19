package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.utils.Resource
import com.sherepenko.android.launchiteasy.repositories.WeatherRepository

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        repository.dispose()
    }

    fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        repository.getCurrentWeather()

    fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        repository.getWeatherForecasts()

    fun updateCurrentLocation(latitude: Double, longitude: Double) {
        repository.updateCurrentLocation(latitude, longitude)
    }
}
