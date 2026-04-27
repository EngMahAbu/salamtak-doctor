package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.vacation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemVactionBinding
import com.salamtak.domain.entities.response.VactionData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.convertDateFormat
import java.text.SimpleDateFormat
import java.util.*

class VacationAdapter(
    val dataList: MutableList<VactionData>,
    val onEditClickListener: VacationAdapter.OnEditClickListener,
    val onDeleteClickListener: VacationAdapter.OnDeleteClickListener
) :
    RecyclerView.Adapter<VacationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemVactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        var binding =
            ItemVactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            binding
        )

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.vaction = dataList[position]
//
        var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S", Locale.getDefault())
        val date = sdf.parse(dataList[position].CreateDate)
        sdf = SimpleDateFormat("EEEE, yyyy-MMM-dd hh:mm a", Locale.getDefault())
        holder.binding.vactionToTv.text = "${convertDateFormat(dataList[position].FromDate,"yyyy-MM-dd'T'HH:mm:ss","EEEE, yyyy-MMM-dd")}- " +
                "${convertDateFormat(dataList[position].ToDate,"yyyy-MM-dd'T'HH:mm:ss","EEEE, yyyy-MMM-dd")}"
        holder.binding.tvVacationissuedate.text="${holder.binding.tvVacationissuedate.context.getString(R.string.createdat)} ${sdf.format(date)}"

        holder.binding.editPatientAccountTv.setOnClickListener {
            onEditClickListener.onEditClicked(dataList[position])
        }

        holder.binding.deletePatientAccountTv.setOnClickListener {
            onDeleteClickListener.onDeleteClicked(dataList[position])
            dataList.removeAt(position)
            notifyItemRemoved(position)


        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnEditClickListener {
        fun onEditClicked(vactionData: VactionData)
    }

    interface OnDeleteClickListener {
        fun onDeleteClicked(vactionData: VactionData)
    }

}