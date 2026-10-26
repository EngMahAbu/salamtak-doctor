package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import com.google.gson.Gson
import com.salamtak.BuildConfig
import com.salamtak.domain.entities.response.Item
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.VIDEO_CALL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Static appointments for testing the hospital appointment cards in debug builds.
 * (The polyclinic uses its real list, GetCurrentDoctorAppointmentPolyclinic.)
 *
 * TODO(dummy-appointments): the hospital has no appointment endpoint yet. Once it exists, delete
 *  this file and its uses (AppointmentViewModel.getHospitalAppointments / cancelAppointment /
 *  changeAppointmentStatus).
 */
object DummyAppointments {

    private const val CLINIC = 1
    private const val HOME_VISIT = 2
    private val gson = Gson()

    /** Debug builds only, hospital accounts only. */
    val enabled: Boolean
        get() = BuildConfig.DEBUG && DateUtils.isHospital()

    /** Dummy appointments use negative ids, so they're never sent to the API. */
    fun isDummy(appointmentId: Long) = appointmentId < 0

    // One list per app session, so cancel / complete / no show stick until the app restarts
    private val all: List<Item> by lazy { build() }

    fun items(patientName: String?): List<Item> {
        val query = patientName?.trim().orEmpty()
        return if (query.isEmpty()) all else all.filter { it.PatientName.contains(query, ignoreCase = true) }
    }

    // TODO(dummy-appointments): DUMMY DATA — replace with the real hospital appointments
    //  endpoint when the backend adds it, then delete this file.
    private fun build(): List<Item> = listOf(
        item(-1, "Khaled Mostafa", VIDEO_CALL, "Video Call", days = 1, hour = 13, minute = 15, clinic = "Cardiology"),
        item(-2, "Nour Hassan", CLINIC, "Clinic", days = 2, hour = 16, minute = 45, clinic = "Pediatrics"),
        item(-3, "Mona Adel", HOME_VISIT, "Home visit", days = 3, hour = 10, minute = 0, clinic = "Dermatology"),
        item(-4, "Omar Fathy", CLINIC, "Clinic", days = 1, hour = 11, minute = 30, clinic = "Cardiology", cancelled = true),
        item(-5, "Sara Ali", CLINIC, "Clinic", days = -1, hour = 9, minute = 0, clinic = "Pediatrics", completed = true),
        item(-6, "Youssef Samir", HOME_VISIT, "Home visit", days = 0, hour = -2, minute = 0, clinic = "Dermatology"),
        item(-7, "Hana Mahmoud", CLINIC, "Clinic", days = -2, hour = 12, minute = 0, clinic = "Cardiology", noShow = true),
    )

    /** [hour] is the hour of day, or with days = 0 an offset in hours from now. */
    private fun item(
        id: Long, patient: String, typeId: Int, typeName: String,
        days: Int, hour: Int, minute: Int, clinic: String,
        cancelled: Boolean = false, completed: Boolean = false, noShow: Boolean = false,
    ): Item {
        val date = Calendar.getInstance().apply {
            if (days == 0) {
                add(Calendar.HOUR_OF_DAY, hour)
            } else {
                add(Calendar.DAY_OF_YEAR, days)
                set(Calendar.HOUR_OF_DAY, hour)
            }
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }.time
        // Parsed like an API response so the fields the API leaves out stay null
        // (a null RelationshipTypeId hides the "another patient" rows)
        val json = mapOf(
            "AppointmentDate" to SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(date),
            "AppointmentId" to id,
            "ClinicName" to "Salamtak Hospital - $clinic",
            "ClinicAddress" to "Giza, Dokki",
            "ClinicPhone" to listOf("0233333333"),
            "HaveEMR" to false,
            "IsCancel" to cancelled,
            "IsCompleted" to completed,
            "IsNoShow" to noShow,
            "MedicalExaminationTypeId" to typeId,
            "MedicalExaminationTypeName" to typeName,
            "MedicalExaminationTypeImage" to "",
            "PatientId" to id,
            "PatientImage" to "",
            "PatientName" to patient,
            "PatientPhone" to "01000000000",
            "CanCancel" to (!cancelled && !completed && !noShow),
            "AppointmentTime" to SimpleDateFormat("hh:mm", Locale.US).format(date),
            "DoctorName" to "Ahmed Samy",
            "SpecName" to clinic,
            "DoctorImage" to "",
        )
        return gson.fromJson(gson.toJson(json), Item::class.java)
    }
}
