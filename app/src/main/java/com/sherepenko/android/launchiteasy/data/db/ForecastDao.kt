package com.sherepenko.android.launchiteasy.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sherepenko.android.launchiteasy.data.ForecastItem
import java.time.Instant

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherForecasts(vararg forecasts: ForecastItem)

    @Delete
    suspend fun deleteWeatherForecasts(vararg forecasts: ForecastItem)

    @Query("DELETE FROM weather_forecasts")
    suspend fun deleteAllWeatherForecasts()

    @Transaction
    suspend fun updateWeatherForecasts(vararg forecasts: ForecastItem) {
        deleteAllWeatherForecasts()
        insertWeatherForecasts(*forecasts)
    }

    @Query("SELECT * FROM weather_forecasts ORDER BY datetime(timestamp) ASC")
    suspend fun getAllWeatherForecasts(): List<ForecastItem>

    @Query(
        "SELECT * FROM weather_forecasts WHERE timestamp >= :after " +
            "ORDER BY datetime(timestamp) ASC LIMIT :atMost"
    )
    suspend fun getWeatherForecasts(
        atMost: Int = Int.MAX_VALUE,
        after: Instant = Instant.now()
    ): List<ForecastItem>
}
