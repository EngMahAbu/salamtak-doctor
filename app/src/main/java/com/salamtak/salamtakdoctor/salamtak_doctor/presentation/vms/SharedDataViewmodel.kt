package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salamtak.domain.entities.response.AreaData
import com.salamtak.domain.entities.response.CitysData
import com.salamtak.salamtakdoctor.model.ChooseSession
import com.salamtak.salamtakdoctor.model.PatientProfile

class SharedDataViewModel:ViewModel() {

    val getCity= MutableLiveData<CitysData>()
    val getArea=MutableLiveData<AreaData>()
    val session=MutableLiveData<ChooseSession>()
    val patientProfile=MutableLiveData<PatientProfile>()
    val birthDate=MutableLiveData<String>()
    val endDate=MutableLiveData<String>()
    val healthEntityId=MutableLiveData<Int>()

    fun getCity(city:CitysData){
        getCity.postValue(city)
    }
    fun getArea(area:AreaData){
        getArea.postValue(area)
    }
    fun getSession(chooseSession:ChooseSession){
        session.postValue(chooseSession)
    }
    fun getPatientProfile(patient: PatientProfile){
        patientProfile.postValue(patient)
    }
    fun getBirthDate(birthdate: String){
        this.birthDate.postValue(birthdate)
    }
    fun getEdndDate(enddate: String){
        this.endDate.postValue(enddate)
    }
    fun setHealthEntityId(id: Int){
        this.healthEntityId.postValue(id)
    }

}