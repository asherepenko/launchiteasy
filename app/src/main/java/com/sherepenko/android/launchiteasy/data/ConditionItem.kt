package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo

data class ConditionItem(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "icon")
    val icon: String
)
