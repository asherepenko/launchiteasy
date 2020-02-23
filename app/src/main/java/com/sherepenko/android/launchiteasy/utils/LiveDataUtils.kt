package com.sherepenko.android.launchiteasy.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <A, B> LiveData<A>.combineLatest(b: LiveData<B>): LiveData<Pair<A, B>> =
    MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        addSource(this@combineLatest) {
            if (it == null && value != null) {
                value = null
            }

            lastA = it

            if (lastA != null && lastB != null) {
                value = lastA!! to lastB!!
            }
        }

        addSource(b) {
            if (it == null && value != null) {
                value = null
            }

            lastB = it

            if (lastA != null && lastB != null) {
                value = lastA!! to lastB!!
            }
        }
    }
