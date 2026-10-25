package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.signup

import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Sign-up field check: the error (red outline + (i) + message) appears when the user leaves the
 * field invalid after typing in it, and clears as soon as the value becomes valid.
 */
fun TextInputEditText.validateOnLeave(
    layout: TextInputLayout,
    error: String,
    isValid: (String) -> Boolean = { it.isNotEmpty() },
    onChanged: () -> Unit
) {
    var touched = false
    doAfterTextChanged {
        touched = true
        if (isValid(it?.toString()?.trim().orEmpty())) layout.error = null
        onChanged()
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus && touched) {
            layout.error = if (isValid(text?.toString()?.trim().orEmpty())) null else error
        }
    }
}
