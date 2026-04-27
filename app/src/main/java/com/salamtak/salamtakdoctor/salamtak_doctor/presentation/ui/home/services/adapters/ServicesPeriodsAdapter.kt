package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters

import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonSyntaxException
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.databinding.ItemClinicScheduleBinding
import com.salamtak.databinding.TimepickerbottomsheetBinding
import com.salamtak.domain.entities.GeneralResponse
import com.salamtak.domain.entities.response.MedicalExaminationDurationData
import com.salamtak.domain.entities.response.ServiceDayScheduleData
import com.salamtak.domain.entities.resquest.auth.ScheduleModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.convertDateFormat
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DataBinding.convertTime
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.TimePickerDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ClinicDurationBottomSheetDialog
import com.squareup.picasso.Picasso
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class ServicesPeriodsAdapter(
    val listener: RecyclerViewOnClickListener,
    val mContext: Context,
     val deleteListener: IDeleteSchedule
    ,
    val childFragmentManager: FragmentManager,
    val setupServicesViewModel: SetupServicesViewModel,
    val serviceDurationsAdapter: ServiceDurationsAdapter,
    val viewLifeCycleOwner: LifecycleOwner,
    var serviceId: String,
    val clinicId: String,
    val loadingDialog: LoadingDialog
) : RecyclerView.Adapter<ServicesPeriodsAdapter.ViewHolder>(), DialogsListener {
    var bottomservicestxt: TextView? = null
    private var scheduleList = ArrayList<ServiceDayScheduleData>()
  //  private var servicesTypeData = ArrayList<ServicesTypesData>()
    var durationTxt = ""
    var workingFrom = ""
    var workingTo = ""
    var durationId = 0
//    var clinicfee: Int = 0
//    var clinicfollowup: Int = 0
//    var maxpatientno = 0
    var dateResult:String?=null
    var tv_date:TextView?=null
    private val TAG = ServicesPeriodsAdapter::class.java.simpleName
    private lateinit var noInternetDialog: NoInternetDialog
    private val calendar = Calendar.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_clinic_schedule, parent, false
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var data = scheduleList[holder.absoluteAdapterPosition]

        Log.d("sadkjashdkjssdkjhsa", data.toString())
//        if(data.DayName==dayName) {
//            Log.d(
//                "Inside if : ",
//                "$data.DayName and $dayName and ${scheduleList[position]} and $position "
//            )
        Log.d("ksjdklasdlad","$position")

        holder.bind(scheduleList[position], position)

//        }

//        holder.bindingdata(servicesTypeData[position],position)
        LocalBroadcastManager.getInstance(mContext)
            .registerReceiver(broadcastReceiver, IntentFilter("onServiceSelect"))
        holder.binding.ivDelete.setOnClickListener {
            deleteListener.onItemDeleted(data.SchedualId)
        }

    }

    override fun getItemCount(): Int = scheduleList.size

    inner class ViewHolder(val binding: ItemClinicScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: ServiceDayScheduleData, position: Int) {

            binding.operatingTime.text = "${convertTime( item.TimeFrom,"HH:mm","hh:mm a")} - ${convertTime( item.TimeTo,"HH:mm","hh:mm a")}"
            if (item.WorkingDate.isNullOrEmpty() || item.WorkingDate=="null" ){
                binding.operatingDate.visibility=View.GONE
            }else{
                binding.operatingDate.text = "${convertDateFormat(item.WorkingDate,"yyyy-MM-dd'T'HH:mm:ss","MMM dd, yyyy")}"
            }
            val time=calculateTimeToGetMaxNumberOfPatients(item.TimeFrom,item.TimeTo,item.TimeInterval)
            if (time==0){
                binding.maxBooking.text =""
            }else{
            binding.maxBooking.text ="${time}"}
            if (Localization.getLanguageName()=="ar"){
                binding.clinicServicesid.text = item.MedicalExaminationTypeName.toString()
            }else{
                binding.clinicServicesId.text = item.MedicalExaminationTypeName.toString()
            }

//            binding.servicesImage.setImageURI(item.MedicalExaminationTypeImage.toString())
            Picasso.get()
                .load("https://salamtechapi.azurewebsites.net" + item.MedicalExaminationTypeImage.toString())
                .into(binding.servicesImage)
//            binding.clinicServicesId.text = item.subData.get(position).Name.toString()
            binding.submitBtn.text = mContext.getString(R.string.update)
            binding.clinicFeesEt.setText(item.Fees.toString())
            val from = mContext.getString(R.string.From)
            val to = mContext.getString(R.string.to)
            binding.operatingEt.setText("$from ${item.TimeFrom} $to ${item.TimeTo}")
            binding.clinicFollowUpFeesEt.setText(item.FollowUp_Fees.toString())



            setDuration(
                serviceDurationsAdapter.scheduleList,
                binding.durationSelect,
                item.DurationMedicalExaminationId
            )

            binding.operatingEt.setOnClickListener {
                val timePickerDialog =
                    TimePickerDialog(this@ServicesPeriodsAdapter, binding.operatingEt)
                timePickerDialog.show(childFragmentManager, "")
            }

            binding.editappointment.setOnClickListener {

                val editbottomsheet = BottomSheetDialog(mContext)
                editbottomsheet.setContentView(R.layout.add_new_peroid)
                val newperoid = editbottomsheet.findViewById<TextView>(R.id.new_peroid)
                val timeto = editbottomsheet.findViewById<RelativeLayout>(R.id.relative2)
                val timefrom = editbottomsheet.findViewById<RelativeLayout>(R.id.relative1)
                val switch = editbottomsheet.findViewById<SwitchCompat>(R.id.day_switch1)
                bottomservicestxt =
                    editbottomsheet.findViewById<TextView>(R.id.services_select)
                val bottomClinicfee = editbottomsheet.findViewById<EditText>(R.id.checkfee_et)
                val bottomclinicfolow = editbottomsheet.findViewById<EditText>(R.id.checkupfee_et)
                val checkupFee = editbottomsheet.findViewById<TextInputLayout>(R.id.Checkupfee)
                val maxPatient2 =
                    editbottomsheet.findViewById<TextInputLayout>(R.id.filledTextField)
                val fee = editbottomsheet.findViewById<TextInputLayout>(R.id.Checkfee)
                val hours_to = editbottomsheet.findViewById<TextView>(R.id.hoursto)
                val maxPatient = editbottomsheet.findViewById<EditText>(R.id.clinic_name_et)
                val mcard = editbottomsheet.findViewById<ImageView>(R.id.iv_durationspinner)
                val hours_from = editbottomsheet.findViewById<TextView>(R.id.hoursfrom)
                val save_btn = editbottomsheet.findViewById<MaterialButton>(R.id.savePeriod_btn)
                val select_date=editbottomsheet.findViewById<ConstraintLayout>(R.id.layout_SelectDate)
                val tvClinicCheckfee = editbottomsheet.findViewById<TextView>(R.id.tv_Clinic_Checkfee)
                val tv_Clinic_services_select = editbottomsheet.findViewById<TextView>(R.id.tv_Clinic_services_select)
                val tv_Clinic_maxPatient = editbottomsheet.findViewById<TextView>(R.id.tv_Clinic_maxPatient)
                val tv_Clinic_time = editbottomsheet.findViewById<TextView>(R.id.tv_Clinic_time)
                tv_date=editbottomsheet.findViewById<TextView>(R.id.day_select)
                if (item.DayId==0){
                    select_date?.visibility= View.VISIBLE
                }else{
                    select_date?.visibility= View.GONE
                    tv_date?.text=" "
                }
                bottomservicestxt!!.text = item.MedicalExaminationTypeName
                newperoid?.text = mContext.getString(R.string.edit_new_period)
                editbottomsheet.show()
                switch!!.isChecked = item.Inactive
                hours_from!!.text = convertTime(item.TimeFrom,"HH:mm","hh:mm a")
                hours_to!!.text =  convertTime(item.TimeTo,"HH:mm","hh:mm a")
                workingFrom=item.TimeFrom
                workingTo=item.TimeTo
                if (!item.FollowUp_Fees.toString().isNullOrEmpty())
                    checkupFee!!.editText?.setText(item.FollowUp_Fees.toString());
                if (!item.TimeInterval.toString().isNullOrEmpty())
                    maxPatient2!!.editText?.setText(item.TimeInterval.toString());
                if (!item.Fees.toString().isNullOrEmpty())
                    fee!!.editText?.setText(item.Fees.toString())

//                maxPatient!!.setText(item.MaxNoOfPatients.toString())
//                bottomclinicfolow!!.setText(item.FollowUp_Fees)
//                bottomClinicfee!!.setText(item.Fees)
                bottomservicestxt!!.setOnClickListener {
                    listener.onTypeService()
                }
              mcard?.setOnClickListener {

                    Log.e(TAG, "bind: ${serviceDurationsAdapter.scheduleList} ")
                    val d = maxPatient?.let { it1 ->
                        ClinicDurationBottomSheetDialog(
                            serviceDurationsAdapter.scheduleList,
                            this@ServicesPeriodsAdapter, it1
                        )
                    }
                    d?.show(childFragmentManager, "")
                }

                timefrom?.setOnClickListener {
                    val binding = TimepickerbottomsheetBinding.inflate(LayoutInflater.from(mContext))

                    val timePickerDialog = BottomSheetDialog(mContext)
                    timePickerDialog.setContentView(binding.root)
                    timePickerDialog.show()
                    val timepicker = timePickerDialog.findViewById<TimePicker>(R.id.time_picker)

                    timepicker?.setOnTimeChangedListener { _, selectedHour, selectedMinute ->

                        workingFrom = "${String.format("%02d", selectedHour)}:${
                            String.format(
                                "%02d",
                                selectedMinute
                            )
                        }"
                        val sdf = SimpleDateFormat("HH:mm",Locale.US)
                        val date = sdf.parse(workingFrom)

                        workingFrom = sdf.format(date)
                        hours_from!!.text = convertTime(workingFrom,"HH:mm","hh:mm a")

                        Log.e(TAG, "setUi:To $workingFrom")

                    }
                    binding.timeDone.setOnClickListener {
                        timePickerDialog.dismiss()
                    }
                }

                timeto?.setOnClickListener {
                    val timePickerDialog = BottomSheetDialog(mContext)
                    timePickerDialog.setContentView(R.layout.timepickerbottomsheet)
                    timePickerDialog.show()
                    val timepicker = timePickerDialog.findViewById<TimePicker>(R.id.time_picker)

                    val done = timePickerDialog.findViewById<MaterialButton>(R.id.time_done)

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
                        timePickerDialog.dismiss()
                    }
                }

                save_btn!!.setOnClickListener {
                   // workingFrom = hours_from!!.text.toString()
                   // workingTo = hours_to!!.text.toString()
                    if (tv_date!!.text.toString()
                            .isNullOrEmpty() ||bottomClinicfee!!.text.toString()
                            .isNullOrEmpty()|| workingFrom.isNullOrEmpty()
                        || workingTo.isNullOrEmpty() || maxPatient!!.text.toString()
                            .isNullOrEmpty()
                    ) {

//                        tvClinicCheckfee?.text = it.context.getString(R.string.required)
//                        tvClinicCheckfee?.visibility = View.VISIBLE
//                        tv_Clinic_services_select?.text = it.context.getString(R.string.required)
//                        tv_Clinic_services_select?.visibility = View.VISIBLE
//                        tv_Clinic_maxPatient?.text = it.context.getString(R.string.required)
//                        tv_Clinic_maxPatient?.visibility = View.VISIBLE
//                        tv_Clinic_time?.text = it.context.getString(R.string.required)
//                        tv_Clinic_time?.visibility = View.VISIBLE

                        Toast.makeText(
                            mContext,
                            "You must fill the required fields",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (tv_date!!.text.toString()
                            .isNotEmpty() && bottomClinicfee.text.toString()
                            .isNotEmpty() && hours_from!!.text.toString().isNotEmpty()
                        && hours_to!!.text.toString().isNotEmpty() && maxPatient!!.text.trim().toString()
                            .isNotEmpty()
                    ) {
                        val clinicfee = bottomClinicfee!!.text.toString().toInt()
                        var clinicfollowup=0
                        if(!bottomclinicfolow!!.text.toString().isNullOrEmpty()) {
                            clinicfollowup = bottomclinicfolow!!.text.toString().toInt()
                        }
                        val maxpatient = maxPatient!!.text.trim().toString()
                        serviceId= when(bottomservicestxt!!.text.toString()){
                            "Call","مكالمة صوتية"-> 4.toString()
                            "Home visit","كشف منزلى"-> 2.toString()
                            "Video Call","مالمة فيديو"-> 3.toString()
                            else -> 1.toString()
                        }
                        listener.run {
                            onUpdate(
                                ScheduleModel(
                                    item.DayId, workingFrom, workingTo,
                                    clinicfee.toDouble(),
                                    clinicfollowup.toDouble(),
                                    item.SchedualId,
                                   durationId, serviceId.toInt(),
                                    //                            durationId
                                    //                            item.DurationMedicalExaminationId
                                    clinicId.toInt(), item.Inactive, item.OverlapApproved,dateResult
                                )
                            )
                        }
//                        editService(
//                            ScheduleModel(
//                                item.DayId, workingFrom, workingTo,
//                                binding.clinicFeesEt.text.toString().toDouble(),
//                                item.SchedualId,
////                            durationId
////                            item.DurationMedicalExaminationId
//                                clinicId.toInt(), item.Inactive, item.OverlapApproved
//                            )
//                        )
                        editbottomsheet.dismiss()


                    }
                }
                tv_date?.setOnClickListener {
                    showDatePicker(it.context)
                }

            }


            binding.durationSelect.setOnClickListener {

                Log.e(TAG, "bind: ${serviceDurationsAdapter.scheduleList} ")
                val d = ClinicDurationBottomSheetDialog(
                    serviceDurationsAdapter.scheduleList,
                    this@ServicesPeriodsAdapter, binding.durationSelect
                )
                d.show(childFragmentManager, "")
            }

            binding.periodSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    item.Inactive = true
                    binding.periodSwitch.isChecked = false
                } else {
                    item.Inactive = false
                    binding.periodSwitch.isChecked = true
                }
            }

            binding.submitBtn.setOnClickListener {

                if (workingFrom.isNullOrEmpty()) {
                    workingFrom = item.TimeFrom
                    workingTo = item.TimeTo
                }
                if (durationId.equals(0)) {
                    durationId = item.DurationMedicalExaminationId
                }

//                if (binding.operatingEt.text.toString().isNotEmpty() &&
//                    binding.durationSelect.text.toString().isNotEmpty() && durationId != 0
//                    && binding.clinicFeesEt.text.toString().isNotEmpty()
                if (binding.clinicFeesEt.text.toString().isNotEmpty()
                    && binding.durationSelect.text.toString()
                        .isNotEmpty() && binding.clinicFeesEt.text.toString().length >= 2
                ) {

//                    editService(
//                        ScheduleModel(
//                            item.DayId, workingFrom, workingTo,
//                            , item.TimeFrom, item.TimeTo,
//                            ,workingFrom,workingTo,
//                            binding.clinicFeesEt.text.toString().toDouble(),
//                            item.SchedualId,
//                            durationId
//                            item.DurationMedicalExaminationId
//                            clinicId.toInt(), item.Inactive, item.OverlapApproved
//                        )
//                    )

                } else {
//                    binding.errorMsg.visibility = View.VISIBLE

                    if (binding.operatingEt.text.toString().isNullOrEmpty()) {
                        binding.operatingEt.error =
                            mContext.getString(R.string.operating_time_required)
                    }

                    if (binding.durationSelect.text.toString().isNullOrEmpty())
                        binding.durationSelect.error =
                            mContext.getString(R.string.duration_time_required)

                    if (binding.clinicFeesEt.text.toString().isNullOrEmpty()
                        || binding.clinicFeesEt.text.toString().length <= 2
                    )
                        binding.clinicFeesEt.error =
                            mContext.getString(R.string.clinic_fees_error)

                }

            }


        }

