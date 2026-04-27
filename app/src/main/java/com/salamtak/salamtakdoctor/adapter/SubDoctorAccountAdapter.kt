package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemLayoutInvoiceBinding
import com.salamtak.domain.entities.response.doctoraccount.invoice.Item
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding
import java.util.Locale


class SubDoctorAccountAdapter(val edit:IEdit):ListAdapter<Item,SubDoctorAccountAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutInvoiceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
       // holder.binding.editPatientAccountTv.visibility=View.GONE
        if (Localization.getLanguageName()=="ar"){
         //   holder.binding.doctorNameTv.text=services.patientFulltname
            holder.binding.serviceTv.text=services.service
            holder.binding.dateTv.text= DataBinding.convertDateFormat(services.date, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", Locale.US)
            holder.binding.feeTv.text="${services.serviceFee} جم "
            holder.binding.paidTv.text="${services.paid} جم "
            holder.binding.balnceTv.text="${services.serviceFee!! - services.paid!!} جم "
        //    holder.binding.balanceTv.text="${services.paid?.let { services.serviceFee?.minus(it) }} جنية "
        }else{
         //   holder.binding.doctorNameTv.text=services.patientFulltname
            holder.binding.serviceTv.text=services.service
            holder.binding.dateTv.text=DataBinding.convertDateFormat(services.date, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", Locale.US)
            holder.binding.feeTv.text="${services.serviceFee} LE "
            holder.binding.paidTv.text="${services.paid} LE "
            holder.binding.balnceTv.text="${services.serviceFee!! - services.paid!!} LE "
          //  holder.binding.balanceTv.text="${services.paid?.let { services.serviceFee?.minus(it.toInt()) }} EGP "
        }
        holder.binding.tab.setOnClickListener {
            edit.onItemClick(services)
        }


    }


    class ViewHolder(itemBinding: ItemLayoutInvoiceBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutInvoiceBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return true
        }
    }

    interface IEdit{
        fun onItemClick(item:Item)
    }

}