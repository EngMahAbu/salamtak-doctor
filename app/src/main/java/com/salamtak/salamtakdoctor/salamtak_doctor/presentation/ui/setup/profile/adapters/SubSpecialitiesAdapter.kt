package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.SeniorityLevelsData
import com.salamtak.domain.entities.response.SubSpecialitiesData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class   SubSpecialitiesAdapter(val multipleChoice: Boolean,
                             val listener: RecyclerViewOnClickListener,val mContext: Context)
    : RecyclerView.Adapter<SubSpecialitiesAdapter.ViewHolder>()
{
    var list = ArrayList<SubSpecialitiesData>()
    private val TAG = SubSpecialitiesAdapter::class.java.simpleName
    private val texts = HashMap<String, Boolean>()
    private val idsList = HashMap<String, Boolean>()

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

            binding.checkbox.text = item.Name
            binding.checkbox.visibility = VISIBLE
            binding.radioButton.visibility = GONE
            texts.clear()
            idsList.clear()
            binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    texts[item.Name] = isChecked
                    idsList[item.Id.toString()] = isChecked
                }else{
                    texts.remove(item.Name)
                    idsList.remove(item.Id.toString())
                }

                listener.onSubSpecialitySelect(item.Id,item.Name,position)
//                binding.checkbox.setTextColor(mContext.getColor(R.color.head_color))

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


    fun getAdditionIds(): ArrayList<Int>{

        val addedTexts = ArrayList<Int>()

        this.idsList.forEach{ i ->
            if (i.value && i.key.isNotEmpty()) addedTexts.add(i.key.toInt())
        }

        return addedTexts

    }

    fun getAdditionTexts(): String{

        var addedTexts = ""

        this.texts.forEach{ i ->
            if (i.value && i.key.isNotEmpty()) addedTexts = i.key + " " + addedTexts
        }

        return addedTexts

    }

}