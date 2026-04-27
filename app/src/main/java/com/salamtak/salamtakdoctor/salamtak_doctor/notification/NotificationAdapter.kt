package com.salamtak.salamtakdoctor.salamtak_doctor.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemNotificationBinding
import com.salamtak.domain.entities.response.NotificationData

class NotificationAdapter(var notificationList : List<NotificationData>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.ViewHolder {

        var binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationAdapter.ViewHolder(
            binding
        )

    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        holder.binding.notification = notificationList[position]

    }

    override fun getItemCount(): Int {
        return notificationList.size

    }


}