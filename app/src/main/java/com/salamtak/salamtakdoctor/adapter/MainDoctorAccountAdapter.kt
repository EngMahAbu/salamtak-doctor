package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemLayoutInvoiceBinding
import com.salamtak.databinding.ItemPatientParentItemBinding
import com.salamtak.domain.entities.response.DataAccount
import com.salamtak.domain.entities.response.doctoraccount.invoice.Item
import com.salamtak.domain.entities.response.doctoraccount.invoice.PatientData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding
import java.util.Locale


class MainDoctorAccountAdapter(val addItems:IAddItems ,val status:Boolean):ListAdapter<PatientData,MainDoctorAccountAdapter.ViewHolder>(DiffCallback()),SubDoctorAccountAdapter.IEdit {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemPatientParentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
       // holder.binding.editPatientAccountTv.visibility=View.GONE
        if (status){
            holder.binding.edit.visibility=View.VISIBLE
        }else{
            holder.binding.edit.visibility=View.GONE
        }
        if (Localization.getLanguageName()=="ar"){
            holder.binding.doctorNameTv.text=services.patientName
            holder.binding.balanceTv.text="${services.totalAmount!! - services.paidAmount!!} جم "
            holder.binding.feeTv.text="${services.totalAmount!!} جم "
            holder.binding.paidTv.text="${services.paidAmount!!} جم "
        }else{
         holder.binding.doctorNameTv.text=services.patientName
            holder.binding.balanceTv.text="${services.totalAmount!! - services.paidAmount!!} LE "
            holder.binding.feeTv.text="${services.totalAmount!!} LE "
            holder.binding.paidTv.text="${services.paidAmount!!} LE "
        }
        val subItemAdapter = SubDoctorAccountAdapter(this)
        holder.binding.rvSubItem.apply {
            adapter=subItemAdapter
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            subItemAdapter.submitList(services.items)
        }

        holder.binding.editNextArrow.setOnClickListener {
            holder.binding.balance.visibility = if ( holder.binding.balance.isShown) View.GONE else View.VISIBLE
            holder.binding.editNextArrow.visibility = if ( holder.binding.editNextArrow.isShown) View.GONE else View.VISIBLE
           holder.binding.editPatientAccountTv.visibility = if ( holder.binding.editPatientAccountTv.isShown) View.GONE else View.VISIBLE
            holder.binding.rvSubItem.visibility = if ( holder.binding.rvSubItem.isShown) View.GONE else View.VISIBLE
        }
        holder.binding.editPatientAccountTv.setOnClickListener {
            holder.binding.balance.visibility = if ( holder.binding.balance.isShown) View.GONE else View.VISIBLE
            holder.binding.editPatientAccountTv.visibility = if ( holder.binding.editPatientAccountTv.isShown) View.GONE else View.VISIBLE
            holder.binding.editNextArrow.visibility = if ( holder.binding.editNextArrow.isShown) View.GONE else View.VISIBLE
            holder.binding.rvSubItem.visibility = if ( holder.binding.rvSubItem.isShown) View.GONE else View.VISIBLE
        }
        holder.binding.mcard.setOnClickListener {
            services.id?.let { it1 -> addItems.onItemClick(it1) }
        }
        holder.binding.edit.setOnClickListener {
            if (services.items?.isNotEmpty() == true) {
                services.id?.let { it1 -> addItems.editAllItems(services, it1) }
            }else{
                services.id?.let { it1 -> addItems.onItemClick(it1) }
            }
        }

    }


    class ViewHolder(itemBinding: ItemPatientParentItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemPatientParentItemBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<PatientData>() {
        override fun areItemsTheSame(oldItem: PatientData, newItem: PatientData): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: PatientData, newItem: PatientData): Boolean {
            return true
        }
    }

    interface IAddItems{
        fun onItemClick(invoiceId:Int)
        fun edit(item: Item)
        fun editAllItems(patientData: PatientData,invoiceId: Int)
    }

    override fun onItemClick(item: Item) {
        item.id?.let { addItems.edit(item) }
    }

}