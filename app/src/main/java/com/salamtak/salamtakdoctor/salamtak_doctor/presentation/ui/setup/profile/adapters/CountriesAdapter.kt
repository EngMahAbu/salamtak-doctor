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
import com.salamtak.domain.entities.response.CountriesData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener


class CountriesAdapter(val multipleChoice: Boolean,
                       val listener: RecyclerViewOnClickListener,val mContext: Context)
    : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    private var list = ArrayList<CountriesData>()
    val TAG = CountriesAdapter::class.java.simpleName
    private var lastCheckedRB: RadioButton? = null
    var dataFiltered : Boolean = false


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

    inner class ViewHolder(val binding: ItemSpinnerBinding): RecyclerView.ViewHolder(binding.root)
    {

        fun bind(item: CountriesData,position: Int){

            binding.radioButton.text = item.Name

            binding.radioButton.setOnClickListener {

                listener.onCountrySelect(item.Id,item.Name,position)

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

    fun submitData(d:ArrayList<CountriesData>){

        Log.e(TAG, "submitData: $d" )
        this.list=d
        notifyDataSetChanged()
    }

    fun filterData(filterList:ArrayList<CountriesData>){
        this.list= filterList
        notifyDataSetChanged()
    }

    fun filter(filterText : String ) : ArrayList<CountriesData>{
        var filteredList = ArrayList<CountriesData>()

        for(index in list.indices){
            var countryData = list.get(index)
            if(countryData.Name.lowercase().contains(filterText)){
                filteredList.add(countryData)
            }else{

            }
        }

        if(filteredList.size > 0){
            dataFiltered = true
            filterData(filteredList)
        }else{
            submitData(list)
        }
        return filteredList
    }



}