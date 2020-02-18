package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo

data class LocationItem(
    @ColumnInfo(name = "id", index = true)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "latitude")
    val latitude: Float,
    @ColumnInfo(name = "longitude")
    val longitude: Float
)
