package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.CityData
import com.salamtak.domain.entities.response.ServiceData
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener


class ServicesAdapter(
    val multipleChoice: Boolean,
    val listener: RecyclerViewOnClickListener, val mContext: Context,
)
    : RecyclerView.Adapter<ServicesAdapter.ViewHolder>()
{

    private var filteredList : ArrayList<ServicesTypesData> = ArrayList()
//    private var originalList: ArrayList<ServicesTypesData> = ArrayList()
    var list = ArrayList<ServicesTypesData>()
    private val TAG = ServicesAdapter::class.java.simpleName
    private val texts = HashMap<String, Boolean>()
    private val idsList = HashMap<String, Boolean>()

    var arrayList: ArrayList<String>? = null
    var itemClickListener: onItemSelectedListener? = null
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_spinner, parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(list[position],position)
        holder.bind(filteredList[position],position)
        val item = list[position]

        holder.binding.checkbox.text = item.Name
        holder.binding.radioButton.text = item.Name

        holder.binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

            texts[item.Name] = isChecked
            idsList[item.Id.toString()] = isChecked
            listener.onServiceSelect(item.Id,item.Name,position)

            selectedPosition = holder.adapterPosition

        }


        holder.binding.radioButton.isChecked = (selectedPosition == position)

        holder.binding.radioButton.setOnCheckedChangeListener { _, bool ->
            // check condition
            if (bool) {
                selectedPosition = holder.absoluteAdapterPosition
                notifyDataSetChanged()
                listener.onServiceSelect(item.Id, item.Name, position)
            }
        }
        if (multipleChoice){
            holder.binding.checkbox.visibility = VISIBLE
        }else{
            holder.binding.radioButton.visibility = VISIBLE

        }
    }

    override fun getItemCount(): Int = filteredList.size


    inner class ViewHolder(val binding: ItemSpinnerBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: ServicesTypesData,position: Int){


            binding.executePendingBindings()

        }

    }

    fun submitData(d: ArrayList<ServicesTypesData>){

        Log.e(TAG, "submitData: $d" )
        this.list=d
        notifyDataSetChanged()
    }

    fun getAdditionIds(): ArrayList<String>{

        val addedTexts = ArrayList<String>()

        this.idsList.forEach{ i ->
            if (i.value && i.key.isNotEmpty()) addedTexts.add(i.key)
        }

        return addedTexts

    }

    fun filterData(query: String) {
        if (query.isBlank()) {
            filteredList.clear()
            filteredList.addAll(list)
        } else {
            filteredList = list.filter { it.Name.contains(query, ignoreCase = true) } as ArrayList<ServicesTypesData>
        }
        notifyDataSetChanged()
    }
    fun getAdditionTexts(): String{

        var addedTexts = ""

        this.texts.forEach{ i ->
            if (i.value && i.key.isNotEmpty()) addedTexts = i.key + "," + addedTexts
        }

        return addedTexts

    }


}

interface onItemSelectedListener{
    fun onItemSelected(id: String, name: String)
}
