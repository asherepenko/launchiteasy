package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

@Entity(
    tableName = "weather_forecasts",
    indices = [
        Index(value = ["location_id"]),
        Index(value = ["location_name"])
    ]
)
data class ForecastItem(
    @Embedded(prefix = "temperature_")
    val temperature: TemperatureItem,
    @ColumnInfo(name = "pressure")
    val pressure: Float,
    @ColumnInfo(name = "humidity")
    val humidity: Float,
    @Embedded(prefix = "condition_")
    val condition: ConditionItem,
    @Embedded(prefix = "location_")
    val location: LocationItem,
    @ColumnInfo(name = "timestamp")
    val timestamp: Instant,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant = Instant.now(),
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)
