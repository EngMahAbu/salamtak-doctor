package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.ItemAppointmentBinding
import com.salamtak.domain.entities.response.Item
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.VIDEO_CALL
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.VOICE_CALL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class AppointmentAdapter(
    val appointList: List<Item>,
    val onAppointmentClickListener: AppointmentAdapter.OnAppointmentClickListener,
    val onCAncelAppointmentClickListener: AppointmentAdapter.OnCancelAppointmentClickListener,
    val onPatientAccountClickListener: AppointmentAdapter.OnPatientAccountClickListener,
    val onVideoClickedClickListener: AppointmentAdapter.OnVideoClickedClickListener,
    val onActionClickedClicklistner: AppointmentAdapter.OnActionClickedClicklistener,
    var context: Context
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentAdapter.ViewHolder {

        var binding =
            ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentAdapter.ViewHolder(
            binding
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("InvalidAnalyticsName", "SimpleDateFormat")
    override fun onBindViewHolder(holder: AppointmentAdapter.ViewHolder, position: Int) {

//        holder.binding.tvVideoVoiceCall.visibility = View.VISIBLE
        //show video call or voice call button
//        if (appointList[position].MedicalExaminationTypeId == VIDEO_CALL) {
////            var appointmentDate ="03/07/2023 19:15:00"
//            var value = showVideoOrVoicCall(appointList[position].AppointmentDate, true, false)
////            var value = showVideoOrVoicCall(appointmentDate, true, false)
//            if (value == true) {
//                holder.binding.tvVideoVoiceCall.visibility = View.VISIBLE
//                holder.binding.accountButton.visibility = View.GONE
//                holder.binding.tvVideoVoiceCall.text = context.getString(R.string.start_video_call)
//
//            }
//        } else if (appointList[position].MedicalExaminationTypeId == VOICE_CALL) {
//            var value = showVideoOrVoicCall(appointList[position].AppointmentDate, false, true)
//            if (value == true) {
//                holder.binding.tvVideoVoiceCall.visibility = View.VISIBLE
//                holder.binding.accountButton.visibility = View.GONE
//                holder.binding.tvVideoVoiceCall.text = context.getString(R.string.start_voice_call)
//
//            }
//        } else {
//            holder.binding.accountButton.visibility = View.VISIBLE
//        }
        holder.binding.appointment = appointList[position]
        val managesDoctors = DateUtils.managesDoctors()
        if (managesDoctors) resetStatusChip(holder)
        if (DateUtils.getAdminOwnerId().isNullOrEmpty()){
            holder.binding.rlLayoutdoctor.visibility=View.GONE
        }else{
            if (DefaultLocaleHelper.getInstance(holder.binding.doctorname.context).getCurrentLocale()=="en"){
                holder.binding.doctorname.text="doctor: ${appointList[position].DoctorName} - ${appointList[position].SpecName}"
            }else{
                holder.binding.doctorname.text="دكتور: ${appointList[position].DoctorName} - ${appointList[position].SpecName}"
            }
        }

//        holder.binding.videoButton.visibility = View.VISIBLE
//        Glide.with(holder.binding.serviceimage).load("${ UseCaseModule.IMAGE_BASE_URL}${appointList[position].MedicalExaminationTypeImage}")
//            .into(holder.binding.serviceimage)

        holder.binding.tvVideoVoiceCall.setOnClickListener {
            if (holder.binding.tvVideoVoiceCall.text.equals(context.getString(R.string.start_video_call))) {
            onAppointmentClickListener.onVideoCallClicked(appointList[position].AppointmentId)

            } else if (holder.binding.tvVideoVoiceCall.text.equals( context.getString(R.string.start_voice_call))) {
                onAppointmentClickListener.onVoiceCallClicked(
                    appointList[position].AppointmentId,
                    appointList[position]
                )
            } else {

            }

        }

        holder.binding.clinicNameTextview.text = context.getString(R.string.upcoming)

        if (appointList[position].RelationshipTypeId == null){
            holder.binding.rlLayout5.visibility= View.GONE
            holder.binding.rlLayout6.visibility= View.GONE
        }else{
            holder.binding.rlLayout5.visibility= View.VISIBLE
            holder.binding.rlLayout6.visibility= View.VISIBLE
        }
        if (appointList[position].MedicalExaminationTypeId == 3) {
//            holder.binding.videoButton.visibility = View.VISIBLE
            val dateFromApi = appointList[position].AppointmentDate
            val timeFromApi = appointList[position].AppointmentTime

            showVideoCallButton(dateFromApi, holder, timeFromApi)
        }

        if (appointList[position].MedicalExaminationTypeId == 5) {
            holder.binding.chatButton.visibility = View.VISIBLE
        }

        holder.binding.postReportButton.setOnClickListener {
            onAppointmentClickListener.onAppointmentClicked(appointList[position])
        }

        holder.binding.accountButton.setOnClickListener {
            onPatientAccountClickListener.onPatientAccountClicked(appointList[position])
        }

        holder.binding.videoButton.setOnClickListener {
            onVideoClickedClickListener.onVideoClicked(appointList[position])
        }

        holder.binding.nowShowButton.setOnClickListener {
            onActionClickedClicklistner.onActionClicked(appointList[position], position,false)
        }
        holder.binding.completedButton.setOnClickListener {
            onActionClickedClicklistner.onActionClicked(appointList[position], position,true)
        }


        holder.binding.cancleButton.setOnClickListener {
            onCAncelAppointmentClickListener.onCancelAppointmentClicked(appointList[position])
//            val logger = AppEventsLogger.newLogger(context)
       //     logger.logEvent("Patient Reservations")

            // firebase event
            firebaseAnalytics = Firebase.analytics
            val bundle = Bundle()
            firebaseAnalytics.logEvent("Patient Reservations", bundle)
        }


//        testDate(appointList[position].AppointmentDate)


        if (appointList[position].CanCancel) {
            holder.binding.cancleButton.visibility = View.VISIBLE
        } else {
            holder.binding.cancleButton.visibility = View.GONE
        }

        if (appointList[position].IsCancel) {
            holder.binding.cancleButton.visibility = View.GONE
            holder.binding.accountButton.visibility = View.VISIBLE
            holder.binding.cardclinic.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.cardcancel
                )
            )
            holder.binding.clinicNameTextview.visibility = View.VISIBLE
            holder.binding.clinicNameTextview.setTextColor(ContextCompat.getColor(context, `in`.aabhasjindal.otptextview.R.color.red))
           // holder.binding.actionButton.visibility = View.GONE
            holder.binding.nowShowButton.visibility=View.GONE
            holder.binding.completedButton.visibility=View.GONE
            holder.binding.clinicNameTextview.text = context.getString(R.string.canceled)
        } else {
//            if (appointList[position].CanCancel) {
//                val sdfFrom = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
//                if (sdfFrom.format(Calendar.getInstance().time)
//                        .equals(appointList[position].AppointmentDate)
//                ) {
//                    holder.binding.clinicNameTextview.text = context.getString(R.string.in_progressss)
//                } else {
//                    holder.binding.clinicNameTextview.text = context.getString(R.string.pending)
//                }
//            } else {

            if (appointList[position].IsCompleted) {
                holder.binding.clinicNameTextview.text = context.getString(R.string.completed)
            //    holder.binding.actionButton.visibility = View.GONE
                holder.binding.nowShowButton.visibility = View.GONE
                holder.binding.completedButton.visibility = View.GONE

                holder.binding.cancleButton.visibility = View.GONE
            } else if (!appointList[position].IsCompleted && !appointList[position].IsCancel) {
                holder.binding.cancleButton.visibility = View.VISIBLE
                if (appointList[position].IsNoShow) {
                    holder.binding.clinicNameTextview.text = context.getString(R.string.no_show)
                    holder.binding.clinicNameTextview.setTextColor(ContextCompat.getColor(context, `in`.aabhasjindal.otptextview.R.color.red))
                    holder.binding.cardclinic.setBackgroundColor(context.getColor(R.color.nowshowcolor))
                   // holder.binding.actionButton.visibility = View.GONE
                    holder.binding.nowShowButton.visibility = View.GONE
                    holder.binding.completedButton.visibility = View.GONE
                    holder.binding.cancleButton.visibility = View.GONE
                   // holder.binding.accountButton.visibility = View.GONE
                } else if (!appointList[position].IsCompleted && !appointList[position].IsCancel && !appointList[position].IsNoShow) {
                    holder.binding.cardclinic.visibility = View.VISIBLE
                    holder.binding.clinicNameTextview.text = context.getString(R.string.pending)
                   // holder.binding.accountButton.visibility = View.GONE
                    // AppointmentDate> time now
                    if (isDateGreaterThanCurrent(appointList[position].AppointmentDate)) {
                        holder.binding.cancleButton.visibility = View.VISIBLE
                   //     holder.binding.actionButton.visibility = View.GONE
                        holder.binding.completedButton.visibility = View.GONE
                           // AppointmentDate< time now
                    } else if (isDateLessThanCurrent(appointList[position].AppointmentDate)) {
                        if (appointList[position].MedicalExaminationTypeId == VIDEO_CALL) {
//            var appointmentDate ="03/07/2023 19:15:00"
                            var value = showVideoOrVoicCall(appointList[position].AppointmentDate, true, false)
//            var value = showVideoOrVoicCall(appointmentDate, true, false)
                            if (value == true) {
                                holder.binding.tvVideoVoiceCall.visibility = View.VISIBLE
                                holder.binding.accountButton.visibility = View.GONE
                                holder.binding.tvVideoVoiceCall.text = context.getString(R.string.start_video_call)
                            }else{
                                holder.binding.cancleButton.visibility = View.GONE
                               // holder.binding.actionButton.visibility = View.VISIBLE
                                holder.binding.nowShowButton.visibility = View.VISIBLE
                                holder.binding.completedButton.visibility = View.VISIBLE
                                holder.binding.accountButton.visibility = View.GONE
                                holder.binding.cardclinic.visibility = View.VISIBLE
                                holder.binding.clinicNameTextview.text = context.getString(R.string.in_progressss)
                            }
                        } else if (appointList[position].MedicalExaminationTypeId == VOICE_CALL) {
                            var value = showVideoOrVoicCall(appointList[position].AppointmentDate, false, true)
                            if (value == true) {
                                holder.binding.tvVideoVoiceCall.visibility = View.VISIBLE
                                holder.binding.accountButton.visibility = View.GONE
                                holder.binding.tvVideoVoiceCall.text = context.getString(R.string.start_voice_call)
                            }else{
                                holder.binding.cancleButton.visibility = View.GONE
                             //   holder.binding.actionButton.visibility = View.VISIBLE
                                holder.binding.nowShowButton.visibility=View.VISIBLE
                                holder.binding.completedButton.visibility=View.VISIBLE
                               holder.binding.accountButton.visibility = View.GONE
                                holder.binding.cardclinic.visibility = View.VISIBLE
                                holder.binding.clinicNameTextview.text = context.getString(R.string.in_progressss)
                            }
                        } else {
                            holder.binding.cancleButton.visibility = View.GONE
                          //  holder.binding.actionButton.visibility = View.VISIBLE
                            holder.binding.nowShowButton.visibility=View.VISIBLE
                            holder.binding.completedButton.visibility=View.VISIBLE
                            holder.binding.cardclinic.visibility = View.VISIBLE
                            holder.binding.clinicNameTextview.text = context.getString(R.string.pending)
                        }

                    }else{
                        holder.binding.cancleButton.visibility = View.GONE
                //        holder.binding.actionButton.visibility = View.VISIBLE
                        holder.binding.nowShowButton.visibility=View.VISIBLE
                        holder.binding.completedButton.visibility=View.VISIBLE
                        holder.binding.cardclinic.visibility = View.VISIBLE
                        holder.binding.clinicNameTextview.text = context.getString(R.string.pending)
                    }
                }

            }

        }

        if (managesDoctors) bindManagerActions(holder, appointList[position], position)
    }

    // Recycled cards keep the previous card's chip colours otherwise
    private fun resetStatusChip(holder: ViewHolder) {
        holder.binding.cardclinic.background.setTint(ContextCompat.getColor(context, R.color.appointment_status_bg))
        holder.binding.clinicNameTextview.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    // Polyclinic / hospital: an upcoming booking shows Confirm / Amend / Cancel until it's
    // confirmed, then Patient Profile / Account + Cancel
    private fun bindManagerActions(holder: ViewHolder, item: Item, position: Int) {
        val binding = holder.binding
        binding.confirmButton.setOnClickListener {
            onAppointmentClickListener.onConfirmClicked(item, position)
        }
        binding.amendButton.setOnClickListener {
            onAppointmentClickListener.onAmendClicked(item)
        }
        binding.cancleButton.backgroundTintList = null
        binding.cancleButton.setBackgroundResource(R.drawable.bg_appointment_cancel)
        binding.cancleButton.setTextColor(ContextCompat.getColor(context, R.color.appointment_cancel))
        binding.completedButton.backgroundTintList = null
        binding.completedButton.setBackgroundResource(R.drawable.bg_appointment_complete)
        binding.completedButton.setTextColor(ContextCompat.getColor(context, R.color.appointment_confirm))
        binding.completedButton.text = context.getString(R.string.appointment_complete)
        binding.nowShowButton.backgroundTintList = null
        binding.nowShowButton.setBackgroundResource(R.drawable.bg_appointment_no_show)
        binding.nowShowButton.setTextColor(ContextCompat.getColor(context, R.color.appointment_no_show))
        // A past booking waiting for Complete / No Show shows "View Patient" (design)
        binding.accountButton.text = context.getString(
            if (binding.completedButton.visibility == View.VISIBLE) R.string.appointment_view_patient
            else R.string.account
        )

        val upcoming = !item.IsCancel && !item.IsCompleted && !item.IsNoShow &&
                isDateGreaterThanCurrent(item.AppointmentDate)
        // TODO(confirm-api): read the confirmed flag from the appointment list response once the
        //  backend returns one (add it to Item like IsCompleted / IsNoShow) instead of the device list
        val confirmed = item.AppointmentId in DateUtils.getConfirmedAppointments()
        val awaitingConfirm = upcoming && !confirmed
        binding.confirmButton.visibility = if (awaitingConfirm) View.VISIBLE else View.GONE
        binding.amendButton.visibility = if (awaitingConfirm) View.VISIBLE else View.GONE
        if (!upcoming) return

        binding.cancleButton.visibility = View.VISIBLE
        binding.accountButton.visibility = if (confirmed) View.VISIBLE else View.GONE
        binding.clinicNameTextview.visibility = View.VISIBLE
        if (confirmed) {
            binding.clinicNameTextview.text = context.getString(R.string.appointment_confirmed)
            binding.clinicNameTextview.setTextColor(ContextCompat.getColor(context, R.color.appointment_confirm))
            binding.cardclinic.background.setTint(ContextCompat.getColor(context, R.color.appointment_confirm_bg))
        } else {
            binding.clinicNameTextview.text = context.getString(R.string.pending)
            binding.clinicNameTextview.setTextColor(ContextCompat.getColor(context, R.color.new_themeBlue_color))
            binding.cardclinic.background.setTint(ContextCompat.getColor(context, R.color.appointment_pending_bg))
        }
    }

    private fun showVideoCallButton(dateFromApi: String, holder: ViewHolder, timeFromApi: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val sdfTime = SimpleDateFormat("hh:mm")

        val apiDate = dateFromApi.split(" ").toTypedArray()[0]
//        val apiDate = "17/12/2022"

        val date = Calendar.getInstance().time
        val CurrentDate = sdf.format(date)

        val currentTime = sdfTime.format(date)


//        if (CurrentDate.equals(apiDate)) {
//            if (currentTime.equals(timeFromApi))
//                holder.binding.videoButton.visibility = View.VISIBLE
//        }

        if (CurrentDate?.compareTo(apiDate)!! > 0) {
//            Toast.makeText(context, "No! any video call schedule today.", Toast.LENGTH_SHORT).show()
        } else if (CurrentDate.compareTo(apiDate) < 0) {
//            Toast.makeText(context, "So Sorry! you are Missed video call.", Toast.LENGTH_SHORT)
//                .show()

        } else if (CurrentDate?.compareTo(apiDate) == 0) {
        //    Toast.makeText(context, "yes video call scheduled today.", Toast.LENGTH_SHORT) .show()
            if (currentTime.equals(timeFromApi))
                holder.binding.videoButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return appointList.size
    }

    interface OnAppointmentClickListener {
        fun onAppointmentClicked(newsData: Item)
        fun onVideoCallClicked(appointmentId: Long)
        fun onVoiceCallClicked(appointmentId: Long, appointmentItem: Item)
        fun onConfirmClicked(appointment: Item, position: Int) {}
        fun onAmendClicked(appointment: Item) {}
    }

    interface OnCancelAppointmentClickListener {
        fun onCancelAppointmentClicked(newsData: Item)
    }

    interface OnPatientAccountClickListener {
        fun onPatientAccountClicked(newsData: Item)
    }

    interface OnVideoClickedClickListener {
        fun onVideoClicked(newsData: Item)
    }

    interface OnChatClickedClickListener {
        fun onChatClicked(newsData: Item)
    }

    interface OnActionClickedClicklistener {
        fun onActionClicked(newsData: Item, position: Int,isAction:Boolean)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showVideoOrVoicCall(
        appointmentDate: String, videoCall: Boolean,
        voiceCall: Boolean
    ): Boolean {
        var showVideoCallButton = false
        var showVoiceCallButton = false

        if (appointmentDate.contains(" ")) {
            var dateTime = appointmentDate.split(" ")

            var appointmentDate = dateTime.get(0)

            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            var currentDate = LocalDate.now().format(formatter)
            if (dateTime != null && dateTime.size > 1 && appointmentDate.equals(currentDate)) {
                try {
                    var appointmentTime = LocalTime.parse(dateTime.get(1))
                    var currentTimeOnly =
                        LocalTime.parse(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()))
                    var beforeTime = currentTimeOnly.isBefore(appointmentTime)
                    if (currentTimeOnly.isAfter(appointmentTime.plusMinutes(10)) == false
                        && beforeTime == false
                    ) { //this should be false
                        if (videoCall == true) {
                            showVideoCallButton = true
                            showVoiceCallButton = false
                            return showVideoCallButton
                        }

                        if (voiceCall == true) {
                            showVideoCallButton = false
                            showVoiceCallButton = true
                            return showVoiceCallButton
                        }

                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
        }

        return false
    }

    fun isDateGreaterThanCurrent(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        val givenDate = dateFormat.parse(dateString)

        return givenDate?.after(currentDate) ?: false
    }

    fun isDateLessThanCurrent(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        val givenDate = dateFormat.parse(dateString)

        return givenDate?.before(currentDate) ?: false
    }
}