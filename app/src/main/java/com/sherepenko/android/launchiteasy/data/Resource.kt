package com.sherepenko.android.launchiteasy.data

data class Resource<out T> private constructor(
    val status: Status,
    val data: T? = null,
    val message: String? = null,
    val error: Throwable? = null
) {
    companion object {
        fun <T> loading(
            data: T? = null,
            message: String? = null
        ): Resource<T> =
            Resource(
                Status.LOADING,
                data,
                message
            )

        fun <T> success(data: T): Resource<T> =
            Resource(
                Status.SUCCESS,
                data
            )

        fun <T> error(
            error: Throwable? = null,
            data: T? = null,
            message: String? = null
        ): Resource<T> =
            Resource(
                Status.ERROR,
                data,
                message,
                error
            )
    }
}
