package com.sherepenko.android.launchiteasy.api

import com.sherepenko.android.launchiteasy.BuildConfig
import com.sherepenko.android.launchiteasy.api.json.CurrentWeatherResponse
import com.sherepenko.android.launchiteasy.api.json.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
    }

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherByLocationId(
        @Query("id") locationId: Int,
        @Query("units") units: String? = null,
        @Query("appId") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): CurrentWeatherResponse

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String? = null,
        @Query("appId") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): CurrentWeatherResponse

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String? = null,
        @Query("appId") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): CurrentWeatherResponse

    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecastByLocationId(
        @Query("id") locationId: Int,
        @Query("units") units: String? = null,
        @Query("appId") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): WeatherForecastResponse

    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecastByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String? = null,
        @Query("appId") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): WeatherForecastResponse

    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecastByCity(
        @Query("q") city: String,
        @Query("appId") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): WeatherForecastResponse
}
