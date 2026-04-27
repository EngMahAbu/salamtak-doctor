package com.salamtak.salamtakdoctor.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutNotificationsBinding
import com.salamtak.domain.entities.response.NotificationList


class NotificationsAdapter() :
    ListAdapter<NotificationList, NotificationsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            ItemLayoutNotificationsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services = getItem(position)

        holder.binding.tvTitle.text = services.title
        holder.binding.tvSpecialistName.text = services.message
        if (services.isSeen){
            holder.binding.layoutServices.setBackgroundColor( ContextCompat.getColor(holder.binding.layoutServices.context, R.color.white))
        }else{
            holder.binding.layoutServices.setBackgroundColor( ContextCompat.getColor(holder.binding.layoutServices.context, R.color.move))
        }

    }


    class ViewHolder(itemBinding: ItemLayoutNotificationsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutNotificationsBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<NotificationList>() {
        override fun areItemsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
            return true
        }
    }

    interface Action{
        fun onItemClickDoctorsDetails(DoctorId: Int)
    }

}