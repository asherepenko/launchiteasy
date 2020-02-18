package com.sherepenko.android.launchiteasy.ui

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

abstract class BaseActivity(@LayoutRes private val contentLayoutRes: Int) : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayoutRes)
    }
}
