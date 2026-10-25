package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.queue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemQueueClinicBinding
import com.salamtak.domain.entities.response.offers.ClinicData

class QueueClinicsAdapter(
    private val onClinicClick: (clinicId: Int, clinicName: String) -> Unit
) : ListAdapter<ClinicData, QueueClinicsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemQueueClinicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clinic = getItem(position)
        // Gson can leave these null despite the non-null declarations
        val clinicName: String? =
            if (Localization.getLanguageName() == "ar") clinic.nameAr else clinic.name
        holder.binding.tvClinicName.text = clinicName.orEmpty().trim()
        holder.itemView.setOnClickListener {
            onClinicClick(clinic.clinicId, clinicName.orEmpty().trim())
        }
    }

    class ViewHolder(itemBinding: ItemQueueClinicBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemQueueClinicBinding = itemBinding
    }

    private class DiffCallback : DiffUtil.ItemCallback<ClinicData>() {
        override fun areItemsTheSame(oldItem: ClinicData, newItem: ClinicData): Boolean {
            return oldItem.clinicId == newItem.clinicId
        }

        override fun areContentsTheSame(oldItem: ClinicData, newItem: ClinicData): Boolean {
            return oldItem == newItem
        }
    }
}
