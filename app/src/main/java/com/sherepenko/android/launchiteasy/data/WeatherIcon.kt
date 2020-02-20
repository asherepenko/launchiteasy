package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo

data class WeatherIcon(
    @ColumnInfo(name = "glyph")
    val glyph: String
)
