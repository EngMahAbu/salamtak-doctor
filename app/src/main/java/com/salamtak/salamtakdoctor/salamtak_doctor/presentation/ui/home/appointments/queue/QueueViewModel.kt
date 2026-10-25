package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.domain.entities.response.queue.QueueActionResponse
import com.salamtak.domain.entities.response.queue.QueueBoardResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class QueueViewModel : ViewModel() {

    private val repo = QueueRepository()
    val queueBoardResponse = MutableLiveData<Resource<QueueBoardResponse>>()
    val queueActionResponse = MutableLiveData<Resource<QueueActionResponse>?>()

    fun getQueueBoard(clinicId: Int, queueDate: String) = viewModelScope.launch(Dispatchers.IO + handler) {
        queueBoardResponse.postValue(Resource.Loading())
        val response = repo.getQueueBoard(clinicId, queueDate)
        queueBoardResponse.postValue(handleResponse(response, QueueBoardResponse::class.java) { it.message })
    }

    fun completeTicket(ticketId: Int) = viewModelScope.launch(Dispatchers.IO + handler) {
        queueActionResponse.postValue(Resource.Loading())
        val response = repo.completeTicket(ticketId)
        queueActionResponse.postValue(handleResponse(response, QueueActionResponse::class.java) { it.message })
    }

    fun cancelTicket(ticketId: Int) = viewModelScope.launch(Dispatchers.IO + handler) {
        queueActionResponse.postValue(Resource.Loading())
        val response = repo.cancelTicket(ticketId)
        queueActionResponse.postValue(handleResponse(response, QueueActionResponse::class.java) { it.message })
    }

    private fun <T> handleResponse(
        response: Response<T>,
        type: Class<T>,
        message: (T) -> String?
    ): Resource<T> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.sucess(it)
            }
        }
        val error = runCatching {
            Gson().fromJson(response.errorBody()?.string(), type)?.let(message)
        }.getOrNull()
        return Resource.Error(error ?: response.message())
    }

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception", "exception: ${exception.message.toString()}")
        queueBoardResponse.postValue(Resource.Error(exception.message.toString()))
    }
}
