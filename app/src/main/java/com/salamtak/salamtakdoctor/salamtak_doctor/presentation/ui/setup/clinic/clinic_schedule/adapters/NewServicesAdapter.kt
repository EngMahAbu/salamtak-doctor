package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemNewServicesBinding
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicTypesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class NewServicesAdapter(val listener: RecyclerViewOnClickListener
) : RecyclerView.Adapter<NewServicesAdapter.ViewHolder>() {

    var list = ArrayList<ServicesTypesData>()
    private val TAG = ClinicTypesAdapter::class.java.simpleName
    private var lastCheckedRB: RadioButton? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewServicesAdapter.ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_new_services,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: NewServicesAdapter.ViewHolder, position: Int) {
        holder.bind(list[position], position)


    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemNewServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServicesTypesData, position: Int) {

            binding.serviceNameTv.text = item.Name

            binding.serviceNameTv.setOnClickListener {

                listener.onClinicTypeSelect(item.Id, item.Name, position)

                if (lastCheckedRB != null) {

                    lastCheckedRB!!.isChecked = false
                    notifyDataSetChanged()
                }
                //store the clicked radiobutton
                lastCheckedRB = binding.serviceNameTv
            }

            binding.executePendingBindings()
        }
    }

    fun submitData(list: ArrayList<ServicesTypesData>) {
        this.list = list
        notifyDataSetChanged()
    }

}