package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemAppointmentBinding
import com.salamtak.domain.entities.response.Item

class HistoryAppointmentAdapter (val appointList: List<Item>) : RecyclerView.Adapter<HistoryAppointmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAppointmentAdapter.ViewHolder {

        var binding =
            ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryAppointmentAdapter.ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: HistoryAppointmentAdapter.ViewHolder, position: Int) {

        holder.binding.appointment = appointList[position]

        if (appointList[position].IsCancel){
            holder.binding.cancledButton.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return appointList.size
    }

}