package com.salamtak.salamtakdoctor.salamtak_doctor.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.NotificationResponse
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel(){

    val repo = NotificationRepository()
    var showLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<ErrorModel>()
    var notificationModel = MutableLiveData<NotificationResponse>()

    fun getNotification() {

        showLoading.value = true
        viewModelScope.launch {
            val notification = repo.getNotification()

            when (notification) {
                is NetworkResponse.Success -> {
                    notificationModel.value = notification.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = notification.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

}