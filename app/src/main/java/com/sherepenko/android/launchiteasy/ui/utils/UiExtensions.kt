package com.sherepenko.android.launchiteasy.ui.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutResId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutResId, this, attachToRoot)