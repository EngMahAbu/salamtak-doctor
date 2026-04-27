package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.CountriesData
import com.salamtak.domain.entities.response.SeniorityLevelsData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class SeniorityLevelsAdapter(val multipleChoice: Boolean,
                             val listener: RecyclerViewOnClickListener,val mContext: Context)
    : RecyclerView.Adapter<SeniorityLevelsAdapter.ViewHolder>()
{

    private var list = ArrayList<SeniorityLevelsData>()
    private val TAG = SeniorityLevelsAdapter::class.java.simpleName
    private var lastCheckedRB: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_spinner, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position],position)
    }

    override fun getItemCount(): Int = list.size



    inner class ViewHolder(val binding: ItemSpinnerBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(item: SeniorityLevelsData,position: Int){

            binding.radioButton.text = item.Name


            binding.radioButton.setOnClickListener {
                listener.onSenioritySelect(item.Id,item.Name,position)
                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false
                }
                //store the clicked radiobutton
                lastCheckedRB = binding.radioButton
//                binding.radioButton.setTextColor(mContext.getColor(R.color.head_color))

            }


            binding.executePendingBindings()

        }

    }

    fun submitData(d:ArrayList<SeniorityLevelsData>){
        Log.e(TAG, "submitData: $d" )
        this.list=d
        notifyDataSetChanged()
    }

    fun filterData(filterList:ArrayList<SeniorityLevelsData>){
        this.list= filterList
        notifyDataSetChanged()
    }

    fun filter(filterText : String ) : ArrayList<SeniorityLevelsData>{
        var filteredList = ArrayList<SeniorityLevelsData>()

        for(index in list.indices){
            var seniorityData = list.get(index)
            if(seniorityData.Name.lowercase().contains(filterText)){
                filteredList.add(seniorityData)
            }else{

            }
        }

        if(filteredList.size > 0){
            filterData(filteredList)
        }else{
            submitData(list)
        }
        return filteredList
    }


}