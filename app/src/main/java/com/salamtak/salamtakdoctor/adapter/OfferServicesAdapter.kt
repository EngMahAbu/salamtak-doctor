package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemLayoutServicesBinding
import com.salamtak.domain.entities.response.offers.ServicesData


class OfferServicesAdapter(private val select:SelectInsuranceCard):ListAdapter<ServicesData,OfferServicesAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutServicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sp=getItem(position)
            holder.binding.radioSpecialist.text=sp.name
        holder.binding.radioSpecialist.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                select.onItemSelected(sp.id,sp.name)
            }
        })
        holder.binding.radioSpecialist.setOnClickListener { view ->
            selectedPosition = holder.bindingAdapterPosition
            notifyDataSetChanged()
        }

        holder.binding.radioSpecialist.isChecked = selectedPosition == position

    }

    class ViewHolder(itemBinding: ItemLayoutServicesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutServicesBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<ServicesData>() {
        override fun areItemsTheSame(oldItem: ServicesData, newItem: ServicesData): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: ServicesData, newItem: ServicesData): Boolean {
            return true
        }
    }


    interface SelectInsuranceCard{
        fun onItemSelected(id:Int,name:String)
    }


}