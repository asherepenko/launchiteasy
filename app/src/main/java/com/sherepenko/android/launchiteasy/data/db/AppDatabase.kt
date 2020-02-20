package com.sherepenko.android.launchiteasy.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sherepenko.android.launchiteasy.data.AppItem
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.WeatherItem

@Database(
    entities = [
        AppItem::class,
        WeatherItem::class,
        ForecastItem::class
    ],
    version = 2020_02_21_01,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAppDao(): AppDao

    abstract fun getWeatherDao(): WeatherDao

    abstract fun getForecastDao(): ForecastDao
}
