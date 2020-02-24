package com.sherepenko.android.launchiteasy.viewmodels

import androidx.lifecycle.LiveData
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.repositories.WeatherRepository

class WeatherViewModel(
    repository: WeatherRepository
) : BaseViewModel<WeatherRepository>(repository) {

    fun getCurrentWeather(): LiveData<Resource<WeatherItem>> =
        repository.getCurrentWeather()

    fun getWeatherForecasts(): LiveData<Resource<List<ForecastItem>>> =
        repository.getWeatherForecasts()
}
