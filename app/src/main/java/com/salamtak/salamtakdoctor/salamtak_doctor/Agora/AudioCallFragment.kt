package com.salamtak.salamtakdoctor.salamtak_doctor.Agora

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import coil.load
import coil.transform.CircleCropTransformation
import com.salamtak.R
import com.salamtak.databinding.AudioCallfragmentBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.AppointmentViewModel
import io.agora.rtc2.*


class AudioCallFragment : Fragment() {

    private lateinit var binding: AudioCallfragmentBinding
    private val args:AudioCallFragmentArgs by navArgs()
    private lateinit var viewModel: AppointmentViewModel
    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    //private val token ="args.doctorname"
   //private val channelName = "args.channelname"
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
        binding = AudioCallfragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(requireActivity(), REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        initButton()
        setupVoiceSDKEngine()
        initObservers()
        binding.tvVname.text=args.name
        binding.ivProfile.load("https://salamtechapi.azurewebsites.net/${args.image}") {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }
    }

    private fun initButton() {

        binding.layoutAll.setOnClickListener {
            return@setOnClickListener
        }

        binding.ivVoiceMic.setOnClickListener {
            onLocalAudioMuteVoiceClicked()
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


    private fun joinChannel() {
        val options = ChannelMediaOptions()
        options.autoSubscribeAudio = true
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
      agoraEngine!!.joinChannel(args.token, args.channelname, uid, options)
    }

    fun joinLeaveChannel() {
        if (isJoined) {
            agoraEngine!!.leaveChannel()
            binding.infoText.text = "Join"
            binding.ivVoiceEndCall.setBackgroundResource(R.drawable.background_iconcallovalgreen)
            endButtonClicked()
        } else {
            joinChannel()
            binding.infoText.text = "Leave"
            binding.ivVoiceEndCall.setBackgroundResource(R.drawable.background_iconcalloval)
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


    }
    fun endCallOtherUser() {
        Handler(Looper.getMainLooper()).postDelayed({
            if(!otherUserjoined){
                //Show no Show button and Call the ChangeAppointmentStatus with status 2
                binding.ivVoiceEndCall.visibility = View.VISIBLE
            }
        }, 1 * 60 * 1000)
    }

    private fun onSwitchSpeakerVoiceClicked() {
        if (binding.ivVoiceSpeaker.isSelected) {
            binding.ivVoiceSpeaker.isSelected = false
            binding.ivVoiceSpeaker.setImageResource(R.drawable.ic_speaker)
        } else {
            binding.ivVoiceSpeaker.isSelected = true
          binding.ivVoiceSpeaker.setImageResource(R.drawable.ic_speaker)
        }
        agoraEngine?.setEnableSpeakerphone(binding.ivVoiceSpeaker.isSelected)
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
      viewModel.changeAppointmentStatus(args.appointmentId , 1)
        findNavController().navigate(R.id.appointmentsFragment)
        Toast.makeText(requireContext(),"ok",Toast.LENGTH_SHORT).show()
    }

    fun noShowClicked(){
        viewModel.changeAppointmentStatus(args.appointmentId , 2)
    }


}