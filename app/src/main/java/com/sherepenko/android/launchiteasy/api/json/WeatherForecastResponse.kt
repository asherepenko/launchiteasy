package com.sherepenko.android.launchiteasy.api.json

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.sherepenko.android.launchiteasy.api.json.deserialization.WeatherForecastResponseDeserializer
import com.sherepenko.android.launchiteasy.data.ForecastItem

@JsonDeserialize(using = WeatherForecastResponseDeserializer::class)
data class WeatherForecastResponse(
    val items: List<ForecastItem>
)
