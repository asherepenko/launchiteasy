package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ConditionItem(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @Embedded(prefix = "icon_")
    val icon: WeatherIcon
)
