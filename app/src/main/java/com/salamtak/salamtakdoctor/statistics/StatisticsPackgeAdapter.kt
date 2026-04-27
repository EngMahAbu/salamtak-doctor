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
import com.salamtak.databinding.ItemLayoutPackagesBinding
import com.salamtak.salamtakdoctor.statistics.model.PackageStatics

class StatisticsPackgeAdapter(private val selecttime: PackageAction) :
    ListAdapter<PackageStatics, StatisticsPackgeAdapter.ViewHolder>(DiffCallback()) {

    private var selectedItemPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = ItemLayoutPackagesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appoinments = getItem(position)
        holder.binding.tvNumberofPatient.text=appoinments.numberOfPatient.toString()
        holder.binding.tvNumberofPatientInpackage.text="${appoinments.numberOfPatientInPackageStatics}+1"
        holder.binding.tvServiceName.text=appoinments.type


        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.bindingAdapterPosition
            selecttime.onItemClickPackage()
            notifyDataSetChanged()

        }

        if (selectedItemPosition == position) {
            holder.binding.cardview.background =
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.bg_chooselang_button)



        } else {
       //     holder.binding.layputServices.setBackgroundColor(color())

            holder.binding.cardview.background =
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.bg_completeprofile
                )
//            holder.binding.tvTime.setTextColor(Color.parseColor("#262D70"))
//            holder.binding.tvNumberofPatient.setTextColor(Color.WHITE)
//            holder.binding.tvNumberofPatientInpackage.setTextColor(Color.WHITE)

        }



    }


    class ViewHolder(itemBinding: ItemLayoutPackagesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutPackagesBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<PackageStatics>() {
        override fun areItemsTheSame(
            oldItem: PackageStatics,
            newItem: PackageStatics
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PackageStatics,
            newItem: PackageStatics
        ): Boolean {
            return true
        }
    }

    private fun color():Int {
        val random = java.util.Random()
        val color=Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256))
        return color
    }


    interface PackageAction {
        fun onItemClickPackage()
    }


}