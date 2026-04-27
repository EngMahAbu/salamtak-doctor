package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemLayoutInsurancecompanyBinding
import com.salamtak.domain.entities.response.InsuranceCompany


class InsuranceCompanyAdapter(private val selectInsuranceCompany:SelectInsuranceCompany):ListAdapter<InsuranceCompany,InsuranceCompanyAdapter.ViewHolder>(DiffCallback()) {

    val subSpeciaListData= mutableListOf<InsuranceCompany>()
    private var selectedItemPosition=-1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutInsurancecompanyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sp=getItem(position)

        holder.binding.radioSpecialist.text=sp.name
        subSpeciaListData.clear()
          holder.binding.radioSpecialist.setOnCheckedChangeListener { buttonView, isChecked ->
              if (isChecked) {
                  selectedItemPosition = holder.bindingAdapterPosition
                  subSpeciaListData.add(sp)
              } else {
                  subSpeciaListData.remove(sp)
              }
              selectInsuranceCompany.onSelectInsuranceCompany(subSpeciaListData)
          }
        holder.binding.radioSpecialist.isChecked = selectedItemPosition == position
//        holder.binding.radioSpecialist.setOnClickListener { view ->
//
//            notifyDataSetChanged()
//        }


    }

    class ViewHolder(itemBinding: ItemLayoutInsurancecompanyBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutInsurancecompanyBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<InsuranceCompany>() {
        override fun areItemsTheSame(oldItem: InsuranceCompany, newItem: InsuranceCompany): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: InsuranceCompany, newItem: InsuranceCompany): Boolean {
            return true
        }
    }


    interface SelectInsuranceCompany{
       fun onSelectInsuranceCompany(listofInsuranceCompanyt:MutableList<InsuranceCompany>)
    }




}