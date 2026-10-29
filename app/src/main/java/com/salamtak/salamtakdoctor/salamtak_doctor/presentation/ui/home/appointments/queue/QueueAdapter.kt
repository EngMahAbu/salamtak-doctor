package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemQueueBinding
import com.salamtak.domain.entities.response.queue.QueueTicket

class QueueAdapter(
    private val clinicName: String,
    private val action: Action
) : ListAdapter<QueueTicket, QueueAdapter.ViewHolder>(DiffCallback()) {

    var currentlyServingTicketId: Int? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = getItem(position)
        val context = holder.itemView.context
        with(holder.binding) {
            tvNumber.text = ticket.ticketPosition.toString()
            tvPatientName.text = ticket.patientName.orEmpty()
            tvClinicName.text = clinicName
            tvBookingTime.text = formatTime(ticket.originalBookingTime)
            // Backend doesn't send the estimate on the queue board yet; default to 15 min
            tvWait.text = context.getString(
                R.string.queue_minutes, ticket.estimatedWaitMinutes ?: DEFAULT_WAIT_MINUTES
            )
            tvStatus.text = ticket.statusName.orEmpty()
            tvStatus.visibility =
                if (ticket.statusName.isNullOrEmpty()) View.GONE else View.VISIBLE

            val isServing = ticket.id == currentlyServingTicketId
            val navy = ContextCompat.getColor(context, R.color.color_primary)
            val lavender = ContextCompat.getColor(context, R.color.move)
            cardQueue.setCardBackgroundColor(
                if (isServing) ContextCompat.getColor(context, R.color.queue_serving_bg)
                else ContextCompat.getColor(context, R.color.white)
            )
            cardQueue.strokeColor =
                if (isServing) navy else ContextCompat.getColor(context, R.color.text_gray4)
            cardNumber.setCardBackgroundColor(if (isServing) navy else lavender)
            tvNumber.setTextColor(
                if (isServing) ContextCompat.getColor(context, R.color.white) else navy
            )

            btnComplete.setOnClickListener { action.onCompleteClick(ticket) }
            btnCancel.setOnClickListener { action.onCancelClick(ticket) }
        }
    }

    // "2026-11-02T10:00:00" -> "10:00"
    private fun formatTime(dateTime: String?): String {
        val time = dateTime?.substringAfter('T', "") ?: ""
        return if (time.length >= 5) time.substring(0, 5) else "--"
    }

    class ViewHolder(itemBinding: ItemQueueBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemQueueBinding = itemBinding
    }

    private class DiffCallback : DiffUtil.ItemCallback<QueueTicket>() {
        override fun areItemsTheSame(oldItem: QueueTicket, newItem: QueueTicket): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QueueTicket, newItem: QueueTicket): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val DEFAULT_WAIT_MINUTES = 15
    }

    interface Action {
        fun onCompleteClick(ticket: QueueTicket)
        fun onCancelClick(ticket: QueueTicket)
    }
}
