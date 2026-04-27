package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.CityData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class AreasAdapter(val multipleChoice: Boolean,
                   val listener: RecyclerViewOnClickListener, val mContext: Context):
    RecyclerView.Adapter<AreasAdapter.ViewHolder>()
{
    private val TAG = AreasAdapter::class.java.simpleName
    private var lastCheckedRB: RadioButton? = null
    private var filteredList : ArrayList<CityData> = ArrayList()
    private var originalList: ArrayList<CityData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_spinner, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount(): Int = filteredList.size

    inner class ViewHolder(val binding: ItemSpinnerBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CityData, position: Int) {
            binding.radioButton.text = item.Name

            binding.radioButton.setOnClickListener {
                listener.onAreSelect(item.Id, item.Name, position)

                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false
                }
                //store the clicked radiobutton
                lastCheckedRB = binding.radioButton
            }

            binding.executePendingBindings()
        }
    }

    fun filterData(query: String) {
        if (query.isBlank()) {
            filteredList.clear()
            filteredList.addAll(originalList)
        } else {
            filteredList = originalList.filter { it.Name.contains(query, ignoreCase = true) } as ArrayList<CityData>
        }
        notifyDataSetChanged()
    }

    fun submitData(d: ArrayList<CityData>) {
        this.originalList = d
        this.filteredList = d
        notifyDataSetChanged()
    }


}