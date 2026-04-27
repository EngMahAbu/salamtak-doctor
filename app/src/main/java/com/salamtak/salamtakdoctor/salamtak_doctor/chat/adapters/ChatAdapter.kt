package com.salamtak.salamtakdoctor.salamtak_doctor.chat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemChatReplyViewBinding
import com.salamtak.databinding.ItemChatSenderViewBinding

class ChatAdapter(var mContext : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private const val type = 3
        private const val Lost = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 3) {
            return SenderHolder(
                LayoutInflater.from(mContext)
                    .inflate(R.layout.item_chat_sender_view, parent, false)
            )
        }
        return ReceiverHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.item_chat_reply_view, parent, false)
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ReceiverHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var binding = ItemChatReplyViewBinding.bind(ItemView)

    }

    inner class SenderHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var binding = ItemChatSenderViewBinding.bind(ItemView)

    }

    override fun getItemViewType(position: Int): Int {
        return if (position==1 || position==3 || position==5 || position==7)
            3
        else
            1
    }
}