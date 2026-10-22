package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ClinicScheduleLayoutBinding
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.ScheduleModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.SetupServicesFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.convertTime
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.TimePickerDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ClinicDurationBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.toast
import java.text.SimpleDateFormat
import java.util.*


class ServicesScheduleAdapter(
    val Iitem:IDeleteSchedule,
    val listener: RecyclerViewOnClickListener,
    val mContext: Context,
    val setupServicesViewModel: SetupServicesViewModel,
//                            ,val dialogsListener: DialogsListener
    val childFragmentManager: FragmentManager,
    val serviceDurationsAdapter: ServiceDurationsAdapter,
    val viewLifeCycleOwner: LifecycleOwner,
    var serviceId: String,
    val loadingDialog: LoadingDialog,
    val clinicId: String,

    ) : RecyclerView.Adapter<ServicesScheduleAdapter.ViewHolder>(), DialogsListener,
    RecyclerViewOnClickListener,ServicesPeriodsAdapter.IDeleteSchedule {
    var bottomservicestxt: TextView? = null
    var isCollapsed = true
    private var scheduleList = ArrayList<ServiceScheduleData>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var noInternetDialog: NoInternetDialog
    private val calendar = Calendar.getInstance()


    //    private lateinit var adapter: SchedulePeriodsAdapter
    var durationTxt = ""
    var workingFrom = ""
    var workingTo = ""
    var durationId = 0
    var maxpatientno = 0
    var appointmenttype=""
    var services: Int = 0
    var clinicfee: Int = 0
    var maxPatient: Int = 0
    var clinicfollowup: Int = 0
    lateinit var switch: SwitchCompat
    private val TAG = ServicesScheduleAdapter::class.java.simpleName
    var selectedPosition = -1
    var dateResult:String?=null
    var tv_date:TextView?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.clinic_schedule_layout, parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("jsdklajsdklajskldjaskljd",scheduleList.toString())
        holder.bind(scheduleList[position], position)
        LocalBroadcastManager.getInstance(mContext)
            .registerReceiver(broadcastReceiver, IntentFilter("onServiceSelect"))
    }

    override fun getItemCount(): Int = scheduleList.size

    inner class ViewHolder(val binding: ClinicScheduleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("InvalidAnalyticsName", "SuspiciousIndentation")
        fun bind(item: ServiceScheduleData, position: Int) {
            val bottomSheetDialog = BottomSheetDialog(mContext)
            bottomSheetDialog.setContentView(R.layout.add_new_peroid)
            bottomSheetDialog.findViewById<TextView>(R.id.day_select)?.text=dateResult
            binding.daySwitch.isChecked = item.Active
            binding.dayTv.text = item.DayName
            val period = mContext.getString(R.string.periods)
            binding.periodsTv.text = "$period ${item.DayPeriodCount.toString()} "
            binding.clinicScheduleLayout.clinicFeesEt.setText(item.FixedFee.toString())

            binding.addPeriodBtn.setOnClickListener {
                binding.clinicScheduleLayout.root.visibility = GONE
                binding.clinicScheduleLayout.editappointment.visibility = GONE

                bottomSheetDialog.show()

                val newperoid = bottomSheetDialog.findViewById<TextView>(R.id.new_peroid)
                val hoursto = bottomSheetDialog.findViewById<RelativeLayout>(R.id.relative2)
                val hoursfrom = bottomSheetDialog.findViewById<RelativeLayout>(R.id.relative1)
                val switch = bottomSheetDialog.findViewById<SwitchCompat>(R.id.day_switch1)
                bottomservicestxt =
                    bottomSheetDialog.findViewById<TextView>(R.id.services_select)
                val bottomClinicfee = bottomSheetDialog.findViewById<EditText>(R.id.checkfee_et)
                val bottomclinicfolow = bottomSheetDialog.findViewById<EditText>(R.id.checkupfee_et)
                val hours_to = bottomSheetDialog.findViewById<TextView>(R.id.hoursto)
                val tvClinicCheckfee = bottomSheetDialog.findViewById<TextView>(R.id.tv_Clinic_Checkfee)
                val tv_Clinic_services_select = bottomSheetDialog.findViewById<TextView>(R.id.tv_Clinic_services_select)
                val tv_Clinic_maxPatient = bottomSheetDialog.findViewById<TextView>(R.id.tv_Clinic_maxPatient)
                val tv_Clinic_time = bottomSheetDialog.findViewById<TextView>(R.id.tv_Clinic_time)
                val maxPatient = bottomSheetDialog.findViewById<EditText>(R.id.clinic_name_et)
                val mcard = bottomSheetDialog.findViewById<ImageView>(R.id.iv_durationspinner)
                val hours_from = bottomSheetDialog.findViewById<TextView>(R.id.hoursfrom)
                val save_btn = bottomSheetDialog.findViewById<MaterialButton>(R.id.savePeriod_btn)
                val select_date=bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_SelectDate)
                tv_date=bottomSheetDialog.findViewById<TextView>(R.id.day_select)
                if (item.DayId==0){
                    select_date?.visibility= View.VISIBLE
                    if (Localization.getLanguageName()=="ar"){
                        newperoid?.text="يوم استثنائى"
                    }else{
                        newperoid?.text="Exceptional Day"
                    }
                }else{
                    select_date?.visibility= View.GONE
                    tv_date?.text=" "
                }

                bottomservicestxt!!.setOnClickListener {
                    listener.onTypeService()
                }
                mcard?.setOnClickListener {

                    Log.e(TAG, "bind: ${serviceDurationsAdapter.scheduleList} ")
                    val d = ClinicDurationBottomSheetDialog(
                        serviceDurationsAdapter.scheduleList,
                        this@ServicesScheduleAdapter, maxPatient!!
                    )
                    d.show(childFragmentManager, "")
                }
                hoursto?.setOnClickListener {
                    val timeSheetDialog = BottomSheetDialog(mContext)
                    timeSheetDialog.setContentView(R.layout.timepickerbottomsheet)
                    timeSheetDialog.show()
                    val timepicker = timeSheetDialog.findViewById<TimePicker>(R.id.time_picker)

                    val done = timeSheetDialog.findViewById<MaterialButton>(R.id.time_done)

                    timepicker?.setOnTimeChangedListener { _, selectedHour, selectedMinute ->

                        workingTo = "${String.format("%02d", selectedHour)}:${
                            String.format(
                                "%02d",
                                selectedMinute
                            )
                        }"
                        val sdf = SimpleDateFormat("HH:mm", Locale.US)
                        val date = sdf.parse(workingTo)

                        workingTo = sdf.format(date)
                        hours_to!!.text = convertTime(workingTo,"HH:mm","hh:mm a")
                        Log.e(TAG, "setUi:From $workingTo")

                    }

                    done?.setOnClickListener {
                        timeSheetDialog.dismiss()
                    }
                }
                hoursfrom?.setOnClickListener {
                    val timeSheetDialog = BottomSheetDialog(mContext)
                    timeSheetDialog.setContentView(R.layout.timepickerbottomsheet)
                    val headertxt = timeSheetDialog.findViewById<TextView>(R.id.header_title_txt)
                    headertxt!!.text = it.context.getString(R.string.operation_hours_from)
                    timeSheetDialog.show()
                    val timepicker = timeSheetDialog.findViewById<TimePicker>(R.id.time_picker)
                    val done = timeSheetDialog.findViewById<MaterialButton>(R.id.time_done)

                    timepicker?.setOnTimeChangedListener { _, selectedHour, selectedMinute ->

                        workingFrom = "${String.format("%02d", selectedHour)}:${
                            String.format(
                                "%02d",
                                selectedMinute
                            )
                        }"
                        val sdf = SimpleDateFormat("HH:mm", Locale.US)
                        val date = sdf.parse(workingFrom)

                        workingFrom = sdf.format(date)
                        hours_from!!.text = convertTime(workingFrom,"HH:mm","hh:mm a")


                        Log.e(TAG, "setUi:To $workingFrom")

                    }

                    done?.setOnClickListener {
                        timeSheetDialog.dismiss()
                    }

                    switch?.setOnCheckedChangeListener { buttonView, isChecked ->
                        setView(binding, isChecked, item.DayId.toString())
                        val logger = AppEventsLogger.newLogger(mContext)
                        logger.logEvent("Appointment changing")

                        // firebase event
                        firebaseAnalytics = Firebase.analytics
                        val bundle = Bundle()
                        firebaseAnalytics.logEvent("Appointment changing", bundle)
                    }

                }
                save_btn?.setOnClickListener {

                 //   workingFrom = hours_from!!.text.toString()
                //    workingTo = hours_to!!.text.toString()
                    if (bottomClinicfee!!.text.toString()
                            .isNullOrEmpty() || workingFrom.isNullOrEmpty()
                        || workingTo.isNullOrEmpty() || maxPatient!!.text.toString()
                            .isNullOrEmpty()
                    ) {

                        tvClinicCheckfee?.text = tvClinicCheckfee?.context?.getString(
                            R.string.check_up_fees_is,
                            it.context.getString(R.string.required)
                        )
                        tvClinicCheckfee?.visibility = View.VISIBLE
                        tv_Clinic_services_select?.text =
                            tv_Clinic_services_select?.context?.getString(
                                R.string.booking_type_is,
                                it.context.getString(R.string.required)
                            )
                        tv_Clinic_services_select?.visibility = View.VISIBLE
                        tv_Clinic_maxPatient?.text = tv_Clinic_maxPatient?.context?.getString(
                            R.string.max_number_of_booking_period_is,
                            it.context.getString(R.string.required)
                        )
                        tv_Clinic_maxPatient?.visibility = View.VISIBLE
                        tv_Clinic_time?.text = tv_Clinic_time?.context?.getString(
                            R.string.period_time_from_to_is,
                            it.context.getString(R.string.required)
                        )
                        tv_Clinic_time?.visibility = View.VISIBLE

                        Toast.makeText(
                            mContext,
                            "You must fill the required fields",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (bottomClinicfee.text.toString()
                            .isNotEmpty() && hours_from!!.text.toString().isNotEmpty()
                        && hours_to!!.text.toString().isNotEmpty() && maxPatient!!.text.toString()
                            .isNotEmpty()
                    ) {

                        clinicfee = bottomClinicfee!!.text.toString().toInt()
                        if ( !bottomclinicfolow!!.text.toString().isNullOrEmpty()){
                            clinicfollowup = bottomclinicfolow!!.text.toString().toInt()
                        }
                        maxpatientno = maxPatient!!.text.toString().toInt()

                        listener.onScheduleSubmit(
                            item.DayId,
                            1,
                            workingFrom,
                            workingTo,
                            clinicfee.toDouble(),
                            position, 1,
                            durationId,
                            clinicfollowup.toDouble(),
                            !switch!!.isChecked,
                            item.OverlapApproved,
                            dateResult
                        )
                        bottomSheetDialog.dismiss()
                    }

                    //Need to apply this,If possible
//                    scheduleList.clear()


                }


                newperoid?.setOnClickListener {
                    val canceldialog = BottomSheetDialog(mContext)
                    canceldialog.setContentView(R.layout.cancelappointmentbottomsheet)
                    val textview = canceldialog.findViewById<TextView>(R.id.tv_display)
                    textview?.text = "Do you want to exit from adding a new period"
                    canceldialog.show()

                }

                tv_date?.setOnClickListener {
                    showDatePicker(it.context)
                }

            }


            setView(binding, item.Active, item.DayId.toString())

            binding.periodsRv.layoutManager = LinearLayoutManager(mContext)


//            if (item.DayPeriodCount > 0) {
            Log.d("dayPeriodCountAndItemDayIdAndPosition","${item.DayPeriodCount} and ${item.DayId} and $position")
            getSchedulePeriods(item.DayId.toString(), binding, position)
//            }


            binding.daySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                setView(binding, isChecked, item.DayId.toString())
                val logger = AppEventsLogger.newLogger(mContext)
                logger.logEvent("Appointment changing")

                // firebase event
                firebaseAnalytics = Firebase.analytics
                val bundle = Bundle()
                firebaseAnalytics.logEvent("Appointment changing", bundle)

            }



            binding.clinicScheduleLayout.operatingEt.setOnClickListener {

                val timePickerDialog = TimePickerDialog(
                    this@ServicesScheduleAdapter,
                    binding.clinicScheduleLayout.operatingEt
                )
                timePickerDialog.show(childFragmentManager, "")
            }

            binding.clinicScheduleLayout.durationSelect.setOnClickListener {

                Log.e(TAG, "bind: ${serviceDurationsAdapter.scheduleList} ")
                val d = ClinicDurationBottomSheetDialog(
                    serviceDurationsAdapter.scheduleList,
                    this@ServicesScheduleAdapter, binding.clinicScheduleLayout.durationSelect
                )
                d.show(childFragmentManager, "")
            }

            binding.clinicScheduleLayout.submitBtn.setOnClickListener {

                if (binding.clinicScheduleLayout.operatingEt.text.toString().isNotEmpty() &&
                    binding.clinicScheduleLayout.durationSelect.text.toString()
                        .isNotEmpty() && durationId != 0
                    && binding.clinicScheduleLayout.clinicFeesEt.text.toString().isNotEmpty() &&
                    binding.clinicScheduleLayout.clinicFeesEt.text.toString().length >= 2
                ) {

                    if (binding.clinicScheduleLayout.clinicFollowUpFeesEt.text.toString()
                            .isNullOrEmpty()
                    ) {
                        listener.onScheduleSubmit(
                            item.DayId,
                            durationId,
                            workingFrom,
                            workingTo,
                            clinicfee.toDouble(),
                            position, 1,
                            durationId,
                            clinicfollowup.toDouble(),
                            !switch.isChecked,
                            item.OverlapApproved,
                            dateResult
                        )
                    } else {
                        listener.onScheduleSubmit(
                            item.DayId,
                            durationId,
                            workingFrom,
                            workingTo,
                            binding.clinicScheduleLayout.clinicFeesEt.text.toString().toDouble(),
                            position, 1,
                            durationId,
                            clinicfollowup.toDouble(),
                            !switch.isChecked,
                            item.OverlapApproved,
                            dateResult
                        )
                    }

                } else {
                    toast(mContext, mContext.getString(R.string.fields_required))

                    if (binding.clinicScheduleLayout.operatingEt.text.toString().isNullOrEmpty()) {
                        binding.clinicScheduleLayout.operatingEt.error =
                            mContext.getString(R.string.operating_time_required)
                    }

                    if (binding.clinicScheduleLayout.durationSelect.text.toString().isNullOrEmpty())
                        binding.clinicScheduleLayout.durationSelect.error =
                            mContext.getString(R.string.duration_time_required)

                    if (binding.clinicScheduleLayout.clinicFeesEt.text.toString().isNullOrEmpty()
                        || binding.clinicScheduleLayout.clinicFeesEt.text.toString().length <= 2
                    )
                        binding.clinicScheduleLayout.clinicFeesEt.error =
                            mContext.getString(R.string.clinic_fees_error)
                }

            }


