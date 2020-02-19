package com.sherepenko.android.launchiteasy.api.json.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.sherepenko.android.launchiteasy.api.json.WeatherForecastResponse
import com.sherepenko.android.launchiteasy.data.ConditionItem
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.LocationItem
import com.sherepenko.android.launchiteasy.data.TemperatureItem
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
                            jsonRoot["city"]["coord"]["lat"].doubleValue(),
                            jsonRoot["city"]["coord"]["lon"].doubleValue(),
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
