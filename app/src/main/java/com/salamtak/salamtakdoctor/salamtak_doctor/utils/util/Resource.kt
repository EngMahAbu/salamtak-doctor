package com.salamtak.salamtakdoctor.salamtak_doctor.utils.util

sealed class Resource<T>(

    val data:T?=null,
    val message:String?=null

) {
    class sucess<T>( data:T):Resource<T>(data)
    class Error<T>( message: String,data:T?=null): Resource<T>(data,message)
    class Loading<T>(): Resource<T>()

}