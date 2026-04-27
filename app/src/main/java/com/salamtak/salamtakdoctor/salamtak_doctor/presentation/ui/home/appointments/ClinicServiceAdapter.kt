package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.ItemNewSpinnerBinding
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class ClinicServiceAdapter(
    val mContext: Context, val listener: RecyclerViewOnClickListener
) : RecyclerView.Adapter<ClinicServiceAdapter.ViewHolder>() {

    var list = ArrayList<ServicesTypesData>()
    private val TAG = ClinicServiceAdapter::class.java.simpleName
    private var lastCheckedRB: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_new_spinner,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size


    inner class ViewHolder(val binding: ItemNewSpinnerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServicesTypesData, position: Int) {

//            binding.spinTxt.text = item.Name
            binding.cc.text = item.Name
            Glide.with(binding.imageIv.context)
                .load("${item.Image}").into(binding.imageIv)

            binding.radioButton.setOnClickListener {
                listener.onClinicTypeSelect(item.Id, item.Name, position)
                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false
                }
                //store the clicked radiobutton
                lastCheckedRB = binding.radioButton
            }

            binding.executePendingBindings()

        }

    }

    fun submitData(list: ArrayList<ServicesTypesData>) {
        this.list = list
        notifyDataSetChanged()
    }

}