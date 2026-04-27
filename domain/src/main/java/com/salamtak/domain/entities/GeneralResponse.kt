package com.salamtak.domain.entities

import java.net.SocketException

sealed class GeneralResponse<out R>
{

    data class Success<out T>(val data: T) : GeneralResponse<T>()

    sealed class Loading: GeneralResponse<Nothing>()
    sealed class Failure : GeneralResponse<Nothing>() {

        class JSonServerError(
            val errorMessage: String? = null,
            val MessageCode: Int? =null,
            var errorCode: Int? = null
        ) : Failure()

        // error from server failure or timeout or no internet connection
        data class Error(val ex: Exception,val message: String, val errorCode: Int) : Failure()

    }

}

class NoInternetException : SocketException() {
    override val message: String?
        get() = "The internet connection appears to be offline."

    companion object {
        const val NO_INTERNET_CODE = 600 /*Just a code to Indicate a connection problem*/
    }
}