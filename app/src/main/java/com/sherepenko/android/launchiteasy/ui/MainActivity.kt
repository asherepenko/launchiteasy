package com.sherepenko.android.launchiteasy.ui

import android.content.Context
import android.content.Intent
import com.sherepenko.android.launchiteasy.R

class MainActivity : BaseActivity(R.layout.activity_main) {

    companion object {
        fun homeIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
    }
}
