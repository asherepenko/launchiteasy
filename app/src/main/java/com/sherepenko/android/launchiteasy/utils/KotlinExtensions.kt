package com.sherepenko.android.launchiteasy.utils

fun Double.round(decimals: Int = 2): Double =
    "%.${decimals}f".format(this).toDouble()

fun Float.round(decimals: Int = 2): Float =
    "%.${decimals}f".format(this).toFloat()
