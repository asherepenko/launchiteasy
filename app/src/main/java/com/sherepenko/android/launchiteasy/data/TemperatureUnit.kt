package com.sherepenko.android.launchiteasy.data

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    companion object {
        fun valueOf(value: Int): TemperatureUnit =
            values().first {
                it.ordinal == value
            }
    }
}

fun TemperatureUnit.isMetric() =
    this@isMetric == TemperatureUnit.CELSIUS

fun TemperatureUnit.isImperial() =
    this@isImperial == TemperatureUnit.FAHRENHEIT
