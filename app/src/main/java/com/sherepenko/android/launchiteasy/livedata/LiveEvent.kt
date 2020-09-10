package com.sherepenko.android.launchiteasy.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

open class LiveEvent<T>(private val content: T) {

    private val handled = AtomicBoolean(false)

    fun getContentIfNotHandled(defaultValue: T? = null): T? =
        if (handled.compareAndSet(false, true)) {
            content
        } else {
            defaultValue
        }

    fun peekContent(): T =
        content
}

inline fun <T> LiveData<LiveEvent<T>>.observe(
    owner: LifecycleOwner,
    crossinline onEventContent: (T) -> Unit
) = observe(owner, Observer {
        it.getContentIfNotHandled()?.let(onEventContent)
    })

inline fun <T> LiveData<LiveEvent<T>>.observeForever(crossinline onEventContent: (T) -> Unit) =
    observeForever {
        it.getContentIfNotHandled()?.let(onEventContent)
    }
