package com.sherepenko.android.launchiteasy.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.sherepenko.android.launchiteasy.R

fun ViewGroup.inflate(@LayoutRes layoutResId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutResId, this@inflate, attachToRoot)

fun Context.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this@isPermissionGranted, permission) ==
        PackageManager.PERMISSION_GRANTED

fun Activity.launchActivity(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(
        R.anim.nav_default_enter_anim,
        R.anim.nav_default_exit_anim
    )
}

fun Activity.launchActivityIfResolved(intent: Intent) {
    intent.resolveActivity(packageManager)?.let {
        launchActivity(intent)
    }
}
