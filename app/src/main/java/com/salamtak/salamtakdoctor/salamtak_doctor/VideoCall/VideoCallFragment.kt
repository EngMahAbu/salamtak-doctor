//package com.salamtak.salamtakdoctor.salamtak_doctor.VideoCall
//
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.salamtak.databinding.FragmentVideoCallBinding
//import us.zoom.sdk.*
//
//
//class VideoCallFragment : Fragment(),View.OnClickListener,ZoomSDKInitializeListener,InMeetingServiceListener {
//
//    private lateinit var binding: FragmentVideoCallBinding
//    private lateinit var zoomSDK: ZoomSDK
//    private lateinit var params: ZoomSDKInitParams
//    private lateinit var listener: ZoomSDKInitializeListener
//    private lateinit var name:String
//    private lateinit var meetingId:String
//    private lateinit var password:String
//    private lateinit var startUrl:String
//    private lateinit var meeting: MeetingService
//    private lateinit var joinMeetingOptions: JoinMeetingOptions
//    private lateinit var joinMeetingParams: JoinMeetingParams
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        arguments?.let {
//            startUrl = it.getString("start_url", "")
//        }
//
//        binding = FragmentVideoCallBinding.inflate(inflater, container , false)
//        // Inflate the layout for this fragment
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initializeZoomsdk(requireContext())
//        val zoomSDK = ZoomSDK.getInstance()
//        val meetingService = zoomSDK.inMeetingService
//        meetingService?.addListener(this)
//        binding.btnJoin.setOnClickListener(this)
//    }
//
//    private fun initializeZoomsdk(context: Context) {
//        zoomSDK = ZoomSDK.getInstance()
//        params = ZoomSDKInitParams()
//        params.appKey="Nv5YM36z17L5iCo7mUoFdESq2yPYCPKW33H9"
//        params.appSecret="bmRZ67X1XGX1xq5xSjBHWFGPTbzyozxjV9i9"
//        params.domain="zoom.us"
//        params.enableLog = false
//
//
//        zoomSDK.initialize(context,this,params)
//
//
//
//
//    }
//
//
//
//
//    override fun onClick(v: View?) {
//        if (v == binding.btnJoin) {
//            name = binding.name.text.toString().trim()
//            meetingId = binding.meetingId.text.toString().trim()
//            password = binding.password.text.toString().trim()
//            meeting = ZoomSDK.getInstance().meetingService
//
////            joinMeetingOptions = JoinMeetingOptions()
////            joinMeetingParams = JoinMeetingParams()
////            joinMeetingParams.displayName = name
////            joinMeetingParams.password = password
////            joinMeetingParams.meetingNo = meetingId
////            meeting.joinMeetingWithParams(context, joinMeetingParams, joinMeetingOptions)
//
//            meeting.handZoomWebUrl(name)
//
//
//        }
//
//    }
//
//
//
//    override fun onZoomSDKInitializeResult(p0: Int, p1: Int) {
////        TODO("Not yet implemented")
//    }
//
//    override fun onZoomAuthIdentityExpired() {
////        TODO("Not yet implemented")
//    }
//
//
//    override fun onMeetingNeedPasswordOrDisplayName(
//        p0: Boolean,
//        p1: Boolean,
//        p2: InMeetingEventHandler?
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onWebinarNeedRegister(p0: String?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onJoinWebinarNeedUserNameAndEmail(p0: InMeetingEventHandler?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingNeedCloseOtherMeeting(p0: InMeetingEventHandler?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingFail(p0: Int, p1: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingLeaveComplete(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingUserJoin(p0: MutableList<Long>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingUserLeave(p0: MutableList<Long>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingUserUpdated(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingHostChanged(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingCoHostChanged(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingCoHostChange(p0: Long, p1: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onActiveVideoUserChanged(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onActiveSpeakerVideoUserChanged(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onHostVideoOrderUpdated(p0: MutableList<Long>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFollowHostVideoOrderChanged(p0: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSpotlightVideoChanged(p0: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSpotlightVideoChanged(p0: MutableList<Long>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onUserVideoStatusChanged(p0: Long, p1: InMeetingServiceListener.VideoStatus?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onUserNetworkQualityChanged(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSinkMeetingVideoQualityChanged(p0: VideoQuality?, p1: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMicrophoneStatusError(p0: InMeetingAudioController.MobileRTCMicrophoneError?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onUserAudioStatusChanged(p0: Long, p1: InMeetingServiceListener.AudioStatus?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onHostAskUnMute(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onHostAskStartVideo(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onUserAudioTypeChanged(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMyAudioSourceTypeChanged(p0: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onLowOrRaiseHandStatusChanged(p0: Long, p1: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onChatMessageReceived(p0: InMeetingChatMessage?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onChatMsgDeleteNotification(p0: String?, p1: ChatMessageDeleteType?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onShareMeetingChatStatusChanged(p0: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSilentModeChanged(p0: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFreeMeetingReminder(p0: Boolean, p1: Boolean, p2: Boolean) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onMeetingActiveVideo(p0: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSinkAttendeeChatPriviledgeChanged(p0: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSinkAllowAttendeeChatNotification(p0: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSinkPanelistChatPrivilegeChanged(p0: InMeetingChatController.MobileRTCWebinarPanelistChatPrivilege?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onUserNameChanged(p0: Long, p1: String?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onUserNamesChanged(p0: MutableList<Long>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFreeMeetingNeedToUpgrade(p0: FreeMeetingNeedUpgradeType?, p1: String?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFreeMeetingUpgradeToGiftFreeTrialStart() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFreeMeetingUpgradeToGiftFreeTrialStop() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFreeMeetingUpgradeToProMeeting() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onClosedCaptionReceived(p0: String?, p1: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onRecordingStatus(p0: InMeetingServiceListener.RecordingStatus?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onLocalRecordingStatus(p0: Long, p1: InMeetingServiceListener.RecordingStatus?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onInvalidReclaimHostkey() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onPermissionRequested(p0: Array<out String>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onAllHandsLowered() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onLocalVideoOrderUpdated(p0: MutableList<Long>?) {
//        TODO("Not yet implemented")
//    }
//
//}