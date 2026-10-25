package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.doctors

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemHospitalDoctorBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil

class HospitalDoctorsAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<HospitalDoctorsAdapter.ViewHolder>() {

    interface Listener {
        fun onClinics(row: HospitalDoctorRow)
        fun onAddClinic(row: HospitalDoctorRow)
        fun onSchedule(row: HospitalDoctorRow)
        fun onEdit(row: HospitalDoctorRow)
        fun onDelete(row: HospitalDoctorRow)
    }

    private var rows: List<HospitalDoctorRow> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submit(list: List<HospitalDoctorRow>) {
        rows = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHospitalDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = rows.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = rows[position]
        val doctor = row.doctor
        val arabic = Localization.getLanguageName() == "ar"
        with(holder.binding) {
            tvDoctorName.text = (if (arabic) doctor.DoctorNameAr else doctor.DoctorName)
                ?.takeIf { it.isNotBlank() } ?: doctor.DoctorName.orEmpty()
            tvDoctorSpecialty.text = doctor.SpecialistName.orEmpty()
            ImageUtil.loadPicture(ivDoctor, doctor.DoctorImage)

            val hasClinic = row.clinic != null
            ivClinic.setImageResource(if (hasClinic) R.drawable.svg_hospital else R.drawable.ic_add)
            ivClinic.setOnClickListener {
                if (hasClinic) listener.onClinics(row) else listener.onAddClinic(row)
            }
            ivSchedule.setOnClickListener { listener.onSchedule(row) }
            ivEdit.setOnClickListener { listener.onEdit(row) }
            ivDelete.setOnClickListener { listener.onDelete(row) }
        }
    }

    class ViewHolder(val binding: ItemHospitalDoctorBinding) : RecyclerView.ViewHolder(binding.root)
}
