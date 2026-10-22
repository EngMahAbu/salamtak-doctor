package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services

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
 * Booking type images (In Clinic, Home Visit, Video Call, Call) taken from the same
 * home endpoint the Home tab uses, keyed by type name in the current language.
 * The schedule periods only carry the type name, so that's what we match on.
 */
object MedicalTypeImages {

    private const val TAG = "MedicalTypeImages"
    private val _images = MutableLiveData<Map<String, String>>(emptyMap())
    val images: LiveData<Map<String, String>> = _images
    private var loadedLang: String? = null

    fun load(scope: CoroutineScope) {
        val lang = DateUtils.getLang()
        if (lang == loadedLang && !_images.value.isNullOrEmpty()) return
        scope.launch {
            when (val home = HomeRepository().getHome()) {
                is NetworkResponse.Success -> {
                    val types = home.body.Data?.getMedicalEximationListDtos.orEmpty()
                    loadedLang = lang
                    _images.value = types
                        .filter { !it.MedicalTypeName.isNullOrBlank() && !it.Image.isNullOrBlank() }
                        .associate { key(it.MedicalTypeName) to it.Image }
                }
                else -> Log.e(TAG, "load: couldn't get booking type images: $home")
            }
        }
    }

    fun imageFor(typeName: String?): String? =
        typeName?.let { _images.value?.get(key(it)) }

    private fun key(name: String) = name.trim().lowercase(Locale.ROOT)
}
