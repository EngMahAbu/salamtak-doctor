package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.vacation

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemSpinnerBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import org.koin.core.parameter.parametersOf

class VacationClinicAdapter (val listener: RecyclerViewOnClickListener, val mContext: Context,val onRadioClicked: VacationClinicAdapter.onRadio)
    : RecyclerView.Adapter<VacationClinicAdapter.ViewHolder>() {
    private val allclinks=ArrayList<String>()
    private var list = ArrayList<ClinicData>()
    val TAG = VacationClinicAdapter::class.java.simpleName
    private var lastCheckedRB: RadioButton? = null
    var selectedPos = -1
    var pos = 0

    companion object{
         var clinicName = MutableLiveData<String>()
    }


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
        holder.binding.radioButton.isSelected = selectedPos == holder.position

        holder.binding.radioButton.setOnCheckedChangeListener { _, boolean ->
            if (boolean) {
                onRadioClicked.onRadioClicked(item.ClinicId!!, item.Name!!, position)
//                selectedPos = holder.position
//                notifyDataSetChanged()
            }
            lastCheckedRB?.isChecked = false
            lastCheckedRB = holder.binding.radioButton
        }

    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemSpinnerBinding): RecyclerView.ViewHolder(binding.root)
    {


        fun bind(item: ClinicData,position: Int){

//            allclinks.add(item.AreaName)
            if(allclinks[position] != null){
                binding.radioButton.text=allclinks[position]
            }


/*            binding.radioButton.setOnClickListener {


                if (lastCheckedRB != null) {
                    lastCheckedRB!!.isChecked = false
                }
                //store the clicked radiobutton
                lastCheckedRB = binding.radioButton
//                binding.radioButton.setTextColor(mContext.getColor(R.color.head_color))

            }*/
            binding.executePendingBindings()

        }

    }

    fun submitData(d:ArrayList<ClinicData>){

        Log.e(TAG, "submitData: $d" )
        this.list=d
        for (i in d){
            allclinks.add(i.Name!!)
        }
    }
    interface onRadio{
        fun onRadioClicked(Id:Int,Area:String,position: Int)
    }


}