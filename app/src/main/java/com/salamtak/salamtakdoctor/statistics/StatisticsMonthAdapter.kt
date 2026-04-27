package com.salamtak.salamtakdoctor.statistics


import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutPackagesInMonthBinding

class StatisticsMonthAdapter(private val selecttime: Action) :
    ListAdapter<String, StatisticsMonthAdapter.ViewHolder>(DiffCallback()) {

    private var selectedItemPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = ItemLayoutPackagesInMonthBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appoinments = getItem(position)
        holder.binding.tvTime.text=appoinments
        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.bindingAdapterPosition
            selecttime.onItemClick(position+1)
            notifyDataSetChanged()

        }

        if (selectedItemPosition == position) {

            holder.binding.layoutTime.background =
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.bg_chooselang_button
                )
            holder.binding.tvTime.setTextColor(Color.WHITE)


        } else {
            holder.binding.layoutTime.background =
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.bg_completeprofile
                )
            holder.binding.tvTime.setTextColor(Color.parseColor("#262D70"))
        }



    }


    class ViewHolder(itemBinding: ItemLayoutPackagesInMonthBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutPackagesInMonthBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return true
        }
    }

    interface Action {
        fun onItemClick(id:Int)
    }


}