package com.sherepenko.android.launchiteasy.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.sherepenko.android.launchiteasy.R

fun Context.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) ==
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
