package com.sherepenko.android.launchiteasy.api.json

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.api.json.deserializers.CurrentWeatherResponseDeserializer

@JsonDeserialize(using = CurrentWeatherResponseDeserializer::class)
data class CurrentWeatherResponse(
    val item: WeatherItem
)
