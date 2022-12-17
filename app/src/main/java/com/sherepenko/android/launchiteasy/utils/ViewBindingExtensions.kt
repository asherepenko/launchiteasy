package com.sherepenko.android.launchiteasy.utils

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline initializer: (LayoutInflater) -> T
) =
    lazy(LazyThreadSafetyMode.NONE) {
        initializer.invoke(layoutInflater)
    }
