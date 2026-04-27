package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.core.utils.SingleLiveEvent
import com.salamtak.domain.entities.GeneralResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel(): ViewModel()
{

    val failure: MutableLiveData<GeneralResponse.Failure> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = SingleLiveEvent()

    val TAG = BaseViewModel::class.java.simpleName

    fun handleError(failure: GeneralResponse.Failure) {
        Log.e("Mostafa", "handleError: ${failure.toString()}" )
        this.failure.value = failure
    }

    fun <T> callApi(successResult: MutableLiveData<T>, apiCall: suspend () -> GeneralResponse<T>) {
        viewModelScope.launch {
            loading.postValue(true)
            val result: GeneralResponse<T> = withContext(Dispatchers.IO) {
                apiCall.invoke()
            }
            loading.postValue(false)
            when (result) {
                is GeneralResponse.Success -> {
                    Log.e("Gallery", "callApi:ResultSuccess ${Gson().toJson(result.data)}" )
                    successResult.value = result.data
                }
                is GeneralResponse.Failure -> {
                    Log.e("$TAG Mostafa",result.toString())
                    handleError(result)
                    loading.postValue(false)
                }
                is GeneralResponse.Loading ->{
                    loading.postValue(true)
                }

            }
        }
    }


}