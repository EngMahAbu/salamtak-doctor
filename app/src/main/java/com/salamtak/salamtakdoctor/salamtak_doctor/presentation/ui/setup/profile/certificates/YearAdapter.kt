package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.certificates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemYearBinding


class YearAdapter(var context : Context, var list: MutableList<String>,
                  val adapterOnClick: AdapterOnClick)
    : RecyclerView.Adapter<YearAdapter.ViewHolder>()
{

    var refresh = false
    var selectedPosition = -1
    interface AdapterOnClick{
        fun YearOnClick( year: String)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val binding = ItemYearBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_year, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       with(holder){
           binding.tvYear.text = list[position]



               if(selectedPosition == position){
                   binding.tvYear.isSelected = true
                   binding.tvYear.setTextColor(context.getColor(R.color.white))
               }else{
                   binding.tvYear.isSelected = false
                   binding.tvYear.setTextColor(context.getColor(R.color.black))
               }




           binding.tvYear.setOnClickListener{
               selectedPosition = holder.bindingAdapterPosition
//               refresh = true
//               selectedPosition = position
               adapterOnClick.YearOnClick(list[position])
               notifyDataSetChanged()
           }

       }
    }

    override fun getItemCount(): Int {
       return list.size
    }

}