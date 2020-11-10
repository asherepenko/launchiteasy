package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "current_weather",
    indices = [
        Index(value = ["location_id"]),
        Index(value = ["location_name"])
    ]
)
data class WeatherItem(
    @Embedded(prefix = "temperature_")
    val temperature: TemperatureItem,
    @Embedded(prefix = "perceived_temperature_")
    val perceivedTemperature: TemperatureItem,
    @ColumnInfo(name = "pressure")
    val pressure: Float,
    @ColumnInfo(name = "humidity")
    val humidity: Float,
    @ColumnInfo(name = "visibility")
    val visibility: Float?,
    @Embedded(prefix = "condition_")
    val condition: ConditionItem,
    @Embedded(prefix = "wind_")
    val wind: WindItem,
    @Embedded(prefix = "location_")
    val location: LocationItem,
    @ColumnInfo(name = "sunrise")
    val sunrise: Instant,
    @ColumnInfo(name = "sunset")
    val sunset: Instant,
    @ColumnInfo(name = "timestamp")
    val timestamp: Instant,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant = Instant.now(),
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)
