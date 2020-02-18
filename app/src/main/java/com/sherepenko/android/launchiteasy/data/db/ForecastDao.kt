package com.sherepenko.android.launchiteasy.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sherepenko.android.launchiteasy.data.ForecastItem

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherForecasts(vararg forecasts: ForecastItem)

    @Delete
    suspend fun deleteWeatherForecasts(vararg forecasts: ForecastItem)

    @Query("DELETE FROM weather_forecast")
    suspend fun deleteAllWeatherForecasts()

    @Transaction
    suspend fun updateWeatherForecasts(vararg forecasts: ForecastItem) {
        deleteAllWeatherForecasts()
        insertWeatherForecasts(*forecasts)
    }

    @Query("SELECT * FROM weather_forecast ORDER BY datetime(timestamp)")
    suspend fun getAllWeatherForecasts(): List<ForecastItem>
}
