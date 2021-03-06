package com.sherepenko.android.launchiteasy.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import timber.log.Timber

abstract class BaseActivity(@LayoutRes private val contentLayoutId: Int) : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayoutId)
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

    private fun setCurrentScreen() {
        Timber.d("Current activity: ${javaClass.simpleName}")
        Firebase.analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to javaClass.simpleName,
                FirebaseAnalytics.Param.SCREEN_CLASS to javaClass.canonicalName
            )
        )
    }
}
