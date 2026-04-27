package com.salamtak.salamtakdoctor.salamtak_doctor.Agora

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.salamtak.R
import com.salamtak.databinding.VideoCallfragmentBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentViewModel
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import java.text.SimpleDateFormat
import java.util.*


class VideoCallFragment : Fragment() {

    private lateinit var binding: VideoCallfragmentBinding
    private val args:VideoCallFragmentArgs by navArgs()
    private lateinit var viewModel: AppointmentViewModel
    var startTime: Long = 0
    var timeInMilliseconds:kotlin.Long = 0
    var customHandler: android.os.Handler = android.os.Handler()
    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    private var token =""
    private var channelName = ""
    private val appId = "571628199b5a4fe681496f14f9951a67"
    private val uid = 0
    private var isJoined = false
    var otherUserjoined = false
    private var agoraEngine: RtcEngine? = null

    //SurfaceView to render local video in a Container.
    private var localSurfaceView: SurfaceView? = null

    //SurfaceView to render Remote video in a Container.
    private var remoteSurfaceView: SurfaceView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = VideoCallfragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (args.deepLink?.token.isNullOrEmpty() || args.deepLink?.date.isNullOrEmpty() || args.deepLink?.DoctorImage.isNullOrEmpty()
//            || args.deepLink?.link.isNullOrEmpty()) {
//            Toast.makeText(requireContext(), "null", Toast.LENGTH_SHORT).show()
//        }
       // binding.tvName.text = args.doctorname
    //    binding.tvVname.text = args.doctorname
        token= args.token.toString()
        channelName= args.chennelname.toString()
        start()
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(requireActivity(), REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        initButton()
        setupVideoSDKEngine()
        joinCall()
        initObservers()
//        if (args.link == "video") {
//            binding.layoutVideocalling.visibility = View.GONE
//            binding.layoutVoiceCallng.visibility = View.VISIBLE
//            setupVoiceSDKEngine()
//        } else {
//            binding.layoutVideocalling.visibility = View.VISIBLE
//            binding.layoutVoiceCallng.visibility = View.GONE
//            setupVideoSDKEngine()
//            joinCall()
//        }

//        binding.ivProfile.load("https://salamtechapi.azurewebsites.net/${args.doctorimage}") {
//            crossfade(true)
//            crossfade(1000)
//            placeholder(R.drawable.ic_profile)
//            transformations(CircleCropTransformation())
//        }
//
//        binding.ivBack.load("https://salamtechapi.azurewebsites.net/${args.doctorimage}") {
//            crossfade(true)
//            crossfade(1000)
//            placeholder(R.drawable.ic_profile)
//            transformations(CircleCropTransformation())
//        }


    }


    private fun initButton() {

        binding.nowShowButton.setOnClickListener {
            noShowClicked()
            leaveChannel()
            findNavController().navigate(R.id.appointmentsFragment)
        }
        binding.layoutAll.setOnClickListener {
            return@setOnClickListener
        }

        binding.ivResend.setOnClickListener {
            agoraMeeting()
        }

        binding.ivEndCall.setOnClickListener {
            leaveChannel()
            endButtonClicked()
        }

        binding.ivSpeaker.setOnClickListener {
            onSwitchSpeakerClicked()
        }

        binding.ivMic.setOnClickListener {
            onLocalAudioMuteClicked()
        }
        binding.ivVoiceMic.setOnClickListener {
            onLocalAudioMuteVoiceClicked()
        }
        binding.ivSwitchCameraDailogg.setOnClickListener {
            onSwitchCameraClicked()
        }

        binding.ivVoiceSpeaker.setOnClickListener {
            onSwitchSpeakerVoiceClicked()
        }
        binding.ivVoiceEndCall.setOnClickListener {
            joinLeaveChannel()

        }

    }

    fun leaveChannel() {
        if (!isJoined) {
            showMessage("Join a channel first")
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
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
            agoraEngine!!.joinChannel(args.token, args.chennelname, uid, options)
        } else {
            Toast.makeText(requireContext(), "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
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
       agoraEngine!!.joinChannel(args.token, args.chennelname, uid, options)
    }

    fun joinLeaveChannel() {
        if (isJoined) {
            agoraEngine!!.leaveChannel()
            binding.infoText.text = "Join"
            binding.ivVoiceEndCall.setBackgroundResource(R.drawable.background_iconcallovalgreen)
     //     findNavController().navigate(R.id.homeFragment)
        } else {
            joinChannel()
            binding.infoText.text = "Leave"
        //    binding.ivVoiceEndCall.setBackgroundResource(R.drawable.background_iconcalloval)
        }
    }

    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = requireContext()
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
            requireContext(),
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }

    fun showMessage(message: String?) {
        requireActivity().runOnUiThread {
            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote host joining the channel to get the uid of the host.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")
            requireActivity().runOnUiThread() { setupRemoteVideo(uid) }
            // Set the remote video view
        // commit
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            checkForOtherUser()
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote user offline $uid $reason")
            checkForOtherUser()
            requireActivity().runOnUiThread() { remoteSurfaceView!!.visibility = View.GONE }
        }
    }
    fun checkForOtherUser() {
        Handler(Looper.getMainLooper()).postDelayed({
            if(!otherUserjoined){
                //Show no Show button and Call the ChangeAppointmentStatus with status 2
                binding.nowShowButton.visibility = View.VISIBLE
                stop()
            }
        }, 5 * 60 * 1000)
    }
    fun endCallOtherUser() {
        Handler(Looper.getMainLooper()).postDelayed({
            if(!otherUserjoined){
                //Show no Show button and Call the ChangeAppointmentStatus with status 2
                binding.ivEndCall.visibility = View.VISIBLE
            }
        }, 1 * 60 * 1000)
    }
    private fun setupRemoteVideo(uid: Int) {
        stop()
        binding.customDialog.visibility=View.GONE
        val container = binding.remoteVideoViewContainer
        binding.layoutVideocalling.visibility=View.VISIBLE
        remoteSurfaceView = SurfaceView(requireContext())
        remoteSurfaceView!!.setZOrderMediaOverlay(false)
        container.addView(remoteSurfaceView)
        setupLocalVideo2()
        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        // Display RemoteSurfaceView.
        remoteSurfaceView!!.setVisibility(View.VISIBLE)
        endCallOtherUser()
    }

    private fun setupLocalVideo() {
        val container = binding.localVideoViewContainerdialog
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = SurfaceView(requireContext())
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
    private fun setupLocalVideo2(){
        val container = binding.localVideoViewContainer
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = SurfaceView(requireContext())
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

    fun onSwitchCameraClicked() {

        agoraEngine?.switchCamera()
    }

    private fun onSwitchSpeakerClicked() {
        if (binding.ivSpeaker.isSelected) {
            binding.ivSpeaker.isSelected = false
            binding.ivSpeaker.setImageResource(R.drawable.ic_speaker)
        } else {
            binding.ivSpeaker.isSelected = true
    //        binding.ivSpeaker.setImageResource(R.drawable.ic_speaker_up)
        }
        agoraEngine?.setEnableSpeakerphone(binding.ivSpeaker.isSelected)
    }

    private fun onSwitchSpeakerVoiceClicked() {
        if (binding.ivVoiceSpeaker.isSelected) {
            binding.ivVoiceSpeaker.isSelected = false
            binding.ivVoiceSpeaker.setImageResource(R.drawable.ic_speaker)
        } else {
            binding.ivVoiceSpeaker.isSelected = true
  //          binding.ivVoiceSpeaker.setImageResource(R.drawable.ic_speaker_up)
        }
        agoraEngine?.setEnableSpeakerphone(binding.ivVoiceSpeaker.isSelected)
    }

    private fun onLocalAudioMuteClicked() {


        if (binding.ivMic.isSelected) {
            binding.ivMic.isSelected = false
            binding.ivMic.clearColorFilter()
            binding.ivMic.setImageResource(R.drawable.ic_mic)
        } else {
            binding.ivMic.isSelected = true
            binding.ivMic.setImageResource(R.drawable.ic_mic_off)
        }
        agoraEngine?.muteLocalAudioStream(binding.ivMic.isSelected)

    }

    private fun onLocalAudioMuteVoiceClicked() {


        if (binding.ivVoiceMic.isSelected) {
            binding.ivVoiceMic.isSelected = false
            binding.ivVoiceMic.clearColorFilter()
            binding.ivVoiceMic.setImageResource(R.drawable.ic_mic)
        } else {
            binding.ivVoiceMic.isSelected = true
            binding.ivVoiceMic.setImageResource(R.drawable.ic_mic_off)
        }
        agoraEngine?.muteLocalAudioStream(binding.ivVoiceMic.isSelected)

    }


    private fun setupVoiceSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = requireContext()
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
        } catch (e: java.lang.Exception) {
            throw RuntimeException("Check the error.")
        }
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

    fun getDateFromMillis(d: Long): String? {
        val df = SimpleDateFormat("HH:mm:ss")
        df.setTimeZone(TimeZone.getTimeZone("GMT"))
        return df.format(d)
    }

    fun start() {
        startTime = SystemClock.uptimeMillis()
        customHandler.postDelayed(updateTimerThread, 0)
    }

    fun stop() {
        customHandler.removeCallbacks(updateTimerThread)
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            binding.tvTimer.setText(getDateFromMillis(timeInMilliseconds))
            customHandler.postDelayed(this, 1000)
        }
    }

    private fun initObservers() {


        viewModel.changeAppointmentStatusResponse.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.Success) {
                viewModel.AppointmentModel.observe(requireActivity(), androidx.lifecycle.Observer { it1 ->
                    if(it.Data != null){

                    }
                })
            }
        })

    }

    fun endButtonClicked(){
        viewModel.changeAppointmentStatus(args.appointementId , 1)
        findNavController().navigate(R.id.appointmentsFragment)
       // Toast.makeText(requireContext(),"ok",Toast.LENGTH_SHORT).show()
    }

    fun noShowClicked(){
        viewModel.changeAppointmentStatus(args.appointementId , 2)
        findNavController().navigate(R.id.appointmentsFragment)
    }


    private fun agoraMeeting(){
        viewModel.AgoraMeetingModel.observe(viewLifecycleOwner) {
            if (it.MessageCode == 200) {
                   token=it.Data.Token
                channelName=it.Data.channelName
                        }
        //    Toast.makeText(requireContext(), "${it.Message}", Toast.LENGTH_SHORT).show()
        }

        viewModel.getAgoraMeeting(appointmentId = args.appointementId)
    }


}