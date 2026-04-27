package com.salamtak.salamtakdoctor.salamtak_doctor.Agora

import android.R
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.salamtak.databinding.ActivityAgoraVideoBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.AGORA_APPOINTMENT_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.AGORA_CHANNEL
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.AGORA_TOKEN
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas


class AgoraVideoCall : AppCompatActivity() {

    private lateinit var binding: ActivityAgoraVideoBinding
    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )
    var appId = ""
    var channelName = ""
    var appointmentId = -1
    var token = ""
    var otherUserjoined = false
    private lateinit var viewModel: AppointmentViewModel

//    private val appId = "8c6a0bb92eb943b28327bb286bd0b03f"
//    private val token = "0068c6a0bb92eb943b28327bb286bd0b03fIAB7n+HgvNRz0SfsUxxO/5HWkilfwzESOKnJaGRpNMnbHkFElD0AAAAAEAB9FRAAZat5ZAEAAQCdZXhk"
//    private val channelName = "4a0ee45a-ca5c-458c-8bff-07209eb957d3"
//    private val app_certificate="11c30c645c3843ba8cfe74dc56fbfe16"

//    private var token:String?=null
//
    // An integer that identifies the local user.
    private val uid = 0
    private var isJoined = false

    private var agoraEngine: RtcEngine? = null

    //SurfaceView to render local video in a Container.
    private var localSurfaceView: SurfaceView? = null

    //SurfaceView to render Remote video in a Container.
    private var remoteSurfaceView: SurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgoraVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)

        appId = getString(com.salamtak.R.string.agora_app_id)

        if (intent.hasExtra(AGORA_TOKEN)) {
            token = intent.getStringExtra(AGORA_TOKEN)!!
        }

        if (intent.hasExtra(AGORA_CHANNEL)) {
            channelName = intent.getStringExtra(AGORA_CHANNEL)!!
        }

        if (intent.hasExtra(AGORA_APPOINTMENT_ID)) {
            appointmentId = intent.getIntExtra(AGORA_CHANNEL,-1)
        }


//        val tokenBuilder=RtcTokenBuilder2()
//        val timstamp=(System.currentTimeMillis()/1000+60).toInt()
//        token=tokenBuilder.buildTokenWithUid(appId,app_certificate,channelName,uid,RtcTokenBuilder2.Role.ROLE_PUBLISHER,timstamp,timstamp)
//        Log.e("token",token.toString())
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVideoSDKEngine()
        initButton()
        initObservers()

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

    fun endButtonClicked(){
        viewModel.changeAppointmentStatus(appointmentId , 1)
    }

    fun noShowClicked(){
        viewModel.changeAppointmentStatus(appointmentId , 2)
    }


    private fun initButton(){

        binding.JoinButton.setOnClickListener {
            joinCall()
        }

        binding.ivEndCall.setOnClickListener {
            leaveChannel()
        }

        binding.ivCamera.setOnClickListener {
            onswitchCameraclicked()
        }

        binding.ivMicOff.setOnClickListener {
            onLocalAudioMuteClicked()
        }
    }

    fun leaveChannel() {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
            finish()
        }
    }



    private fun joinCall() {
        if (checkSelfPermission()) {
            val options = ChannelMediaOptions()

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            // Display LocalSurfaceView.
            setupLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            // Start local preview.
            agoraEngine!!.startPreview()
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine!!.joinChannel(token, channelName, uid, options)
        } else {
            Toast.makeText(applicationContext, "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }


    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
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
    fun checkForOtherUser() {
//        val handler = Handler(Looper.myLooper()!!)
//        val delayMillis: Long = 5 * 60 * 1000 // 5 minutes in milliseconds
//
//        handler.postDelayed({
//            if(otherUserjoined == false){
//                //Show no Show button and Call the ChangeAppointmentStatus with status 2
//                binding.NoShow.visibility = View.VISIBLE
//            }
//
//        }, delayMillis)
        Handler(Looper.getMainLooper()).postDelayed({
            if(otherUserjoined){
                //Show no Show button and Call the ChangeAppointmentStatus with status 2
                binding.NoShow.visibility = View.VISIBLE
            }
        }, 3 * 60 * 1000)
    }

    fun showEndButton() {
//        val handler = Handler(Looper.myLooper()!!)
//        val delayMillis: Long = 2 * 60 * 1000 // 2 minutes in milliseconds


//        handler.postDelayed({
//            binding.LeaveButton.text = getString(R.string.end)
//
//        }, delayMillis)
    }



    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote host joining the channel to get the uid of the host.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")
            showEndButton()
            // Set the remote video view
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            checkForOtherUser()
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote user offline $uid $reason")
            runOnUiThread { remoteSurfaceView!!.visibility = View.GONE }
        }

        override fun onLeaveChannel(stats: RtcStats?) {
           endButtonClicked()
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        val container =  binding.remoteVideoViewContainer
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(false)
        container.addView(remoteSurfaceView)
        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        // Display RemoteSurfaceView.
        remoteSurfaceView!!.setVisibility(View.VISIBLE)
    }

    private fun setupLocalVideo() {
        val container = binding.localVideoViewContainer
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = SurfaceView(baseContext)
        container.addView(localSurfaceView)
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    fun onswitchCameraclicked()
    {
        agoraEngine?.stopPreview()
    }

    private fun onLocalAudioMuteClicked(){


        if(binding.ivMicOff.isSelected){
            binding.ivMicOff.isSelected=false
            binding.ivMicOff.clearColorFilter()
        }else{
            binding.ivMicOff.isSelected=true
            binding.ivMicOff.setColorFilter(resources.getColor(R.color.holo_purple), PorterDuff.Mode.MULTIPLY)

        }
        agoraEngine?.muteLocalAudioStream(binding.ivMicOff.isSelected)

    }


    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        // Destroy the engine in a sub-thread to avoid congestion
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }



}