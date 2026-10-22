package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.HomeRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Localized area names of the doctor's clinics, from GetDoctorClinics.
 * Appointments only carry the clinic name and its typed address (ClinicAddress),
 * so the card looks its area up here by clinic name.
 */
object ClinicAreas {

    private const val TAG = "ClinicAreas"
    private val _areas = MutableLiveData<Map<String, String>>(emptyMap())
    val areas: LiveData<Map<String, String>> = _areas
    private var loadedLang: String? = null

    fun load(scope: CoroutineScope) {
        val lang = DateUtils.getLang()
        if (lang == loadedLang && !_areas.value.isNullOrEmpty()) return
        scope.launch {
            when (val clinics = HomeRepository().getHomeClinic()) {
                is NetworkResponse.Success -> {
                    val byName = HashMap<String, String>()
                    clinics.body.Data.orEmpty().forEach { clinic ->
                        val area = clinic.AreaName?.trim()
                        if (area.isNullOrEmpty()) return@forEach
                        // Appointments may use either the English or the Arabic clinic name
                        clinic.Name?.let { byName[key(it)] = area }
                        clinic.NameAr?.let { byName[key(it)] = area }
                    }
                    loadedLang = lang
                    _areas.value = byName
                }
                else -> Log.e(TAG, "load: couldn't get the doctor's clinics: $clinics")
            }
        }
    }

    /** The clinic's area in the app's language, or [fallback] (the typed address) if unknown. */
    @JvmStatic
    fun areaFor(clinicName: String?, fallback: String?): String? =
        clinicName?.let { _areas.value?.get(key(it)) } ?: fallback

    private fun key(name: String) = name.trim().lowercase(Locale.ROOT)
}
