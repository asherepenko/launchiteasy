package com.sherepenko.android.launchiteasy.api.json.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.sherepenko.android.launchiteasy.api.OpenWeatherIconMapper
import com.sherepenko.android.launchiteasy.api.json.CurrentWeatherResponse
import com.sherepenko.android.launchiteasy.data.ConditionItem
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.TemperatureItem
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.WindItem
import java.io.IOException
import java.time.Instant

class CurrentWeatherResponseDeserializer : JsonDeserializer<CurrentWeatherResponse>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): CurrentWeatherResponse {
        val jsonRoot: JsonNode = jsonParser.codec.readTree(jsonParser)

        val jsonMain = jsonRoot["main"]
        val jsonLocation = jsonRoot["coord"]
        val jsonWeather = jsonRoot["weather"][0]
        val jsonWind = jsonRoot["wind"]
        val jsonSystem = jsonRoot["sys"]

        return CurrentWeatherResponse(
            currentWeather = WeatherItem(
                TemperatureItem(
                    jsonMain["temp"].floatValue()
                ),
                TemperatureItem(
                    if (jsonMain.hasNonNull("feels_like")) {
                        jsonMain["feels_like"].floatValue()
                    } else {
                        jsonMain["temp"].floatValue()
                    }
                ),
                jsonMain["pressure"].floatValue(),
                jsonMain["humidity"].floatValue(),
                if (jsonRoot.hasNonNull("visibility")) {
                    jsonRoot["visibility"].floatValue()
                } else {
                    null
                },
                ConditionItem(
                    jsonWeather["id"].intValue(),
                    jsonWeather["main"].asText(),
                    jsonWeather["description"].asText(),
                    OpenWeatherIconMapper.toWeatherIcon(jsonWeather["icon"].asText())
                ),
                WindItem(
                    if (jsonWind.hasNonNull("speed")) {
                        jsonWind["speed"].floatValue()
                    } else {
                        0.0f
                    },
                    if (jsonWind.hasNonNull("deg")) {
                        jsonWind["deg"].intValue()
                    } else {
                        null
                    }
                ),
                LocationItem(
                    jsonLocation["lat"].doubleValue(),
                    jsonLocation["lon"].doubleValue(),
                    jsonRoot["id"].intValue(),
                    jsonRoot["name"].asText()
                ),
                Instant.ofEpochSecond(jsonSystem["sunrise"].longValue()),
                Instant.ofEpochSecond(jsonSystem["sunset"].longValue()),
                Instant.ofEpochSecond(jsonRoot["dt"].longValue())
            )
        )
    }
}
