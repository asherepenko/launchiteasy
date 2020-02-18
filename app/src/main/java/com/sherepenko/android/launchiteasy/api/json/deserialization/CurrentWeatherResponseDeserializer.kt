package com.sherepenko.android.launchiteasy.api.json.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.TemperatureItem
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.api.json.CurrentWeatherResponse
import com.sherepenko.android.launchiteasy.data.ConditionItem
import org.threeten.bp.Instant
import java.io.IOException

class CurrentWeatherResponseDeserializer : JsonDeserializer<CurrentWeatherResponse>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): CurrentWeatherResponse {
        val jsonRoot: JsonNode = jsonParser.codec.readTree(jsonParser)

        return CurrentWeatherResponse(
            item = WeatherItem(
                TemperatureItem(
                    jsonRoot["main"]["temp"].floatValue()
                ),
                jsonRoot["main"]["pressure"].floatValue(),
                jsonRoot["main"]["humidity"].floatValue(),
                ConditionItem(
                    jsonRoot["weather"][0]["id"].intValue(),
                    jsonRoot["weather"][0]["main"].asText(),
                    jsonRoot["weather"][0]["description"].asText(),
                    jsonRoot["weather"][0]["icon"].asText()
                ),
                LocationItem(
                    jsonRoot["id"].intValue(),
                    jsonRoot["name"].asText(),
                    jsonRoot["coord"]["lat"].floatValue(),
                    jsonRoot["coord"]["lon"].floatValue()
                ),
                Instant.ofEpochSecond(jsonRoot["dt"].longValue())
            )
        )
    }
}
