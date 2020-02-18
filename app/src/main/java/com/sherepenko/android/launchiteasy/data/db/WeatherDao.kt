package com.sherepenko.android.launchiteasy.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sherepenko.android.launchiteasy.data.WeatherItem

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: WeatherItem)

    @Delete
    suspend fun deleteCurrentWeather(weather: WeatherItem)

    @Query("DELETE FROM current_weather")
    suspend fun deleteCurrentWeather()

    @Transaction
    suspend fun updateCurrentWeather(weather: WeatherItem) {
        deleteCurrentWeather()
        insertCurrentWeather(weather)
    }

    @Query("SELECT * FROM current_weather LIMIT 1")
    suspend fun getCurrentWeather(): WeatherItem
}
