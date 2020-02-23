package com.sherepenko.android.launchiteasy.utils

fun Double.round(decimals: Int = 2): Double =
    "%.${decimals}f".format(this@round).toDouble()

fun Float.round(decimals: Int = 2): Float =
    "%.${decimals}f".format(this@round).toFloat()
