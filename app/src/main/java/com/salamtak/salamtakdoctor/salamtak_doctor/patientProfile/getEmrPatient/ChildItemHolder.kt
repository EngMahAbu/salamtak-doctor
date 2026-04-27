package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile.getEmrPatient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemEmrDetailsBinding
import com.salamtak.databinding.MedicationRvItemBinding
import com.salamtak.domain.entities.response.NewPatientEmrdetail

class ChildItemHolder (private val binding: MedicationRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(child: NewPatientEmrdetail, position: Int) {
        binding.emrDetails = child
        binding.position = position
        initClick()
    }

    private fun initClick() {
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
//            listener: OnChildItemClickListener,
            parentPosition: Int
        ): ChildItemHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = MedicationRvItemBinding.inflate(layoutInflater, parent, false)
//            binding.listener = listener
            binding.parentPosition = parentPosition
            return ChildItemHolder(binding)
        }
    }
}