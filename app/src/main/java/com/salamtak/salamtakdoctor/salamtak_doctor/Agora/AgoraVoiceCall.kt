package com.salamtak.salamtakdoctor.salamtak_doctor.Agora

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.ActivityAgoraVideoBinding
import com.salamtak.databinding.ActivityAgoraVoiceBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.AGORA_APPOINTMENT_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.AGORA_CHANNEL
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.AGORA_TOKEN
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.PATIENT_IMAGE
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.PATIENT_NAME
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

class AgoraVoiceCall : AppCompatActivity(){

    private val PERMISSION_REQ_ID = 22

    // Fill the App ID of your project generated on Agora Console.
    private var appId = ""
    private var channelName = ""
    private var token = ""
    private val uid = 0
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    var appointmentId = -1
    private lateinit var binding: ActivityAgoraVoiceBinding
    private lateinit var viewModel: AppointmentViewModel
    var otherUserjoined = false
    var patientName =""
    var patientImage =""

    private val REQUESTED_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgoraVoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(
                this,
                REQUESTED_PERMISSIONS,
                PERMISSION_REQ_ID
            )
        }

        appId = getString(R.string.agora_app_id)

        if (intent.hasExtra(AGORA_TOKEN)) {
            token = intent.getStringExtra(AGORA_TOKEN)!!
        }

        if (intent.hasExtra(AGORA_CHANNEL)) {
            channelName = intent.getStringExtra(AGORA_CHANNEL)!!

        }

        if (intent.hasExtra(AGORA_APPOINTMENT_ID)) {
            appointmentId = intent.getIntExtra(AGORA_CHANNEL, -1)
        }

        if (intent.hasExtra(PATIENT_NAME)) {
            patientName = intent.getStringExtra(PATIENT_NAME)!!
            binding.tvPatientName.setText(patientName)
        }

        if (intent.hasExtra(PATIENT_IMAGE)) {
            patientImage = intent.getStringExtra(PATIENT_IMAGE)!!

            Glide.with(this@AgoraVoiceCall).load(patientImage).into(binding.ivPatientImage)
        }

        setupVoiceSDKEngine()
        initButton()
        initObservers()


        // Set up access to the UI elements

        // Set up access to the UI elements

    }

    @SuppressLint("SuspiciousIndentation")
    private fun initObservers() {


        viewModel.changeAppointmentStatusResponse.observe(this, Observer {
            if (it.Success) {
                viewModel.AppointmentModel.observe(this, Observer { it1 ->
                    if(it.Data != null){
                        finish()
                    }
                })
            }
        })

    }


    private fun initButton() {
//        binding.joinButton.setOnClickListener{
//            joinChannel()
//        }

        binding.leaveButton.setOnClickListener{
            joinLeaveChannel()
        }

        binding.NoShow.setOnClickListener{
            noShowClicked()
        }
    }

    private fun checkSelfPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                REQUESTED_PERMISSIONS[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else true
    }

    private fun setupVoiceSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            joinChannel()
        } catch (e: Exception) {
            throw RuntimeException("Check the error.")
        }
    }

    fun checkForOtherUser() {
        Handler(Looper.getMainLooper()).postDelayed({
            if(otherUserjoined == false){
                //Show no Show button and Call the ChangeAppointmentStatus with status 2
                binding.NoShow.visibility = View.VISIBLE
            }
        }, 5 * 60 * 1000)
    }

    fun endButtonClicked(){

        viewModel.changeAppointmentStatus(appointmentId , 1)
    }

    fun noShowClicked(){
        viewModel.changeAppointmentStatus(appointmentId , 2)
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            otherUserjoined= true
            runOnUiThread { binding.infoText?.setText("Remote user joined: $uid") }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            // Successfully joined a channel
            isJoined = true
            checkForOtherUser()
            showMessage("Joined Channel $channel")
            runOnUiThread { binding.infoText?.setText("Waiting for a remote user to join") }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline $uid $reason")
            if (isJoined) runOnUiThread { binding.infoText?.setText("Waiting for a remote user to join") }
        }

        override fun onLeaveChannel(stats: RtcStats) {
            // Listen for the local user leaving the channel
            runOnUiThread { binding.infoText?.setText("Press the button to join a channel") }
            endButtonClicked()
            isJoined = false
        }
    }

    private fun joinChannel() {
        val options = ChannelMediaOptions()
        options.autoSubscribeAudio = true
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine?.muteLocalAudioStream(false)
        agoraEngine?.joinChannel(token, channelName, uid, options)
    }

    fun joinLeaveChannel() {
        if (isJoined) {
            agoraEngine?.leaveChannel()

//            binding.joinLeaveButton?.setText("Join")
        } else {
            finish()
           // joinChannel()
//            binding.joinLeaveButton?.setText("Leave")
        }
    }


    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.leaveChannel()

        // Destroy the engine in a sub-thread to avoid congestion
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }


}