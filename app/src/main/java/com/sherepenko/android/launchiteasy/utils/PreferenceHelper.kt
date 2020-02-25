package com.sherepenko.android.launchiteasy.utils

import android.content.Context
import android.content.SharedPreferences
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.TemperatureUnit

class PreferenceHelper(
    private val context: Context,
    private val preferences: SharedPreferences
) {
    fun getTemperatureUnit(): TemperatureUnit =
        TemperatureUnit.valueOf(
            preferences.getString(
                context.getString(R.string.temperature_unit_key),
                "${TemperatureUnit.CELSIUS.ordinal}"
            )!!.toInt()
        )

    fun showSystemApps(): Boolean =
        preferences.getBoolean(context.getString(R.string.show_system_apps_key), true)
}