//        fun bindingdata(servicesTypesData: ServicesTypesData, position: Int) {
//            if (servicesTypesData.Name != "" && servicesTypesData.Name != null) {
//                binding.clinicServicesId.text = servicesTypesData.Name.toString()
//            }
//
//        }

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

    fun submitData(list: ArrayList<ServiceDayScheduleData>) {
        Log.e(TAG, "submitData: $list ${list.size}")
        this.scheduleList = list
        notifyDataSetChanged()
    }

    private fun setDuration(
        list: ArrayList<MedicalExaminationDurationData>,
        text: TextView,
        id: Int
    ) {

        if (id != 0) {

            for (i in list.indices) {

                if (id == list[i].Id)
                    text.text = list[i].Duration.toString()

            }

        }


    }

    private fun editService(scheduleModel: ScheduleModel) {
        //TODO 29/11/22
        if (isOnline(mContext)) {
            loadingDialog.show()
            setupServicesViewModel.editClinicSchedule(scheduleModel)
        } else {
            noInternetDialog = NoInternetDialog(mContext, this)
            noConnectivityDialog.noInternetDialogFragment(mContext)

        }

        setupServicesViewModel.editScheduleResponse.observe(viewLifeCycleOwner) {

            if (it.Success) {
                loadingDialog.dismiss()
                toast(mContext, mContext.getString(R.string.updated_successfully))
                Log.e(TAG, "editService: ${it.Message} ")
            } else {
                loadingDialog.dismiss()
                Log.e(TAG, "editService: ${it.Message} ")
            }

        }

        setupServicesViewModel.failure.observe(viewLifeCycleOwner) {

            loadingDialog.dismiss()
            when (it) {
                is GeneralResponse.Failure.JSonServerError -> {
                    when (it.errorCode) {
                        401 -> {
                            showError(
                                mContext.getString(R.string.error_unauthorized),
                                childFragmentManager
                            )
                        }

                        400 -> {

                            showError(
                                it.errorMessage!!, childFragmentManager
                            )

                        }

                        else -> {
                            Log.e("TAG editService", "onCreate: ${it.errorMessage}  ")
                        }
                    }
                }

                is GeneralResponse.Failure.Error -> {
                    when (it.ex) {
                        is HttpException ->
                            Log.e("Tag", "${it.ex.localizedMessage} ")

                        is JsonSyntaxException -> {
                            Log.e("TAGJson editService", "onCreate: ${it.ex.printStackTrace()}")
                        }

                        else -> {
                            Log.e(
                                "TAG editService",
                                "onCreate: ${it.ex.cause?.message}  == ${it.ex.message}"
                            )
                        }
                    }
                }
            }

        }

    }

    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (bottomservicestxt != null) {
                bottomservicestxt!!.text = intent!!.getStringExtra("serviceTxt")
            }
            serviceId = intent!!.getIntExtra("serviceId", 1).toString()
        }
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
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateTimeToGetMaxNumberOfPatients(timeFrom:String, timeTo:String,intevalTime:Int): Int {

           val formatter = DateTimeFormatter.ofPattern("HH:mm")
           // Define the two times as strings
           val timeStartString = timeFrom
           val timeEndString = timeTo

           // Parse the time strings into LocalTime objects
           val timeStart = LocalTime.parse(timeStartString, formatter)
           val timeEnd = LocalTime.parse(timeEndString, formatter)

           // Calculate the duration between the two times
           val duration = Duration.between(timeStart, timeEnd)

           // Extract hours and minutes
           val hours = duration.toHours()
           val minutes = duration.toMinutes()
        return if (intevalTime==0  || intevalTime==null) {
            intevalTime
        }else {
            (minutes / intevalTime).toInt()
        }
    }

    interface IDeleteSchedule{
        fun onItemDeleted(scheduleId:Int)
    }
}