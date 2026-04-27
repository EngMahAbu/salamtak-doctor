package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import androidx.annotation.StringRes


sealed class Resource<out R> {

    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(@StringRes val message: Int) : Resource<Nothing>()
    data class SigUpError<out T>(val data: T) : Resource<T>()
    object Loading : Resource<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception message =$message]"
            is SigUpError -> "SignUpError[exception message =$data]"
            Loading -> "Loading"
        }
    }
}

inline fun <T : Any> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

inline fun <T : Any> Resource<T>.onError(action: (Int) -> Unit): Resource<T> {
    if (this is Resource.Error) action(message)
    return this
}

/**
 * `true` if [Resource] is of type [Success] & holds non-null [Success.data].
 */
val Resource<*>.succeeded
    get() = this is Resource.Success && data != null



