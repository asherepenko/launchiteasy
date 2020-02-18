package com.sherepenko.android.launchiteasy.providers

import com.sherepenko.android.launchiteasy.api.OpenWeatherApi
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.db.AppDatabase

interface WeatherDataSource {

    suspend fun getCurrentWeather(locationId: Int): WeatherItem

    suspend fun getWeatherForecasts(locationId: Int): List<ForecastItem>
}

class WeatherLocalDataSource(private val database: AppDatabase) :
    WeatherDataSource {

    override suspend fun getCurrentWeather(locationId: Int): WeatherItem =
        database.getWeatherDao().getCurrentWeather()

    override suspend fun getWeatherForecasts(locationId: Int): List<ForecastItem> =
        database.getForecastDao().getAllWeatherForecasts()

    suspend fun saveCurrentWeather(weather: WeatherItem) {
        database.getWeatherDao().updateCurrentWeather(weather)
    }

    suspend fun saveWeatherForecasts(forecasts: List<ForecastItem>) {
        database.getForecastDao().updateWeatherForecasts(*forecasts.toTypedArray())
    }
}

class WeatherRemoteDataSource(private val weatherApi: OpenWeatherApi) :
    WeatherDataSource {

    override suspend fun getCurrentWeather(locationId: Int): WeatherItem =
        weatherApi.getCurrentWeatherByLocationId(locationId).item

    override suspend fun getWeatherForecasts(locationId: Int): List<ForecastItem> =
        weatherApi.getWeatherForecastByLocationId(locationId).items
}
