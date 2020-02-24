package com.sherepenko.android.launchiteasy.providers

import com.sherepenko.android.launchiteasy.AppConstants
import com.sherepenko.android.launchiteasy.api.OpenWeatherApi
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.db.AppDatabase

interface WeatherDataSource {

    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherItem

    suspend fun getWeatherForecasts(latitude: Double, longitude: Double): List<ForecastItem>
}

class WeatherLocalDataSource(
    database: AppDatabase,
    private val forecastsCount: Int = AppConstants.WEATHER_FORECASTS_LIMIT
) : WeatherDataSource {

    private val weatherDao = database.getWeatherDao()

    private val forecastDao = database.getForecastDao()

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherItem =
        weatherDao.getCurrentWeather()

    override suspend fun getWeatherForecasts(
        latitude: Double,
        longitude: Double
    ): List<ForecastItem> =
        forecastDao.getWeatherForecasts(forecastsCount)

    suspend fun saveCurrentWeather(weather: WeatherItem) {
        weatherDao.updateCurrentWeather(weather)
    }

    suspend fun saveWeatherForecasts(forecasts: List<ForecastItem>) {
        forecastDao.updateWeatherForecasts(*forecasts.toTypedArray())
    }
}

class WeatherRemoteDataSource(private val weatherApi: OpenWeatherApi) :
    WeatherDataSource {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherItem =
        weatherApi.getCurrentWeatherByLocation(latitude, longitude).currentWeather

    override suspend fun getWeatherForecasts(
        latitude: Double,
        longitude: Double
    ): List<ForecastItem> =
        weatherApi.getWeatherForecastsByLocation(latitude, longitude).weatherForecasts
}
