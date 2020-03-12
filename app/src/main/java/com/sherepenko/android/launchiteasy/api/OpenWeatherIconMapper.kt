package com.sherepenko.android.launchiteasy.api

import com.sherepenko.android.launchiteasy.data.WeatherIcon

object OpenWeatherIconMapper {
    private const val CLEAR_SKY_DAY = "01d"
    private const val CLEAR_SKY_NIGHT = "01n"
    private const val FEW_CLOUDS_DAY = "02d"
    private const val FEW_CLOUDS_NIGHT = "02n"
    private const val SCATTERED_CLOUDS_DAY = "03d"
    private const val SCATTERED_CLOUDS_NIGHT = "03n"
    private const val BROKEN_CLOUDS_DAY = "04d"
    private const val BROKEN_CLOUDS_NIGHT = "04n"
    private const val SHOWER_RAIN_DAY = "09d"
    private const val SHOWER_RAIN_NIGHT = "09n"
    private const val RAIN_DAY = "10d"
    private const val RAIN_NIGHT = "10n"
    private const val THUNDERSTORM_DAY = "11d"
    private const val THUNDERSTORM_NIGHT = "11n"
    private const val SNOW_DAY = "13d"
    private const val SNOW_NIGHT = "13n"
    private const val MIST_DAY = "50d"
    private const val MIST_NIGHT = "50n"

    private val UNKNOWN_WEATHER = WeatherIcon(0xf07b.toChar().toString())

    private val weatherIcons = mapOf(
        CLEAR_SKY_DAY to WeatherIcon(0xf00d.toChar().toString()),
        CLEAR_SKY_NIGHT to WeatherIcon(0xf02e.toChar().toString()),
        FEW_CLOUDS_DAY to WeatherIcon(0xf002.toChar().toString()),
        FEW_CLOUDS_NIGHT to WeatherIcon(0xf086.toChar().toString()),
        SCATTERED_CLOUDS_DAY to WeatherIcon(0xf041.toChar().toString()),
        SCATTERED_CLOUDS_NIGHT to WeatherIcon(0xf041.toChar().toString()),
        BROKEN_CLOUDS_DAY to WeatherIcon(0xf013.toChar().toString()),
        BROKEN_CLOUDS_NIGHT to WeatherIcon(0xf013.toChar().toString()),
        SHOWER_RAIN_DAY to WeatherIcon(0xf019.toChar().toString()),
        SHOWER_RAIN_NIGHT to WeatherIcon(0xf019.toChar().toString()),
        RAIN_DAY to WeatherIcon(0xf008.toChar().toString()),
        RAIN_NIGHT to WeatherIcon(0xf036.toChar().toString()),
        THUNDERSTORM_DAY to WeatherIcon(0xf00e.toChar().toString()),
        THUNDERSTORM_NIGHT to WeatherIcon(0xf03a.toChar().toString()),
        SNOW_DAY to WeatherIcon(0xf076.toChar().toString()),
        SNOW_NIGHT to WeatherIcon(0xf076.toChar().toString()),
        MIST_DAY to WeatherIcon(0xf003.toChar().toString()),
        MIST_NIGHT to WeatherIcon(0xf04a.toChar().toString())
    )

    fun toWeatherIcon(openWeatherIcon: String): WeatherIcon =
        weatherIcons[openWeatherIcon] ?: UNKNOWN_WEATHER
}
