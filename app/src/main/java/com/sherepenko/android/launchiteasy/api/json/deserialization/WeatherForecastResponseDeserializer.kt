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
import com.sherepenko.android.launchiteasy.utils.round
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
                    ForecastItem(
                        TemperatureItem(
                            it["main"]["temp"].floatValue()
                        ),
                        TemperatureItem(
                            it["main"]["feels_like"].floatValue()
                        ),
                        it["main"]["pressure"].floatValue(),
                        it["main"]["humidity"].floatValue(),
                        ConditionItem(
                            it["weather"][0]["id"].intValue(),
                            it["weather"][0]["main"].asText(),
                            it["weather"][0]["description"].asText(),
                            OpenWeatherIconMapper.toWeatherIcon(it["weather"][0]["icon"].asText())
                        ),
                        WindItem(
                            it["wind"]["speed"].floatValue(),
                            it["wind"]["deg"].intValue()
                        ),
                        LocationItem(
                            jsonRoot["city"]["coord"]["lat"].doubleValue().round(),
                            jsonRoot["city"]["coord"]["lon"].doubleValue().round(),
                            jsonRoot["city"]["id"].intValue(),
                            jsonRoot["city"]["name"].asText()
                        ),
                        Instant.ofEpochSecond(it["dt"].longValue())
                    )
                }
                .toList()
        )
    }
}
