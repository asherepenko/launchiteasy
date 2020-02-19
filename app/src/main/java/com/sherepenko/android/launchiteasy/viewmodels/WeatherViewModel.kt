package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.repositories.WeatherRepository

class WeatherViewModel(
    private val repository: WeatherRepository
) : BaseViewModel() {

    override fun onCleared() {
        super.onCleared()
        repository.dispose()
    }

    fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        repository.getCurrentWeather()

    fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        repository.getWeatherForecasts()
}
