package com.salamtak.salamtakdoctor.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.salamtak.R

class Dialog: DialogFragment() {
    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    private var actionDialog: MaterialButton? = null

    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionDialog = view.findViewById<MaterialButton?>(R.id.mbt_dialog_action).apply {
            setOnClickListener {
                getStoreUrl()
            }
        }
    }

    private fun getStoreUrl() {
        val updateUrl = firebaseRemoteConfig.getString(KEY_FORCE_UPDATE_URL)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity?.startActivity(intent)
    }

    companion object {
        const val KEY_FORCE_UPDATE_URL = "android_doctor_force_update_store_url"
    }
}