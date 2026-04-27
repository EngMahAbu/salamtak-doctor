package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import com.salamtak.R
import com.salamtak.databinding.DialogDeleteBinding
import com.salamtak.databinding.DocumentsLayoutBinding
import com.salamtak.domain.entities.response.ClinicGalleryData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters.ClinicGalleryAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class DeleteDocumentDialog(val listener: RecyclerViewOnClickListener,
                           val bindings: DocumentsLayoutBinding, val position: Int) : DialogFragment() {

    private lateinit var binding: DialogDeleteBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_delete, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.cancelBtn.setOnClickListener {
            dialog!!.dismiss()
        }

        binding.deleteBtn.setOnClickListener {
            listener.onImageDelete(position,bindings)
            dialog!!.dismiss()
        }

    }

}