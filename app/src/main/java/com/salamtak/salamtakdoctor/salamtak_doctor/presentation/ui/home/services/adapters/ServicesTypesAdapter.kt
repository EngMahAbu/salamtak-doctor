package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.databinding.ItemServiceTypeBinding
import com.salamtak.domain.entities.response.ServicesTypesData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener

class ServicesTypesAdapter(
    val mContext: Context, val listener: RecyclerViewOnClickListener
) : RecyclerView.Adapter<ServicesTypesAdapter.ViewHolder>() {

    var list = ArrayList<ServicesTypesData>()
    private val TAG = ServicesTypesAdapter::class.java.simpleName
    private var selectedService: TextView? = null
    var buttonSelected = false
    private val texts = HashMap<String, Boolean>()
    private val idsList = HashMap<String, Boolean>()

    var arrayList: ArrayList<String>? = null
    var itemClickListener: onItemSelectedListener? = null
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_service_type,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)

        if(position == 0 && buttonSelected == false){
            list[position].isSelected = true
        }

        if(list[position].isSelected == false){
            holder.binding.serviceTv!!.background =
                    ContextCompat.getDrawable(mContext, R.drawable.bg_btn_gray)
        }else{
            holder.binding.serviceTv!!.background =
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.bg_btn_border_gray_transparent_selected
                    )
            holder.binding.serviceTv!!.setTextColor(ContextCompat.getColor(mContext, R.color.white))
        }


    }

    override fun getItemCount(): Int = list.size


    inner class ViewHolder(val binding: ItemServiceTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServicesTypesData, position: Int) {

            binding.serviceTv.text = item.Name

            selectedService = binding.serviceTv

//            if (item.Name.equals("Clinic Appointments") && list[0].Name.equals("Clinic Appointments")) {
//                selectedService!!.background =
//                    ContextCompat.getDrawable(
//                        mContext,
//                        R.drawable.bg_btn_border_gray_transparent_selected
//                    )
//            } else {
//                selectedService!!.background =
//                    ContextCompat.getDrawable(mContext, R.drawable.bg_btn_gray)
//            }

            binding.root.setOnClickListener {

                for(i in list.indices){
                    binding.serviceTv!!.background =
                        ContextCompat.getDrawable(mContext, R.drawable.bg_btn_gray)
                    list.get(i).isSelected = false
                    buttonSelected = false

                }

                if(list[position].isSelected == false){
                    list[position].isSelected = true
                    buttonSelected = true


                }
                notifyDataSetChanged()
                listener.onServiceSelect(item.Id, item.Name, position)

//                if (selectedService != null)
//                    selectedService!!.background =
//                        ContextCompat.getDrawable(mContext, R.drawable.bg_btn_gray)
//
//                selectedService = binding.serviceTv
//
//                selectedService!!.setTextColor(ContextCompat.getColor(mContext, R.color.white))
//                selectedService!!.background =
//                    ContextCompat.getDrawable(
//                        mContext,
//                        R.drawable.bg_btn_border_gray_transparent_selected
//                    )
            }
        }
    }

    fun submitData(list: ArrayList<ServicesTypesData>) {
        this.list = list
        notifyDataSetChanged()
    }


    fun filterData(filterList:ArrayList<ServicesTypesData>){
        this.list= filterList
        notifyDataSetChanged()
    }

    fun filter(filterText : String ) : ArrayList<ServicesTypesData>{
        var filteredList = ArrayList<ServicesTypesData>()

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
            if (i.value && i.key.isNotEmpty()) addedTexts = i.key + "," + addedTexts
        }

        return addedTexts

    }

    interface onItemSelectedListener{
        fun onItemSelected(id: String, name: String)
    }

}
