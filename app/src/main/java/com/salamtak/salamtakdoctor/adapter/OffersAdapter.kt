package com.salamtak.salamtakdoctor.adapter


import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemLayoutOffersBinding
import com.salamtak.domain.entities.response.offers.Offers
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding


class OffersAdapter(private val select:SelectOfferId):ListAdapter<Offers,OffersAdapter.ViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=ItemLayoutOffersBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data=getItem(position)

        holder.binding.apply {
            tvCategoryname.text="${data.healthEntityName}"
            if (data.offerMainCategoryName==null || data.offerSubCategoryL1Name==null){
                name.text="-"
            }else{
                name.text="${data.offerMainCategoryName} - ${data.offerSubCategoryL1Name}"
            }
            tvApproved.text= checkStatus(data.offerStatus)
            when(tvApproved.text.toString()){
                "Approved"->{
                    holder.binding.tvComment.visibility=View.GONE
                    holder.binding.tvApproved.background=ContextCompat.getDrawable(holder.binding.tvApproved.context,
                        R.drawable.bg_green)
                }
                "In_review"->{
                    holder.binding.tvComment.visibility=View.GONE
                    holder.binding.tvApproved.background=ContextCompat.getDrawable(holder.binding.tvApproved.context,
                        R.drawable.bg_green1)
                    holder.binding.tvApproved.setTextColor(android.graphics.Color.parseColor("#000000"))
                }
                "Comment"->{
                    holder.binding.tvComment.visibility=View.VISIBLE
                    holder.binding.tvApproved.setTextColor(ContextCompat.getColor(holder.binding.tvApproved.context, `in`.aabhasjindal.otptextview.R.color.red))
                    holder.binding.tvApproved.setBackgroundColor(holder.binding.tvApproved.context.getColor(R.color.nowshowcolor))
                }

            }
            tvStartdate.text= DataBinding.convertDateFormat(data.startDate, "yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy")
            tvEnddate.text=DataBinding.convertDateFormat(data.endDate, "yyyy-MM-dd'T'HH:mm:ss", "MMM dd, yyyy")
        }
        holder.itemView.setOnClickListener {
            select.onItemSelected(data.id)
        }


    }

    class ViewHolder(itemBinding: ItemLayoutOffersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutOffersBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<Offers>() {
        override fun areItemsTheSame(oldItem: Offers, newItem: Offers): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Offers, newItem: Offers): Boolean {
            return true
        }
    }


    private fun checkStatus(status:Int):String{
        var sStatus=""
        when(status){
            1->{
              sStatus=  "Approved"
            }
            2->{
                sStatus="Comment"
            }
            3->{
                sStatus= "In_review"
            }
        }
       return sStatus
    }


    interface SelectOfferId{
        fun onItemSelected(offerId:Int)
    }


}