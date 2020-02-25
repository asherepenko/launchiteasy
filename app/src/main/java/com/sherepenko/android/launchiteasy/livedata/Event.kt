package com.sherepenko.android.launchiteasy.livedata

import java.util.concurrent.atomic.AtomicBoolean

open class Event<T>(private val content: T) {

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
