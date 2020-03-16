package com.sherepenko.android.launchiteasy.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import timber.log.Timber

abstract class BaseActivity(@LayoutRes private val contentLayoutRes: Int) : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayoutRes)
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

    private fun setCurrentScreen() {
        Timber.d("Current activity: ${javaClass.simpleName}")
        FirebaseAnalytics.getInstance(this@BaseActivity)
            .setCurrentScreen(this@BaseActivity, javaClass.simpleName, null)
    }
}
