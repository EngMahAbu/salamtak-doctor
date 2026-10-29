package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.ClinicData
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
    // Clinic name (English or Arabic, lowercased) -> its name in the app's language
    private val names = HashMap<String, String>()

    fun load(scope: CoroutineScope) {
        val lang = DateUtils.getLang()
        if (lang == loadedLang && !_areas.value.isNullOrEmpty()) return
        scope.launch {
            when (val clinics = HomeRepository().getHomeClinic()) {
                is NetworkResponse.Success -> {
                    loadedLang = lang
                    setClinics(clinics.body.Data)
                }
                else -> Log.e(TAG, "load: couldn't get the doctor's clinics: $clinics")
            }
        }
    }

    fun setClinics(clinics: List<ClinicData>?) {
        val byName = HashMap(_areas.value.orEmpty())
        clinics.orEmpty().forEach { clinic ->
            val localized = clinic.localizedName()
            if (!localized.isNullOrEmpty()) {
                clinic.Name?.let { names[key(it)] = localized }
                clinic.NameAr?.let { names[key(it)] = localized }
            }
            val area = clinic.AreaName?.let(::withoutCity).takeIf { !it.isNullOrEmpty() }
                ?: clinic.Address?.trim().takeIf { !it.isNullOrEmpty() }
            if (area.isNullOrEmpty()) return@forEach
            // Appointments may use either the English or the Arabic clinic name
            clinic.Name?.let { byName[key(it)] = area }
            clinic.NameAr?.let { byName[key(it)] = area }
        }
        _areas.value = byName
    }

    /** The clinic's area (without the city) in the app's language, or [fallback] (the typed address), or "-" if unknown. */
    @JvmStatic
    fun areaFor(clinicName: String?, fallback: String?): String {
        val map = _areas.value.orEmpty()
        val area = clinicName?.let { raw ->
            val k = key(raw)
            map[k] ?: map.entries.firstOrNull { (nameKey, _) ->
                nameKey.isNotEmpty() && (k.contains(nameKey) || nameKey.contains(k))
            }?.value
        }?.let(::withoutCity)
        val validArea = area?.takeIf { it.isNotEmpty() }
        val validFallback = fallback?.let(::withoutCity)?.takeIf { it.isNotEmpty() }
        return validArea ?: validFallback ?: "-"
    }

    /**
     * The clinic's own name for the card. Hospital and polyclinic appointments carry ClinicName as
     * "Hospital - Speciality", so drop the speciality and show the doctor's clinic it names, or
     * the hospital name if none of the doctor's clinics matches.
     */
    @JvmStatic
    fun clinicNameFor(clinicName: String?, specName: String?): String {
        val raw = clinicName?.trim().orEmpty()
        if (raw.isEmpty()) return "-"
        val spec = specName?.trim().orEmpty()
        val parts = raw.split(" - ").map { it.trim() }.filter { it.isNotEmpty() }
        // Without a separator it's already a plain clinic name
        if (parts.size < 2) return names[key(raw)] ?: raw
        val withoutSpec = parts.filterNot { spec.isNotEmpty() && it.equals(spec, ignoreCase = true) }
            .ifEmpty { parts }
        withoutSpec.firstNotNullOfOrNull { names[key(it)] }?.let { return it }
        // Separator but the speciality isn't one of the parts: only a hospital name is "A - B"
        return if (withoutSpec.size == parts.size && !DateUtils.isHospital()) raw else withoutSpec.first()
    }

    private fun ClinicData.localizedName(): String? {
        val ar = NameAr?.trim()?.takeIf { it.isNotEmpty() }
        val en = Name?.trim()?.takeIf { it.isNotEmpty() }
        return if (Locale.getDefault().language == "ar") ar ?: en else en ?: ar
    }

    /** AreaName and ClinicAddress come as "City, Area" (e.g. "Giza, Dokki"); keep only the area. */
    private fun withoutCity(areaName: String) =
        areaName.split(',', '،').map { it.trim() }.lastOrNull { it.isNotEmpty() }.orEmpty()

    private fun key(name: String) = name.trim().lowercase(Locale.ROOT)
}
