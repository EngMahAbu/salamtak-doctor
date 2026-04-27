//package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters
//
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View.GONE
//import android.view.View.VISIBLE
//import android.view.ViewGroup
//import android.view.animation.Animation
//import android.view.animation.RotateAnimation
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.FragmentManager
//import androidx.lifecycle.LifecycleOwner
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.google.android.material.button.MaterialButton
//import com.salamtak.R
//import com.salamtak.databinding.ClinicScheduleLayoutBinding
//import com.salamtak.domain.entities.response.ClinicData
//import com.salamtak.domain.entities.response.ClinicDayScheduleData
//import com.salamtak.domain.entities.response.ClinicDayScheduleResponse
//import com.salamtak.domain.entities.response.ClinicScheduleData
//import com.salamtak.salamtakdoctor.noConnectivityDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicsAdapter
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.ClinicScheduleFragment
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.TimePickerDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ClinicDurationBottomSheetDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toast
//
//
//class ClinicScheduleAdapter(
//    val listener: RecyclerViewOnClickListener,
//    val mContext: Context,
//    val setupClinicViewModel: SetupClinicViewModel
////                            ,val dialogsListener: DialogsListener
//    ,
//    val childFragmentManager: FragmentManager,
//    val clinicDurationsAdapter: ClinicDurationsAdapter,
//    val viewLifeCycleOwner: LifecycleOwner,
//    val clinicId: String,
//    val loadingDialog: LoadingDialog,
//    val clinicTypeAdapter: NewServicesAdapter
//) : RecyclerView.Adapter<ClinicScheduleAdapter.ViewHolder>(), DialogsListener,
//    RecyclerViewOnClickListener {
//
//    var periodSwitch = false // If ON then send false (Switch is ON by default)
//    var isCollapsed = true
//    private var scheduleList = ArrayList<ClinicScheduleData>()
//
//    //    private lateinit var adapter: SchedulePeriodsAdapter
//    var durationTxt = ""
//    var workingFrom = ""
//    var workingTo = ""
//    var durationId = 0
//    //new
//    var serviceId = 0
//    var serviceTxt = ""
//    var selectedPosition = 0
//
//
//    private val TAG = ClinicDurationsAdapter::class.java.simpleName
//    private lateinit var  noInternetDialog: NoInternetDialog
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
//        DataBindingUtil.inflate(
//            LayoutInflater.from(parent.context),
//            R.layout.clinic_schedule_layout, parent, false
//        )
//    )
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(scheduleList[position], position)
//    }
//
//    override fun getItemCount(): Int = scheduleList.size
//
//    inner class ViewHolder(val binding: ClinicScheduleLayoutBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: ClinicScheduleData, position: Int) {
//
//
//            binding.daySwitch.isChecked = item.Active
//            binding.dayTv.text = item.DayName
//            val period = mContext.getString(R.string.period)
//
//
//            binding.periodsTv.text = "${item.DayPeriodCount.toString()} $period"
//            binding.clinicScheduleLayout.clinicFeesEt.setText(item.FixedFee.toString())
//
//            binding.addPeriodBtn.setOnClickListener {
//                binding.clinicScheduleLayout.root.visibility = VISIBLE
//            }
//
//            setView(binding, item.Active, item.DayId.toString())
//
//            binding.periodsRv.layoutManager = LinearLayoutManager(mContext)
//
//
////            if (item.DayPeriodCount > 0) {
//               var periodCount = getSchedulePeriods(item.DayId.toString(), binding)
//                binding.periodsTv.visibility = VISIBLE
//                binding.periodsTv.setText(periodCount.toString())
////            }
//
//            if(selectedPosition != 0){
//                animateExpand(binding.ivCollapse, mContext)
//                binding.periodsRv.visibility = VISIBLE
//                binding.addPeriodBtn.visibility = VISIBLE
//            }
//
//            binding.daySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
//                setView(binding, isChecked, item.DayId.toString())
//            }
//
//
//
//
//            binding.clinicScheduleLayout.operatingEt.setOnClickListener {
//
//                val timePickerDialog = TimePickerDialog(
//                    this@ClinicScheduleAdapter,
//                    binding.clinicScheduleLayout.operatingEt
//                )
//                timePickerDialog.show(childFragmentManager, "")
//            }
//
//            binding.clinicScheduleLayout.durationSelect.setOnClickListener {
//
//                Log.e(TAG, "bind: ${clinicDurationsAdapter.scheduleList} ")
//                val d = ClinicDurationBottomSheetDialog(
//                    clinicDurationsAdapter.scheduleList,
//                    this@ClinicScheduleAdapter, binding.clinicScheduleLayout.durationSelect
//                )
//                d.show(childFragmentManager, "")
//            }
//            //new add service to schuduale
////            binding.clinicScheduleLayout.servicesSelect.setOnClickListener {
////                val d = ClinicTypeBottomSheetDialog(clinicTypeAdapter.list,
////                    this@ClinicScheduleAdapter, binding.clinicScheduleLayout.servicesSelect)
////                d.show(childFragmentManager, "")
////            }
//
//            binding.clinicScheduleLayout.submitBtn.setOnClickListener {
//
//                if (binding.clinicScheduleLayout.operatingEt.text.toString().isNotEmpty() &&
//                    binding.clinicScheduleLayout.durationSelect.text.toString()
//                        .isNotEmpty() && durationId != 0 && serviceId != 0
//                    && binding.clinicScheduleLayout.clinicFeesEt.text.toString().isNotEmpty()
//                ) {
//                  // TODO Changes 3/1/22
//
////                    listener.onScheduleSubmit(
////                        item.DayId,
////                        durationId,
////                        workingFrom,
////                        workingTo,
////                        binding.clinicScheduleLayout.clinicFeesEt.text.toString().toDouble(),
////                        position,
////                        serviceId,0,0.0,false
////                        ,item.overlapping
////
////                    )
//
//                } else {
//                    toast(mContext, mContext.getString(R.string.fields_required))
//
//                    if (binding.clinicScheduleLayout.operatingEt.text.toString().isNullOrEmpty()) {
//                        binding.clinicScheduleLayout.operatingEt.error =
//                            mContext.getString(R.string.operating_time_required)
//                    }
//
//                    if (binding.clinicScheduleLayout.durationSelect.text.toString().isNullOrEmpty())
//                        binding.clinicScheduleLayout.durationSelect.error =
//                            mContext.getString(R.string.duration_time_required)
//
//                    if (binding.clinicScheduleLayout.clinicFeesEt.text.toString().isNullOrEmpty()
//                        || binding.clinicScheduleLayout.clinicFeesEt.text.toString()
//                            .toDouble() <= 0.0
//                    )
//                        binding.clinicScheduleLayout.clinicFeesEt.error =
//                            mContext.getString(R.string.clinic_fees_error)
//
//                }
//
//            }
//
//            binding.ivCollapse.setOnClickListener {
//
//                if (isCollapsed) {
//                    animateExpand(binding.ivCollapse, mContext)
//                    binding.periodsRv.visibility = VISIBLE
////                    binding.view.visibility = VISIBLE
//                    binding.addPeriodBtn.visibility = VISIBLE
////                    binding.periodsTv.visibility = VISIBLE
//                } else {
//                    animateCollapse(binding.ivCollapse, mContext)
//                    binding.periodsRv.visibility = GONE
////                    binding.view.visibility = GONE
//                    binding.clinicScheduleLayout.root.visibility = GONE
//                    binding.addPeriodBtn.visibility = GONE
//                }
//
//            }
//
//
//
//
////            binding.durationSelect.text = durationTxt
//
//        }
//
//    }
//
//    override fun onDurationConfirm(durationId: Int, durationTxt: String) {
//        this.durationTxt = durationTxt
//
//        Log.e(TAG, "onDurationSelect: $durationTxt $durationId")
//        this.durationId = durationId
//    }
//
//    override fun onServiceConfirm(clinicTypelId: Int, servTxt: String) {
//
//        this.serviceTxt = servTxt
//
//        this.serviceId = clinicTypelId
//    }
//    override fun onTimePick(fromTime: String, toTime: String) {
//        Log.e(TAG, "onTimePick: $fromTime -- $toTime")
//        this.workingFrom = fromTime
//        this.workingTo = toTime
//    }
//
//    private fun setView(binding: ClinicScheduleLayoutBinding, isChecked: Boolean, id: String) {
//
//        if (isChecked) {
////            binding.view.visibility = VISIBLE
////            binding.addPeriodBtn.visibility = VISIBLE
//////            binding.clinicScheduleLayout.root.visibility = VISIBLE
////            binding.periodsTvView.visibility = VISIBLE
////            binding.periodsTv.visibility = VISIBLE
////            binding.ivCollapse.visibility = VISIBLE
//        } else {
////            binding.view.visibility = GONE
////            binding.clinicScheduleLayout.root.visibility = GONE
////            binding.periodsTvView.visibility = GONE
////            binding.addPeriodBtn.visibility = GONE
////            binding.ivCollapse.visibility = GONE
////            binding.periodsRv.visibility = GONE
//        }
//    }
//
//    fun submitData(list: ArrayList<ClinicScheduleData>) {
//        selectedPosition = ClinicScheduleFragment.latestClinicPosition
//        this.scheduleList = list
//        notifyDataSetChanged()
//    }
//
//    private fun getSchedulePeriods(id: String, binding: ClinicScheduleLayoutBinding) :Int {
//
//        var count = 0
//        val adapter = SchedulePeriodsAdapter(
//            this@ClinicScheduleAdapter,
//            mContext,
//            childFragmentManager,
//            setupClinicViewModel,
//            clinicDurationsAdapter,
//            viewLifeCycleOwner,
//            clinicId,
//            loadingDialog
//        )
//
//        binding.periodsRv.adapter = adapter
//
//        count =  adapter.itemCount
//
//
//
//        Log.e(TAG, "getSchedulePeriods:ID $id")
//
//        val periodsListener = object : PeriodsListener {
//            override fun result(response: ClinicDayScheduleResponse) {
//
//                when {
//                    response.Success -> {
//                        loadingDialog.dismiss()
//                        adapter.submitData(response.Data as ArrayList<ClinicDayScheduleData>)
//
//                        // check weather is last page or not
//
//                    }
//                }
//
//            }
//        }
//
//        //todo 29/11/22
//        if(isOnline(mContext)){
//            setupClinicViewModel.getClinicDaySchedule(clinicId, id, periodsListener)
//        }
//        else
//        {
//            noInternetDialog = NoInternetDialog(mContext,this)
//                       noConnectivityDialog.noInternetDialogFragment(mContext)
//
//        }
//
//
//
//        return count
//    }
//
//    private fun animateExpand(binding: ImageView, mContext: Context) {
//        isCollapsed = false
//        val rotate = RotateAnimation(
//            360F,
//            180F,
//            Animation.RELATIVE_TO_SELF,
//            0.5f,
//            Animation.RELATIVE_TO_SELF,
//            0.5f
//        )
//        rotate.duration = 300
//        rotate.fillAfter = true
//        binding.setImageDrawable(mContext.getDrawable(R.drawable.group_71080409))
//    }
//
//    private fun animateCollapse(binding: ImageView, mContext: Context) {
//        isCollapsed = true
//        val rotate = RotateAnimation(
//            180F,
//            360F,
//            Animation.RELATIVE_TO_SELF,
//            0.5f,
//            Animation.RELATIVE_TO_SELF,
//            0.5f
//        )
//        rotate.duration = 300
//        rotate.fillAfter = true
//        binding.setImageDrawable(mContext.getDrawable(R.drawable.down_arrow))
//    }
//
//    //todo
//    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
//        noInternetDialog.dismiss()
//    }
//
//
//
//
//
//
//
//}