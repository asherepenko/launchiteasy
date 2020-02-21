package com.sherepenko.android.launchiteasy.api.json.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.sherepenko.android.launchiteasy.api.OpenWeatherIconMapper
import com.sherepenko.android.launchiteasy.api.json.WeatherForecastResponse
import com.sherepenko.android.launchiteasy.data.ConditionItem
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.TemperatureItem
import com.sherepenko.android.launchiteasy.data.WindItem
import java.io.IOException
import org.threeten.bp.Instant

class WeatherForecastResponseDeserializer : JsonDeserializer<WeatherForecastResponse>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): WeatherForecastResponse {
        val jsonRoot: JsonNode = jsonParser.codec.readTree(jsonParser)

        return WeatherForecastResponse(
            weatherForecasts = jsonRoot["list"]
                .asSequence()
                .map {
                    val jsonMain = it["main"]
                    val jsonCity = jsonRoot["city"]
                    val jsonLocation = jsonCity["coord"]
                    val jsonWeather = it["weather"][0]
                    val jsonWind = it["wind"]

                    ForecastItem(
                        TemperatureItem(
                            jsonMain["temp"].floatValue()
                        ),
                        TemperatureItem(
                            jsonMain["feels_like"].floatValue()
                        ),
                        jsonMain["pressure"].floatValue(),
                        jsonMain["humidity"].floatValue(),
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
                            jsonCity["id"].intValue(),
                            jsonCity["name"].asText()
                        ),
                        Instant.ofEpochSecond(it["dt"].longValue())
                    )
                }
                .toList()
        )
    }
}
