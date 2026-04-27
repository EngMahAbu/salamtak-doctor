package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class GetClincPacakgesResponse(
    @SerializedName("Data")
    val `data`: GetClincPacakgesData,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class GetClincPacakgesData(
    @SerializedName("Doctorname")
    val Doctorname: String,
    @SerializedName("AreaName")
    val areaName: String,
    @SerializedName("CityName")
    val cityName: String,
    @SerializedName("Complete_inclinic_insurance")
    val completeInclinicInsurance: Int,
    @SerializedName("Complete_inclinic_patientCount")
    val completeInclinicPatientCount: Int,
    @SerializedName("Complete_teleconsultation")
    val completeTeleconsultation: Int,
    @SerializedName("DoctorMobile")
    val doctorMobile: String,
    @SerializedName("Extra_inclinic_insuranceCount")
    val extraInclinicInsuranceCount: Int,
    @SerializedName("Extra_inclinic_insurancePrice")
    val extraInclinicInsurancePrice: Int,
    @SerializedName("Extra_inclinic_patientCount")
    val extraInclinicPatientCount: Int,
    @SerializedName("Extra_inclinic_patientPrice")
    val extraInclinicPatientPrice: Int,
    @SerializedName("Extra_teleconsultationCount")
    val extraTeleconsultationCount: Int,
    @SerializedName("Extra_teleconsultationPrice")
    val extraTeleconsultationPrice: Int,
    @SerializedName("LastMonth_inclinic_patientCount")
    val lastMonthInclinicPatientCount: Int,
    @SerializedName("Month")
    val month: Int,
    @SerializedName("PackageID")
    val packageID: Int,
    @SerializedName("Package_inclinic_insurance")
    val packageInclinicInsurance: Int,
    @SerializedName("Package_inclinic_patient")
    val packageInclinicPatient: Int,
    @SerializedName("PackagePrice")
    val packagePrice: Int,
    @SerializedName("Package_teleconsultation")
    val packageTeleconsultation: Int,
    @SerializedName("Packagename")
    val packagename: String,
    @SerializedName("SpecName")
    val specName: String,
    @SerializedName("Year")
    val year: Int,
    @SerializedName("InclinicAppointments")
    val InclinicAppointments: List<InclinicAppointments>,
    @SerializedName("TeleCommunicationAppointments")
    val teleCommunicationAppointments: List<TeleCommunicationAppointment>,
    @SerializedName("insuranceAppointments")
    val insuranceAppointments: List<Any>,

)

data class InclinicAppointments(

    @SerializedName("AppointmentDate")
    val appointmentDate: String,
    @SerializedName("PatientName")
    val patientName: String

)
data class TeleCommunicationAppointment(
    @SerializedName("AppointmentDate")
    val appointmentDate: String,
    @SerializedName("PatientName")
    val patientName: String
)