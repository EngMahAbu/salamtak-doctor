package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs.getDocs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.EditDocumentResponse
import kotlinx.coroutines.launch

class EditDocumentViewModel : ViewModel() {

//    val repo = EditDocumentRepository()
//    var showLoading = MutableLiveData<Boolean>()
//    var documentModel = MutableLiveData<EditDocumentResponse>()
//    val error = MutableLiveData<ErrorModel>()
//
//    fun getDocument() {
//
//        showLoading.value = true
//        viewModelScope.launch {
//            val document = repo.getDocumentDoctor()
//
//            when (document) {
//                is NetworkResponse.Success -> {
//                    documentModel.value = document.body
//                }
//                is NetworkResponse.ApiError -> {
//                    error.value = document.body
//                }
//                is NetworkResponse.NetworkError -> {
//                }
//                is NetworkResponse.UnknownError -> {
//                }
//            }
//            showLoading.value = false
//        }
//    }

}