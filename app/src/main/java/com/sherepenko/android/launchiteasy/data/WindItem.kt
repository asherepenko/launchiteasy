package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo

data class WindItem(
    @ColumnInfo(name = "speed")
    val speed: Float,
    @ColumnInfo(name = "direction")
    val direction: Int
)

fun WindItem.toImperial(): WindItem =
    copy(speed = speed * 2.237f)
