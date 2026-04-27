//package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments
//
////import us.zoom.sdk.InstantMeetingOptions
////import us.zoom.sdk.JoinMeetingOptions
////import us.zoom.sdk.MeetingOptions
////import us.zoom.sdk.StartMeetingOptions
//
//
//object ZoomMeetingUISettingHelper {
//    private val meetingOptions = StartMeetingOptions()
//
//    fun getStartMeetingOptions(): StartMeetingOptions {
//        val opts = StartMeetingOptions()
//        fillMeetingOption(opts)
//        return opts
//    }
//
//    fun getJoinMeetingOptions(): JoinMeetingOptions {
//        val opts = JoinMeetingOptions()
//        fillMeetingOption(opts)
//        opts.no_audio = meetingOptions.no_audio
//        return opts
//    }
//
//    private fun fillMeetingOption(opts: MeetingOptions): MeetingOptions {
//        opts.no_driving_mode = meetingOptions.no_driving_mode
//        opts.no_invite = meetingOptions.no_invite
//        opts.no_meeting_end_message = meetingOptions.no_meeting_end_message
//        opts.no_titlebar = meetingOptions.no_titlebar
//        opts.no_bottom_toolbar = meetingOptions.no_bottom_toolbar
//        opts.no_dial_in_via_phone = meetingOptions.no_dial_in_via_phone
//        opts.no_dial_out_to_phone = meetingOptions.no_dial_out_to_phone
//        opts.no_disconnect_audio = meetingOptions.no_disconnect_audio
//        opts.no_share = meetingOptions.no_share
//        opts.invite_options = meetingOptions.invite_options
//        opts.no_video = meetingOptions.no_video
//        opts.meeting_views_options = meetingOptions.meeting_views_options
//        opts.no_meeting_error_message = meetingOptions.no_meeting_error_message
//        opts.custom_meeting_id = meetingOptions.custom_meeting_id
//        opts.customer_key = meetingOptions.customer_key
//        return opts
//    }
//
//    fun getInstantMeetingOptions(): InstantMeetingOptions {
//        val opts = InstantMeetingOptions()
//        fillMeetingOption(opts)
//        return opts
//    }
//}