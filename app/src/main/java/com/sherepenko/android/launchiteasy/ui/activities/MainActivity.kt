package com.sherepenko.android.launchiteasy.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.sherepenko.android.launchiteasy.R

class MainActivity : BaseActivity(R.layout.activity_main) {

    companion object {
        fun homeIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this@MainActivity, R.xml.main_preferences, false)
    }
}
