package com.sherepenko.android.launchiteasy.data.db

import androidx.room.TypeConverter
import org.threeten.bp.Instant

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromInstant(instant: Instant?): Long? =
        instant?.toEpochMilli()

    @TypeConverter
    @JvmStatic
    fun toInstant(epochMilli: Long?): Instant? =
        epochMilli?.let {
            Instant.ofEpochMilli(it)
        }
}
