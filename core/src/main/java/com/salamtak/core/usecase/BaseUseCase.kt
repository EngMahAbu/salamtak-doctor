package com.salamtak.core.usecase

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.salamtak.core.network.WebClientCreator
import com.salamtak.core.utils.SessionManagement
import com.salamtak.domain.entities.GeneralResponse
import com.salamtak.domain.entities.response.DefaultResponse
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.StringQualifier
import retrofit2.HttpException
import java.net.SocketTimeoutException

abstract class BaseUseCase<out Response,in Params,in Token> : KoinComponent{


    private val gson: Gson by inject(StringQualifier(UseCaseModule.GSON))
    private val context: Context by inject(StringQualifier(UseCaseModule.APP_CONTEXT))
    val webClient: WebClientCreator by inject()
    val sessionManagement: SessionManagement by inject()

    val TAG = "BaseUseCase"

    object NULL_TO_EMPTY_STRING_ADAPTER {
        @FromJson
        fun fromJson(reader: JsonReader): String {
            if (reader.peek() != JsonReader.Token.NULL) {
                return reader.nextString()
            }
            reader.nextNull<Unit>()
            return ""
        }
    }

    suspend operator fun invoke(parameters: Params,token: Token): GeneralResponse<Response> {
        return try {
            // try calling api

            Log.e("$TAG Request","$parameters")
            Log.e("$TAG Request22", "invoke: ${gson.excluder().toString()}" )
            val result = execute(parameters,token)
            Log.e("$TAG ExceptTry", "invoke: $result" )
            Log.e("$TAG Mostafa",result.toString())
            GeneralResponse.Success(result)
        }
        catch (callingApiException: Exception) {
            // An exception occured
            Log.e("$TAG Mostafa","${callingApiException.message!!} ${callingApiException.localizedMessage} $ " )

            callingApiException.printStackTrace()

            when (callingApiException) {
                is HttpException -> {

                    // an exception related to the Backend Api
                    val errorResponse = callingApiException.response()
                    val errorBody = errorResponse?.errorBody()
                    try {
                        // Try to parse error from backend

                        Log.e(TAG, "invokeErrorTry:${callingApiException.response()} ==> ${callingApiException.message} " )

                        val apiError = callingApiException.response()?.errorBody()?.source().let {
                            val moshiAdapter = Moshi.Builder()
                                .add(NULL_TO_EMPTY_STRING_ADAPTER)
                                .add(KotlinJsonAdapterFactory())
                                .build().adapter(DefaultResponse::class.java)

                                moshiAdapter.fromJson(it)


                        }
//                        Log.e(TAG, "invoke: $apiError" )

                        Log.e(TAG, "invokeerrorResponse: ${callingApiException.message}" )

                        GeneralResponse.Failure.JSonServerError(
                            apiError?.Message
                                ?: "Something wrong !",
                            apiError?.MessageCode,
                            errorResponse?.code()
                        )

                    } catch (parsingEx: Exception) {

                        Log.e(TAG, "invokeExceptionCatchException: $ $parsingEx" )
                        GeneralResponse.Failure.Error(parsingEx, errorResponse?.message()!! ,errorResponse?.code()!!)
                    }
                }
                is KotlinNullPointerException -> GeneralResponse.Failure.JSonServerError(
                    errorMessage
                )
                is SocketTimeoutException ->
                {

                    GeneralResponse.Failure.JSonServerError(
                        errorMessage  )
                }


                else -> // An error not related to API or backend
                    GeneralResponse.Failure.Error(callingApiException, errorMessage!!,0)
            }
        }
    }

    protected abstract suspend fun execute(parameters: Params,token: Token): Response



    var errorMessage: String? = ""



}