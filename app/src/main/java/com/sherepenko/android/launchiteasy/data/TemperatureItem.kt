package com.sherepenko.android.launchiteasy.data

import androidx.room.ColumnInfo
import kotlin.math.roundToInt

data class TemperatureItem(
    @ColumnInfo(name = "kelvin")
    val kelvin: Float
) {
    val celsius: Int
        get() = (kelvin - 273.15f).roundToInt()

    val fahrenheit: Int
        get() = ((kelvin - 273.15f) * 9 / 5 + 32).roundToInt()
}
