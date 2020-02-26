package com.sherepenko.android.launchiteasy.api

import com.sherepenko.android.launchiteasy.AppConstants
import com.sherepenko.android.launchiteasy.api.json.CurrentWeatherResponse
import com.sherepenko.android.launchiteasy.api.json.WeatherForecastResponse
import java.util.Locale
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
    }

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherByLocationId(
        @Query("id") locationId: Int,
        @Query("lang") language: String = Locale.ENGLISH.language,
        @Query("appId") apiKey: String = AppConstants.OPEN_WEATHER_API_KEY
    ): CurrentWeatherResponse

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String = Locale.ENGLISH.language,
        @Query("appId") apiKey: String = AppConstants.OPEN_WEATHER_API_KEY
    ): CurrentWeatherResponse

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") city: String,
        @Query("lang") language: String = Locale.ENGLISH.language,
        @Query("appId") apiKey: String = AppConstants.OPEN_WEATHER_API_KEY
    ): CurrentWeatherResponse

    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecastsByLocationId(
        @Query("id") locationId: Int,
        @Query("cnt") count: Int? = null,
        @Query("lang") language: String = Locale.ENGLISH.language,
        @Query("appId") apiKey: String = AppConstants.OPEN_WEATHER_API_KEY
    ): WeatherForecastResponse

    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecastsByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("cnt") count: Int? = null,
        @Query("lang") language: String = Locale.ENGLISH.language,
        @Query("appId") apiKey: String = AppConstants.OPEN_WEATHER_API_KEY
    ): WeatherForecastResponse

    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecastsByCity(
        @Query("q") city: String,
        @Query("cnt") count: Int? = null,
        @Query("lang") language: String = Locale.ENGLISH.language,
        @Query("appId") apiKey: String = AppConstants.OPEN_WEATHER_API_KEY
    ): WeatherForecastResponse
}
