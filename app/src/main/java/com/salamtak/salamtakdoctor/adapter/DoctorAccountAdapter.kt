package com.salamtak.salamtakdoctor.adapter


import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemLayoutAddServicesBinding
import com.salamtak.databinding.ItemLayoutInvoiceBinding
import com.salamtak.domain.entities.response.DataAccount
import com.salamtak.domain.entities.response.Item
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DoctorAccountAdapter(val update:IUpdate):ListAdapter<DataAccount,DoctorAccountAdapter.ViewHolder>(DiffCallback()) {
    private val calendar = Calendar.getInstance()
    var paid:String=""
    var service:String=""
    var fee:String=""
    var date:String=""
    var tv_date: EditText?=null
    var start_date: TextView?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutAddServicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services=getItem(position)
        tv_date=holder.binding.etStartdate
        start_date=holder.binding.tvStartDate
        date=services.date.toString()
        holder.binding.btnAddInvoice.text=services.service
        if (services.doctorName=="edit"){
            holder.binding.btnAddItem.text="Update"
        }else{
            holder.binding.btnAddItem.visibility=View.GONE
            holder.binding.layoutDelete.visibility=View.GONE
            holder.binding.btnAddItem.text=holder.binding.btnAddItem.context.getString(R.string.addinvoice)
        }
        if (Localization.getLanguageName()=="ar"){
         //   holder.binding.doctorNameTv.text=services.patientFulltname
            holder.binding.etService.setText(services.service)
            holder.binding.tvStartDate.text = DataBinding.convertDateFormat(services.date, "yyyy-M-d", "MMM dd, yyyy")
           holder.binding.etStartdate.setText(
                DataBinding.convertDateFormat(services.date, "yyyy-M-d",
                    "MMM dd, yyyy", Locale.US))
        //   holder.binding.dateTv.text=DataBinding.convertDateFormat(services.date, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-M-d", Locale.US)
            holder.binding.etFee.setText("${services.serviceFee}")
            holder.binding.etPaid.setText("${services.paid}")
        //    holder.binding.balanceTv.text="${services.paid?.let { services.serviceFee?.minus(it) }} جنية "
        }else{
         //   holder.binding.doctorNameTv.text=services.patientFulltname
            holder.binding.etService.setText(services.service)
            holder.binding.tvStartDate.text = DataBinding.convertDateFormat(services.date, "yyyy-M-d", "MMM dd, yyyy")
            holder.binding.etStartdate.setText(
                DataBinding.convertDateFormat(services.date, "yyyy-M-d",
                    "MMM dd, yyyy", Locale.US))
           // holder.binding.dateTv.text=DataBinding.convertDateFormat(services.date, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-M-d", Locale.US)
            holder.binding.etFee.setText("${services.serviceFee}")
            holder.binding.etPaid.setText("${services.paid}")
          //  holder.binding.balanceTv.text="${services.paid?.let { services.serviceFee?.minus(it.toInt()) }} EGP "
        }
        holder.binding.btnAddInvoice.setOnClickListener {
            holder.binding.layoutOfferPrice.visibility = if ( holder.binding.layoutOfferPrice.isShown) View.GONE else View.VISIBLE
        }

        holder.binding.etFee.doOnTextChanged{text, start, before, count ->
             if (!holder.binding.etFee.text.toString().isNullOrEmpty()){
                 fee=text.toString()
             }
        }
        holder.binding.etPaid.doOnTextChanged{text, start, before, count ->
            if (!holder.binding.etPaid.text.toString().isNullOrEmpty()){
                paid=text.toString()
            }
        }
        holder.binding.etService.doOnTextChanged{text, start, before, count ->
            if (!holder.binding.etService.text.toString().isNullOrEmpty()){
                service=text.toString()
            }
        }
        holder.binding.mcaddDate.setOnClickListener {
            showDatePicker(it.context)
        }

        holder.binding.btnAddItem.setOnClickListener {
            if (!holder.binding.etFee.text.toString().isNullOrEmpty()){
                fee=holder.binding.etFee.text.toString()
            }
            if (!holder.binding.etPaid.text.toString().isNullOrEmpty()){
                paid=holder.binding.etPaid.text.toString()
            }
            if (!holder.binding.etService.text.toString().isNullOrEmpty()){
                service=holder.binding.etService.text.toString()
            }
            val item=com.salamtak.domain.entities.response.doctoraccount.invoice.Item(date,services.id,services.id,
               paid.toDouble().toInt(),service,fee.toDouble().toInt())
            update.update(item)
        }
        holder.binding.layoutDelete.setOnClickListener {
            services.id?.let { it1 -> update.delete(it1) }
        }

    }


    class ViewHolder(itemBinding: ItemLayoutAddServicesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutAddServicesBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<DataAccount>() {
        override fun areItemsTheSame(oldItem: DataAccount, newItem: DataAccount): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: DataAccount, newItem: DataAccount): Boolean {
            return true
        }
    }

    interface IUpdate{
        fun update(item:com.salamtak.domain.entities.response.doctoraccount.invoice.Item)
        fun delete(invoiceId:Int)
    }

    private fun showDatePicker(context: Context){
        // Create a DatePickerDialog
        var date=""
        val datePickerDialog = DatePickerDialog(
            context,R.style.DatePickerTheme_Dark, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                date=formattedDate
                this.date=DataBinding.convertDateFormat(date,
                    "dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                tv_date?.setText(date)
                start_date?.text=DataBinding.convertDateFormat(date, "dd-MM-yyyy", "MMM dd, yyyy")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }

}