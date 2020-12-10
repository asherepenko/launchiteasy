package com.sherepenko.android.launchiteasy.data

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<out T>(data: T) : Resource<T>(data)

    class Loading<out T>(
        data: T? = null,
        message: String? = null
    ) : Resource<T>(data, message)

    class Error<out T>(
        val error: Throwable? = null,
        data: T? = null,
        message: String? = null
    ) : Resource<T>(data, message)
}