//            binding.ivCollapse.setOnClickListener {
//
//                if (isCollapsed) {
//                    animateExpand(binding.ivCollapse, mContext)
//                    binding.periodsRv.visibility = VISIBLE
//                } else {
//                    animateCollapse(binding.ivCollapse, mContext)
//                    binding.periodsRv.visibility = GONE
//                }
//
//            }

            val toggleDay = View.OnClickListener {

                // Use this card's own state; isCollapsed is shared by every card in the adapter
                if (binding.periodsRv.visibility != VISIBLE) {
                    animateExpand(binding.ivCollapse, mContext)
                    binding.periodsRv.visibility = VISIBLE
                    // Periods may still be loading if the day is opened right after the tab loads
                    if (binding.periodsLoading.tag != null) binding.periodsLoading.visibility = VISIBLE
//                    binding.view.visibility = VISIBLE
                    binding.addPeriodBtn.visibility = VISIBLE
//                    binding.periodsTv.visibility = VISIBLE
//                    binding.ivCollapse.visibility = VISIBLE
                } else {
                    animateCollapse(binding.ivCollapse, mContext)
                    binding.periodsRv.visibility = GONE
                    binding.periodsLoading.visibility = GONE
//                    binding.view.visibility = GONE
                    binding.clinicScheduleLayout.root.visibility = GONE
                    binding.addPeriodBtn.visibility = GONE
//                    binding.ivCollapse.visibility = GONE
                }

            }
            binding.ivCollapse.setOnClickListener(toggleDay)
            binding.headerClickArea.setOnClickListener(toggleDay)
            // A collapsed card is just its header, so let a tap anywhere on it expand it
            binding.root.setOnClickListener {
                if (binding.periodsRv.visibility != VISIBLE) toggleDay.onClick(it)
            }


        }


    }

    override fun onDurationConfirm(durationId: Int, durationTxt: String) {
        this.durationTxt = durationTxt

        Log.e(TAG, "onDurationSelect: $durationTxt $durationId")
        this.durationId = durationId

    }

    override fun onTimePick(fromTime: String, toTime: String) {
        Log.e(TAG, "onTimePick: $fromTime -- $toTime")
        this.workingFrom = fromTime
        this.workingTo = toTime
    }

    private fun setView(binding: ClinicScheduleLayoutBinding, isChecked: Boolean, id: String) {

        if (isChecked) {
//            binding.view.visibility = VISIBLE
//            binding.addPeriodBtn.visibility = VISIBLE
//            binding.periodsTvView.visibility = VISIBLE
//            binding.periodsTv.visibility = VISIBLE
//            binding.ivCollapse.visibility = VISIBLE
//            binding.periodsRv.visibility = VISIBLE
        } else {
//            binding.view.visibility = GONE
//            binding.periodsTvView.visibility = GONE
//            binding.clinicScheduleLayout.root.visibility = GONE
//            binding.addPeriodBtn.visibility = GONE
//            binding.ivCollapse.visibility = GONE
//            binding.periodsRv.visibility = GONE
        }
    }

    fun submitData(list: ArrayList<ServiceScheduleData>) {
        selectedPosition = SetupServicesFragment.selectedPeriodPosition
        Log.d("sjadkajsdkjds",list.toString())
        this.scheduleList = list
        notifyDataSetChanged()
    }

    private fun getSchedulePeriods(id: String, binding: ClinicScheduleLayoutBinding, position : Int) {

        val adapter = ServicesPeriodsAdapter(
            this@ServicesScheduleAdapter,
            mContext,
            this,
            childFragmentManager,
            setupServicesViewModel,
            serviceDurationsAdapter,
            viewLifeCycleOwner,
            serviceId,
            clinicId,
            loadingDialog
        )

        binding.periodsRv.adapter = adapter

//        Log.e(TAG, "getSchedulePeriods:ID $id $serviceId $clinicId")


        if(selectedPosition == position){
            animateCollapse(binding.ivCollapse, mContext)
            binding.periodsRv.visibility = GONE
            binding.addPeriodBtn.visibility = GONE
        }

        // Token marks this card's in-flight request; a recycled card gets a new one,
        // so a late result for its previous day can't hide the new day's spinner
        val loadingToken = Any()
        binding.periodsLoading.tag = loadingToken
        binding.periodsLoading.visibility =
            if (binding.periodsRv.visibility == VISIBLE) VISIBLE else GONE

        val periodsListener = object : ServicesPeriodsListener {
            override fun result(response: ServiceDayScheduleResponse) {

                // Called on both success and failure, so the spinner never stays forever
                if (binding.periodsLoading.tag === loadingToken) {
                    binding.periodsLoading.tag = null
                    binding.periodsLoading.visibility = GONE
                }

                when {
                    response.Success -> {
                        loadingDialog.dismiss()
                        Log.d(TAG, "APIRESULT: ${response.Data}")
                        adapter.submitData(response.Data as ArrayList<ServiceDayScheduleData>)

                        // check weather is last page or not

                    }
                }

            }
        }
        // TODO 29/11/22
        if (isOnline(mContext)) {
            Log.d("ssdfdksjdklsjdkl","serviceId: $serviceId and id: $id and : $periodsListener and : $clinicId")
            setupServicesViewModel.getServiceDaySchedule(serviceId, id, periodsListener, clinicId)
        } else {
            binding.periodsLoading.tag = null
            binding.periodsLoading.visibility = GONE
            noInternetDialog = NoInternetDialog(mContext, this)
            noConnectivityDialog.noInternetDialogFragment(mContext)

        }


    }

    private fun animateExpand(binding: ImageView, mContext: Context) {
        isCollapsed = false
        val rotate = RotateAnimation(
            360F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        binding.setImageDrawable(mContext.getDrawable(R.drawable.group_71080409))
    }

    private fun animateCollapse(binding: ImageView, mContext: Context) {
        isCollapsed = true
        val rotate = RotateAnimation(
            180F,
            360F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        binding.setImageDrawable(mContext.getDrawable(R.drawable.down_arrow))
    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    override fun onUpdate(scheduleUpdate: ScheduleModel) {
        listener.onUpdate(scheduleUpdate)
        super.onUpdate(scheduleUpdate)
    }

    override fun onTypeService() {
        listener.onTypeService()
        super.onTypeService()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (bottomservicestxt != null) {
                bottomservicestxt!!.text = intent!!.getStringExtra("serviceTxt")
            }
            serviceId = intent!!.getIntExtra("serviceId", 1).toString()
        }
    }

//    override fun getDate(date: String?) {
//        dateResult=date
//        tv_date?.text=date
//        Log.e("date",date.toString())
//    }

    interface ItemClick{
        fun OnItemClick()
    }

    private fun showDatePicker(context: Context){
        // Create a DatePickerDialog
        var date=""
        val datePickerDialog = DatePickerDialog(
            context,R.style.DatePickerTheme_Dark, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                date=formattedDate
                tv_date!!.text=date
                dateResult=date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }

    override fun onItemDeleted(scheduleId: Int) {
        Iitem.onItemDeleted(scheduleId)
    }
    interface IDeleteSchedule{
        fun onItemDeleted(scheduleId:Int)
    }
}