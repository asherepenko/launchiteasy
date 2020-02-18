package com.sherepenko.android.launchiteasy.api.json.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.TemperatureItem
import com.sherepenko.android.launchiteasy.api.json.WeatherForecastResponse
import com.sherepenko.android.launchiteasy.data.ConditionItem
import org.threeten.bp.Instant
import java.io.IOException

class WeatherForecastResponseDeserializer : JsonDeserializer<WeatherForecastResponse>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): WeatherForecastResponse {
        val jsonRoot: JsonNode = jsonParser.codec.readTree(jsonParser)

        return WeatherForecastResponse(
            items = jsonRoot["list"]
                .asSequence()
                .map {
                    ForecastItem(
                        TemperatureItem(
                            it["main"]["temp"].floatValue()
                        ),
                        it["main"]["pressure"].floatValue(),
                        it["main"]["humidity"].floatValue(),
                        ConditionItem(
                            it["weather"][0]["id"].intValue(),
                            it["weather"][0]["main"].asText(),
                            it["weather"][0]["description"].asText(),
                            it["weather"][0]["icon"].asText()
                        ),
                        LocationItem(
                            jsonRoot["city"]["id"].intValue(),
                            jsonRoot["city"]["name"].asText(),
                            jsonRoot["city"]["coord"]["lat"].floatValue(),
                            jsonRoot["city"]["coord"]["lon"].floatValue()
                        ),
                        Instant.ofEpochSecond(it["dt"].longValue())
                    )
                }
                .toList()
        )
    }
}
