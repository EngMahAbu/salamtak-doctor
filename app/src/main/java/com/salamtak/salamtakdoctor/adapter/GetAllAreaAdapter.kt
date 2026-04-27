package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemLayoutSpecialistBinding
import com.salamtak.domain.entities.response.AreaData


class GetAllAreaAdapter(private val selectarea:SelectArea):ListAdapter<AreaData,GetAllAreaAdapter.ViewHolder>(DiffCallback()) {

    var selectedPosition=-1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutSpecialistBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sp=getItem(position)

        if (Localization.getLanguageName() == "ar"){
            holder.binding.radioSpecialist.text=sp.name
        }else{
            holder.binding.radioSpecialist.text=sp.name
        }
        holder.binding.radioSpecialist.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectarea.onSelectArea(sp)
            }
        })
        holder.binding.radioSpecialist.setOnClickListener { view ->
            selectedPosition = holder.bindingAdapterPosition
            notifyDataSetChanged()
        }

        holder.binding.radioSpecialist.isChecked = selectedPosition == position

    }

    class ViewHolder(itemBinding: ItemLayoutSpecialistBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutSpecialistBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<AreaData>() {
        override fun areItemsTheSame(oldItem: AreaData, newItem: AreaData): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: AreaData, newItem: AreaData): Boolean {
            return true
        }
    }

    interface SelectArea{
        fun onSelectArea(Area:AreaData)
    }



}