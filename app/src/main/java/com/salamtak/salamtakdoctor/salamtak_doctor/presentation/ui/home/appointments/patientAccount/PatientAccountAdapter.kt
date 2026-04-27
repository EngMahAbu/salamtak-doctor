package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.patientAccount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.SharedPreferencesTool.getString
import com.salamtak.databinding.ItemPatientAccountScudualBinding
import com.salamtak.domain.entities.response.Item
import com.salamtak.domain.entities.response.PatientAccountData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding
import java.util.Locale

class PatientAccountAdapter(
    val patientDataList: List<PatientAccountData>,
    val onEditClickListener: PatientAccountAdapter.OnEditClickListener,
    val onDeleteClickListener: PatientAccountAdapter.OnDeleteClickListener
) :
    RecyclerView.Adapter<PatientAccountAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPatientAccountScudualBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PatientAccountAdapter.ViewHolder {

        var binding =
            ItemPatientAccountScudualBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PatientAccountAdapter.ViewHolder(
            binding
        )

    }

    override fun onBindViewHolder(holder: PatientAccountAdapter.ViewHolder, position: Int) {
        holder.binding.patientAccount = patientDataList[position]

        if (Localization.getLanguageName()=="ar"){
            holder.binding.feeTv.text="${patientDataList[position].ServiceFee} جنية "
            holder.binding.paidTv.text="${patientDataList[position].Paid} جنية "
            holder.binding.dateTv.text= DataBinding.convertDateFormat(patientDataList[position].Date, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-M-d", Locale.US)
            holder.binding.balanceTv.text="${patientDataList[position].ServiceFee-patientDataList[position].Paid} جنية "
        }else{
            holder.binding.feeTv.text="${patientDataList[position].ServiceFee} EGP "
            holder.binding.paidTv.text="${patientDataList[position].Paid} EGP "
            holder.binding.balanceTv.text="${patientDataList[position].ServiceFee-patientDataList[position].Paid} EGP "
            holder.binding.dateTv.text=DataBinding.convertDateFormat(patientDataList[position].Date, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-M-d", Locale.US)
        }

        holder.binding.editPatientAccountTv.setOnClickListener {
            onEditClickListener.onEditClicked(patientDataList[position])
        }

        holder.binding.deletePatientAccountTv.setOnClickListener {
            onDeleteClickListener.onDeleteClicked(patientDataList[position])
        }

    }

    override fun getItemCount(): Int {
        return patientDataList.size
    }

    interface OnEditClickListener {
        fun onEditClicked(patientAccountData: PatientAccountData)
    }

    interface OnDeleteClickListener {
        fun onDeleteClicked(patientAccountData: PatientAccountData)
    }

}