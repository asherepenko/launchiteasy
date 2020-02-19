package com.sherepenko.android.launchiteasy.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sherepenko.android.launchiteasy.ui.MainActivity

class PackageReplaceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                context.startActivity(MainActivity.homeIntent(context))
            }
            else -> {
                // ignore
            }
        }
    }
}
