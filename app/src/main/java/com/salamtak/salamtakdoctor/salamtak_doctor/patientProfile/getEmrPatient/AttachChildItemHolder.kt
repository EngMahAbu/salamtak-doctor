package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemAttachmentPatientBinding
import com.salamtak.domain.entities.response.NewPatientEmrdocument
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class AttachChildItemHolder (private val binding: ItemAttachmentPatientBinding,
var listener: RecyclerViewOnClickListener) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(child: NewPatientEmrdocument, position: Int) {
        binding.emrDocument = child
        binding.position = position
        initClick(child.DocumentUrl)
    }

    private fun initClick(image :String) {

        binding.viewEyeIv.setOnClickListener {
            binding.listener?.let {
                it.onImageEmrClicked(binding.position!! ,image)
            }
        }
//        binding.chechbox.setOnCheckedChangeListener { _, isChecked ->
//            val clickType = if (isChecked) SELECT else UN_SELECT
//            binding.listener?.let {
//                it.onChildItemClicked(binding.position!!, binding.parentPosition!!, clickType)
//            }
//        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: RecyclerViewOnClickListener,
            parentPosition: Int
        ): AttachChildItemHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemAttachmentPatientBinding.inflate(layoutInflater, parent, false)
            binding.listener = listener
            binding.parentPosition = parentPosition
            return AttachChildItemHolder(binding,listener)
        }
    }
}