package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemStaticBinding
import com.salamtak.domain.entities.response.GetMedicalEximationDtos
import com.salamtak.domain.entities.response.Item
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentAdapter

class HomeAdapter (
    val staticList: List<GetMedicalEximationDtos>,
    val onAppointmentClickListener: OnAppointmentClickListener,
    ) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemStaticBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        var binding =
            ItemStaticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.statics = staticList[position]

        holder.binding.staticView.setOnClickListener {
            onAppointmentClickListener.onAppointmentClicked(staticList[position])
        }

    }

    override fun getItemCount(): Int {
        return staticList.size
    }

    interface OnAppointmentClickListener {
        fun onAppointmentClicked(static: GetMedicalEximationDtos)
    }


}