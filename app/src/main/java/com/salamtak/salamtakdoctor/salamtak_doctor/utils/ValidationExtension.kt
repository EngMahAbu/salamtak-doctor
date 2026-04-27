package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.widget.EditText

fun EditText.isPasswordValid():Boolean = this.length() > 0
fun EditText.isEmailValid():Boolean = this.text.toString().isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this.text).matches()
fun EditText.isValid():Boolean = this.text.toString().isNotEmpty()
fun EditText.isPhoneValid():Boolean = this.length() > 9