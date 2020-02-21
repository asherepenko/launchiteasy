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
import com.sherepenko.android.launchiteasy.utils.round
import java.io.IOException
import org.threeten.bp.Instant

class CurrentWeatherResponseDeserializer : JsonDeserializer<CurrentWeatherResponse>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): CurrentWeatherResponse {
        val jsonRoot: JsonNode = jsonParser.codec.readTree(jsonParser)

        return CurrentWeatherResponse(
            currentWeather = WeatherItem(
                TemperatureItem(
                    jsonRoot["main"]["temp"].floatValue()
                ),
                TemperatureItem(
                    jsonRoot["main"]["feels_like"].floatValue()
                ),
                jsonRoot["main"]["pressure"].floatValue(),
                jsonRoot["main"]["humidity"].floatValue(),
                jsonRoot["visibility"].floatValue(),
                ConditionItem(
                    jsonRoot["weather"][0]["id"].intValue(),
                    jsonRoot["weather"][0]["main"].asText(),
                    jsonRoot["weather"][0]["description"].asText(),
                    OpenWeatherIconMapper.toWeatherIcon(jsonRoot["weather"][0]["icon"].asText())
                ),
                WindItem(
                    jsonRoot["wind"]["speed"].floatValue(),
                    jsonRoot["wind"]["deg"].intValue()
                ),
                LocationItem(
                    jsonRoot["coord"]["lat"].doubleValue().round(),
                    jsonRoot["coord"]["lon"].doubleValue().round(),
                    jsonRoot["id"].intValue(),
                    jsonRoot["name"].asText()
                ),
                Instant.ofEpochSecond(jsonRoot["sys"]["sunrise"].longValue()),
                Instant.ofEpochSecond(jsonRoot["sys"]["sunset"].longValue()),
                Instant.ofEpochSecond(jsonRoot["dt"].longValue())
            )
        )
    }
}
