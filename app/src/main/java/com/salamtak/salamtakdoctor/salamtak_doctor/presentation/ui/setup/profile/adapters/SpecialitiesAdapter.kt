package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.SubSpecialitiesData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class SpecialitiesAdapter(val multipleChoice: Boolean,
                          val listener: RecyclerViewOnClickListener, val mContext: Context):
    RecyclerView.Adapter<SpecialitiesAdapter.ViewHolder>()
{
    var list = ArrayList<SubSpecialitiesData>()
    private val TAG = SpecialitiesAdapter::class.java.simpleName
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


    inner class ViewHolder(val binding: ItemSpinnerBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: SubSpecialitiesData,position: Int){

            binding.radioButton.text = item.Name


            binding.radioButton.setOnClickListener {
                listener.onSpecialitySelect(item.Id,item.Name,position)

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

    fun submitData(d:ArrayList<SubSpecialitiesData>){

        Log.e(TAG, "submitData: $d" )
        this.list=d
        notifyDataSetChanged()
    }

    fun filterData(filterList:ArrayList<SubSpecialitiesData>){
        this.list= filterList
        notifyDataSetChanged()
    }

    fun filter(filterText : String ) : ArrayList<SubSpecialitiesData>{
        var filteredList = ArrayList<SubSpecialitiesData>()

        for(index in list.indices){
            var subSpecialitiesData = list.get(index)
            if(subSpecialitiesData.Name.lowercase().contains(filterText)){
                filteredList.add(subSpecialitiesData)
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