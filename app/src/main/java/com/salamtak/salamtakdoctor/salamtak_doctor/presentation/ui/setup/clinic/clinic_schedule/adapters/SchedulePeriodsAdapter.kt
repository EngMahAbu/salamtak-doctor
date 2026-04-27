package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonSyntaxException
import com.salamtak.R
import com.salamtak.databinding.ItemClinicScheduleBinding
import com.salamtak.domain.entities.GeneralResponse
import com.salamtak.domain.entities.response.ClinicDayScheduleData
import com.salamtak.domain.entities.response.MedicalExaminationDurationData
import com.salamtak.domain.entities.resquest.auth.ScheduleModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.EditDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.TimePickerDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.ClinicDurationBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showError
import retrofit2.HttpException


class SchedulePeriodsAdapter(
    val listener: RecyclerViewOnClickListener,
    val mContext: Context
//                            ,val dialogsListener: DialogsListener
    ,
    val childFragmentManager: FragmentManager,
    val setupClinicViewModel: SetupClinicViewModel,
    val clinicDurationsAdapter: ClinicDurationsAdapter,
    val viewLifeCycleOwner: LifecycleOwner,
    val clinicId: String,
    val loadingDialog: LoadingDialog
) : RecyclerView.Adapter<SchedulePeriodsAdapter.ViewHolder>(), DialogsListener {

    private var scheduleList = ArrayList<ClinicDayScheduleData>()
    var durationTxt = ""
    var workingFrom = ""
    var workingTo = ""
    var durationId = 0
    private val TAG = SchedulePeriodsAdapter::class.java.simpleName
    private lateinit var noInternetDialog: NoInternetDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_clinic_schedule, parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleList[position], position)
    }

    override fun getItemCount(): Int = scheduleList.size

    inner class ViewHolder(val binding: ItemClinicScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClinicDayScheduleData, position: Int) {

            binding.submitBtn.text = mContext.getString(R.string.edit)


            binding.clinicFeesEt.setText(item.Fees.toString())
            val from = mContext.getString(R.string.From)
            val to = mContext.getString(R.string.to)
            binding.operatingEt.setText("$from ${item.TimeFrom} $to ${item.TimeTo}")

            setDuration(
                clinicDurationsAdapter.scheduleList,
                binding.durationSelect,
                item.DurationMedicalExaminationId
            )

            binding.editappointment.setOnClickListener{
                Log.e(TAG, "bind: clicked")
            }

            binding.operatingEt.setOnClickListener {

                val timePickerDialog =
                    TimePickerDialog(this@SchedulePeriodsAdapter, binding.operatingEt)
                timePickerDialog.show(childFragmentManager, "")
            }

            binding.durationSelect.setOnClickListener {

                Log.e(TAG, "bind: ${clinicDurationsAdapter.scheduleList} ")
                val d = ClinicDurationBottomSheetDialog(
                    clinicDurationsAdapter.scheduleList,
                    this@SchedulePeriodsAdapter, binding.durationSelect
                )
                d.show(childFragmentManager, "")
            }

            binding.submitBtn.setOnClickListener {
                if (binding.clinicFeesEt.text.toString().isNotEmpty()
                    && binding.durationSelect.text.toString().isNotEmpty()

//                    binding.operatingEt.text.toString().isNotEmpty()
//                    && binding.durationSelect.text.toString().isNotEmpty() && durationId != 0
//                    && binding.clinicFeesEt.text.toString().isNotEmpty()
                ) {
                    editClinic(
                        ScheduleModel(
                            item.DayId,
//                            workingFrom,
//                            workingTo,
                            item.TimeFrom, item.TimeTo,
                            binding.clinicFeesEt.text.toString().toDouble(),
                            1.00,
                            item.SchedualId,
                            0,
                            1,

//                            item.DurationMedicalExaminationId,
                            clinicId.toInt(),item.Inactive,item.OverlapApproved,null
                        )
                    )
                    val sucess = EditDialog()
                    sucess.show(childFragmentManager, "0")
                } else {
                    binding.errorMsg.visibility = VISIBLE
                }

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

    fun submitData(list: ArrayList<ClinicDayScheduleData>) {
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

    @SuppressLint("SuspiciousIndentation")
    private fun editClinic(scheduleModel: ScheduleModel) {

        // TODO 29/11/22
        if(isOnline(mContext)){
            loadingDialog.show()
            setupClinicViewModel.editClinicSchedule(scheduleModel)
        }
        else{
            noInternetDialog = NoInternetDialog(mContext,this)
                       noConnectivityDialog.noInternetDialogFragment(mContext)

        }

        setupClinicViewModel.editScheduleResponse.observe(viewLifeCycleOwner) {

            if (it.Success) {
                loadingDialog.dismiss()
                Log.e(TAG, "editClinic: ${it.Message} ")
            } else {
                loadingDialog.dismiss()
                Log.e(TAG, "editClinic: ${it.Message} ")
            }

        }

        setupClinicViewModel.failure.observe(viewLifeCycleOwner) {

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
                            Log.e("TAG BaseFragmentss", "onCreate: ${it.errorMessage}  ")
                        }
                    }
                }
                is GeneralResponse.Failure.Error -> {
                    when (it.ex) {
                        is HttpException ->
                            Log.e("Tag", "${it.ex.localizedMessage} ")

                        is JsonSyntaxException -> {
                            Log.e("TAGJson BaseFragment123", "onCreate: ${it.ex.printStackTrace()}")
                        }
                        else -> {
                            Log.e(
                                "TAG BaseFragment",
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


}