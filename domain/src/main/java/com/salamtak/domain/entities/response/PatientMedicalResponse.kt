package com.salamtak.domain.entities.response

class PatientMedicalResponse (
    val Data: PatientMedicalData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class PatientMedicalData(
    val BloodName: String,
    val BloodTypeId: Int,
    val ChronicDiseases: String,
    val CurrentMedication: String,
    val Height: Int,
    val Id: Int,
    val Iinjuries: String,
    val OtherAllergies: String,
    val PastMedication: String,
    val PatientFoodAllergiesDto: List<Any>,
    val PatientFoodAllergiesName: List<Any>,
    val PatientId: Int,
    val PatientMedicineAllergiesDto: List<Any>,
    val PatientMedicineAllergiesName: List<Any>,
    val Prescriptions: String,
    val Pressure: String,
    val SugarLevel: String,
    val Surgeries: String,
    val CityName: String,
    val AreaName: String,
    val Weight: Int,
    val BlockNo: String,
    val FloorNo: String,
)