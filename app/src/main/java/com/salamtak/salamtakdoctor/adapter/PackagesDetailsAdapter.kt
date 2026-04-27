package com.salamtak.salamtakdoctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemLayoutPackagesdetailsBinding
import com.salamtak.domain.entities.response.InsuranceCompany
import com.salamtak.salamtakdoctor.statistics.model.PacakgesDetails

class PackagesDetailsAdapter():ListAdapter<PacakgesDetails,PackagesDetailsAdapter.ViewHolder>(DiffCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutPackagesdetailsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sp=getItem(position)

         holder.binding.apply {
             name.text=sp.patientName
             date.text=sp.appointmentDate

         }

    }

    class ViewHolder(itemBinding: ItemLayoutPackagesdetailsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutPackagesdetailsBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<PacakgesDetails>() {
        override fun areItemsTheSame(oldItem: PacakgesDetails, newItem: PacakgesDetails): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: PacakgesDetails, newItem: PacakgesDetails): Boolean {
            return true
        }
    }


    interface SelectInsuranceCompany{
       fun onSelectInsuranceCompany(listofInsuranceCompanyt:MutableList<InsuranceCompany>)
    }




}