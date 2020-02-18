package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

@Entity(tableName = "weather_forecast")
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
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)
