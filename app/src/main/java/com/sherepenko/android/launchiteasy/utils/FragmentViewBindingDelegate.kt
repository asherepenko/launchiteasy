package com.sherepenko.android.launchiteasy.utils

import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {

    private var viewBinding: T? = null

    init {
        fragment.lifecycleScope.launchWhenCreated {
            fragment.viewLifecycleOwnerLiveData.observe(fragment) {
                it.lifecycle.addObserver(object : DefaultLifecycleObserver {

                    override fun onDestroy(owner: LifecycleOwner) {
                        viewBinding = null
                    }
                })
            }
        }
    }

    @MainThread
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (!fragment.lifecycleState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Should not attempt to get bindings when Fragment views are destroyed")
        }

        return viewBindingFactory(thisRef.requireView()).also {
            viewBinding = it
        }
    }
}

private val Fragment.lifecycleState: Lifecycle.State
    get() =
        viewLifecycleOwner.lifecycle.currentState
