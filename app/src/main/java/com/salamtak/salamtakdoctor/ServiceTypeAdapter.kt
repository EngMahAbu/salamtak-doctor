package com.salamtak.salamtakdoctor

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
import com.salamtak.domain.entities.response.ServiceData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class ServiceTypeAdapter(
    val multipleChoice: Boolean,
    val listener: RecyclerViewOnClickListener, val mContext: Context,
)
    : RecyclerView.Adapter<ServiceTypeAdapter.ViewHolder>()
{
    var list = listOf<ServiceData>()
    private val TAG = ServiceTypeAdapter::class.java.simpleName
    private val texts = HashMap<String, Boolean>()
    private val idsList = HashMap<String, Boolean>()
    var selectedPosition = -1
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

        val item = list[position]

        holder.binding.checkbox.text = item.Name
        holder.binding.radioButton.text = item.Name

        texts.clear()
        idsList.clear()
        holder.binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked){
                texts[item.Name] = isChecked
                idsList[item.Id.toString()] = isChecked
            }else{
                texts.remove(item.Name)
                idsList.remove(item.Id.toString())
            }
            listener.onServiceSelect(item.Id,item.Name,position)

           selectedPosition = holder.adapterPosition

        }

        if (lastCheckedRB != null) {
            lastCheckedRB!!.isChecked = false
        }
        //store the clicked radiobutton
        lastCheckedRB = holder.binding.radioButton


        holder.binding.radioButton.isChecked = (selectedPosition == position)

        holder.binding.radioButton.setOnCheckedChangeListener { _, bool ->
            // check condition
            if (bool) {
                selectedPosition = holder.absoluteAdapterPosition
//                notifyDataSetChanged()
                listener.onServiceSelect(item.Id, item.Name, position)
                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false
                }
                //store the clicked radiobutton
                lastCheckedRB = holder.binding.radioButton

            }
        }
        if (multipleChoice){
            holder.binding.checkbox.visibility = View.VISIBLE
            holder.binding.spinTxt.visibility=View.GONE
            holder.binding.radioButton.visibility = View.GONE

        }else{
            holder.binding.radioButton.visibility = View.VISIBLE
            holder.binding.checkbox.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int = list.size


    inner class ViewHolder(val binding: ItemSpinnerBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: ServiceData, position: Int){

          binding.executePendingBindings()

        }

    }

    fun submitData(d: List<ServiceData>){

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

    fun getAdditionTexts(): String{

        var addedTexts = ""

        this.texts.forEach{ i ->
            if (i.value && i.key.isNotEmpty()) addedTexts = i.key + " " + addedTexts
        }

        return addedTexts

    }

    fun filterData(filterList:ArrayList<ServiceData>){
        this.list= filterList
        notifyDataSetChanged()
    }

    fun filter(filterText : String ) : ArrayList<ServiceData>{
        var filteredList = ArrayList<ServiceData>()

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