package com.sherepenko.android.launchiteasy.api.json

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.sherepenko.android.launchiteasy.api.json.deserialization.CurrentWeatherResponseDeserializer
import com.sherepenko.android.launchiteasy.data.WeatherItem

@JsonDeserialize(using = CurrentWeatherResponseDeserializer::class)
data class CurrentWeatherResponse(
    val currentWeather: WeatherItem
)
