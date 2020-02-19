package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo

data class LocationItem(
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "name")
    val name: String? = null
)
