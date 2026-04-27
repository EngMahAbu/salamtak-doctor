package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.FragmentAppointmentsBinding
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.resquest.auth.FilterModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.filter.FilterViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicFilterAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicTypesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.HomeViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.adapter.SliderAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.CancelAppointmentDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.FilterBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.lang.reflect.InvocationTargetException
import java.text.SimpleDateFormat
import java.util.*


class AppointmentFragment : Fragment(),
    AppointmentAdapter.OnAppointmentClickListener,
    AppointmentAdapter.OnCancelAppointmentClickListener,
    AppointmentAdapter.OnPatientAccountClickListener,
    AppointmentAdapter.OnVideoClickedClickListener,
    View.OnClickListener, RecyclerViewOnClickListener, DialogsListener,
    AppointmentAdapter.OnActionClickedClicklistener {

    private lateinit var binding: FragmentAppointmentsBinding
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private lateinit var viewModel: AppointmentViewModel
    var selectedAppointment: Item? = null
    private val args:AppointmentFragmentArgs by navArgs()

    lateinit var loading: Dialog
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var homeViewModel: HomeViewModel
   // private val sessionManagement: SessionManagement by inject()
    val list = ArrayList<String>()
    var onActionPosition = 0
    private val birthDateCalendar = Calendar.getInstance()

    // clinic list
    private lateinit var clinicsAdapter: ClinicFilterAdapter
    val screenViewModel: SetupClinicViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_Clinic_VIEW_MODEL))

    private lateinit var clinicTypeAdapter: ClinicTypesAdapter
    val clinicsTypeViewModel: SetupServicesViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_SERVICE_VIEW_MODEL))

    private lateinit var filterViewModel: FilterViewModel

//    private var clinicsIds = ArrayList<String>()
//    var clinicTypelId = 0
//    var newAppointmentId: Int = 0
    var appointmentId: Int = 0
    var today: String = ""
    var toDate: String = ""
    var clinicId: Int = 0
    var isCompletedClicked = false
    var isNoShowClicked = false
    var x = ""
    var formatted: String = ""
    var voiceCallClicked = false
    var videoCallClicked = false
    var messageCode=0

    companion object {
        var patientId: Long = 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        clinicTypeAdapter = ClinicTypesAdapter(requireContext(), this)
        clinicsAdapter = ClinicFilterAdapter(this)

//        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LocalDateTime.now()
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }
//        val formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM")
//        formatted = current.format(formatter)

        val sdf = SimpleDateFormat("yyyy-dd-MM")
        val date = Calendar.getInstance().time
        formatted = sdf.format(date)

        arguments?.let {
            appointmentId = it.getInt(APPOINTMENT_ID, 0)
            today = it.getString(TODAY_DATE, formatted)
            toDate = it.getString(TO_DATE, formatted)
            clinicId = it.getInt(CLINIC_ID, 0)
            Log.d(TAG, "serviceClick: " + appointmentId + today + toDate + clinicId)
        }

        loading = Dialog(requireContext())
        binding = FragmentAppointmentsBinding.inflate(layoutInflater,container, false)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        filterViewModel = ViewModelProvider(this).get(FilterViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

//        clinicsAdapter = ClinicFilterAdapter(this)
//        clinicTypeAdapter = ClinicTypesAdapter(requireContext(), this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)

        try {
            if (!args?.clinicId.isNullOrEmpty()){
                clinicId= args.clinicId?.toInt() ?: 0
            }
        } catch (e: InvocationTargetException) {

            // Answer:
            e.printStackTrace();
        } catch (e:Exception ) {

            // generic exception handling
            e.printStackTrace();
        }

        initListner()
        setUi()
        inits()
        initObserve()
        initButton()
    }


    private fun initButton(){

        binding.statisctics.setOnClickListener {
            val action = args.clinicId?.let {
                AppointmentFragmentDirections.actionAppointmentFragmentToStatisticsFragment(
                    it.toInt())
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }
        binding.appointments.setOnClickListener {
            val action = args.clinicId?.let {
                AppointmentFragmentDirections.actionAppointmentFragmentSelf(
                    it)
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }
    }

    private fun newEdit() {
//        binding.backgroundGrayToolbar.root.visibility = View.GONE
        binding.statusRadio.visibility = View.GONE
        binding.filterIv.visibility = View.GONE


        // //TODO
        if (isOnline(mContext)) {
            filterViewModel.filter(fillFilterRequest(FilterModel()))
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }
//        filterViewModel.filter(fillFilterRequest(FilterModel()))

    }

    private fun fillFilterRequest(filterModel: FilterModel): FilterModel {
        filterModel.MaxResultCount = 1000
        filterModel.FromDate = today
        filterModel.ToDate = toDate
        filterModel.MedicalExaminationTypeId = appointmentId
        filterModel.PatientName = binding.search.text.toString()
        filterModel.SkipCount = 0
        filterModel.ClinicId = clinicId

        return filterModel
    }

    private fun initListner() {
        binding.appointmentRv.layoutManager = LinearLayoutManager(requireContext())

        binding.search.addTextChangedListener {
            if (it?.length!! >= 3) {
                if (isOnline(mContext)) {
                    filterViewModel.filter(fillFilterRequest(FilterModel()))
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }
            }
            if (it.isEmpty()) {
                if (isOnline(mContext)) {
                    Log.d("getReq", "initObserve: $today, $toDate,$clinicId, $appointmentId")
                    Log.d("getAppointmentLog","on line 252")
                    viewModel.getAppointment(today, toDate, clinicId, appointmentId, 0, 15)
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }
            }
        }
//
//
        if (binding.search.length() > 3) {
            filterViewModel.filter(fillFilterRequest(FilterModel()))
        }

    }

//    private fun initObserves() {
//
//        filterViewModel.filterResponseModel.observe(viewLifecycleOwner) {
//            x = it.Data.Items.toString()
//            Log.d(TAG, "initObserve: " + x)
//            binding.appointmentRv.adapter = AppointmentAdapter(it.Data.Items, this, this, mContext)
//
//        }
//
//    }

    @SuppressLint("SuspiciousIndentation")
    private fun initObserve() {

        homeViewModel.getHomeAds()

        homeViewModel.homeAdsModel.observe(viewLifecycleOwner, Observer {
//            it.Data[0].getAd_ImagesDtos.forEach { ddModel ->
//                list.add("https://salamtechapi.azurewebsites.net" + ddModel.ImageURL)
//            }
//            Log.d(ContentValues.TAG, "AdsImages: " + list)
            binding.viewPager.adapter =
                SliderAdapter((activity as FragmentActivity), it.Data as ArrayList<HomeAdsData>)
        })

        viewModel.AppointmentModel.observe(viewLifecycleOwner, Observer {
            Log.d("jshkjdhdkjfhsdk","kldjkldsfjkld")
//            if (it.Data.Items.isNullOrEmpty()) {
//                binding.emptyPageLayout.emptyPage.visibility = View.VISIBLE
//                binding.emptyPageLayout.message.setText("There are no Appointment yet")
//                binding.emptyPageLayout.descmessage.visibility = View.GONE
//                binding.emptyPageLayout.emptyGoToBtn.visibility = View.GONE
//            } else
//                binding.emptyPageLayout.emptyPage.visibility = View.GONE

            binding.appointmentRv.adapter =
                AppointmentAdapter(
                    it.Data.Items, this,
                    this, this,
                    this, this,
                     mContext
                )

        })

        viewModel.cancel.observe(viewLifecycleOwner, Observer {

            if (it.Success) {
                viewModel.AppointmentModel.observe(viewLifecycleOwner, Observer { it1 ->
                    it1.Data.Items[onActionPosition].IsCancel = true
                    binding.appointmentRv.adapter!!.notifyItemChanged(onActionPosition)

                })
                messageCode=it.MessageCode
                if (it.MessageCode==201){
                    findNavController().navigate(R.id.appointmentsFragment)
                    findNavController().navigate(R.id.dialogCalling)
                }
            }
        })
        // change appointements status
        viewModel.changeAppointmentStatusResponse.observe(viewLifecycleOwner, Observer {
            if (it.Success) {
                viewModel.AppointmentModel.observe(viewLifecycleOwner, Observer { it1 ->
                    if (isCompletedClicked) {
                        isCompletedClicked = false
                        it1.Data.Items[onActionPosition].IsCompleted = true

                    }
                    if (isNoShowClicked) {
                        isNoShowClicked = false
                        it1.Data.Items[onActionPosition].IsNoShow = true
                    }
                    binding.appointmentRv.adapter!!.notifyItemChanged(onActionPosition)
                })

                it.Message?.let { it1 -> toast(requireContext(), it1) }
            }
//            toast(requireContext(), it.Message)
        })
        // show all count list
        ////TODO INDER
        // osama
        if (isOnline(mContext)) {
            Log.d("getReq", "initObserve: $today, $toDate,$clinicId, $appointmentId")
            Log.d("getAppointmentLog","on line 345")
            viewModel.getAppointment(today, toDate, clinicId, appointmentId, 0, 15)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        filterViewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            it.Message?.let { it1 -> errorMessage(it1) }
        })

        filterViewModel.filterResponseModel.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "initObserve: " + x)
            if (it.Data.Items.isNullOrEmpty()) {
                toast(requireContext(), "No data found")
            } else {
                x = it.Data.Items.toString()
                binding.appointmentRv.adapter =
                    AppointmentAdapter(it.Data.Items, this, this,
                        this, this, this, mContext)
            }
        })
//        filterViewModel.meetingInfoModel.observe(viewLifecycleOwner, Observer {
//            if (!it.Data.ZoomStartURL.isNullOrEmpty()) {
////                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.Data.ZoomStartURL))
////                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
////                    startActivity(intent)
////                }
//                meeting = zoomSDK.meetingService
////                            val joinMeetingOptions = JoinMeetingOptions()
////            val joinMeetingParams = JoinMeetingParams()
////            joinMeetingParams.displayName = "Hello"
////            joinMeetingParams.password = it.Data.zoomMeetPassword
////            joinMeetingParams.meetingNo = it.Data.zoomMeetID
////            meeting.joinMeetingWithParams(context, joinMeetingParams, joinMeetingOptions)
////                meeting.handZoomWebUrl(it.Data.ZoomStartURL)
//
//
//                val startMeetingOptions = StartMeetingOptions()
////             val joinMeetingParams = JoinMeetingParams()
//
////                val opts: StartMeetingOptions = ZoomMeetingSDKSettingHelper
//
//                val opts: StartMeetingOptions = ZoomMeetingUISettingHelper.getStartMeetingOptions()
//
//                val params = StartMeetingParamsWithoutLogin()
//////            params.userId = "userId" //TODO: Enable for proper working functionality...
////
//                params.userId = it.Data.host_id
//                params.userType = MeetingService.USER_TYPE_API_USER
//                params.displayName = "Doctor"
//
//                var startIndex = it.Data.ZoomStartURL.indexOf('=')
//                var zak =
//                    it.Data.ZoomStartURL.substring(startIndex + 1, it.Data.ZoomStartURL.length)
//                params.zoomAccessToken = zak
//                params.meetingNo = it.Data.zoomMeetID
//
////                Log.d("getZoomLink", "initObserve: ${it.Data.ZoomStartURL}")
////                Log.d("getZoomLink", "zak is : $zak")
//
//
//                meeting.startMeetingWithParams(context, params, opts)
//                Log.d("Metting", meeting.startMeetingWithParams(context, params, opts).toString())
//
//                Toast.makeText(context, it.Data.zoomMeetPassword, Toast.LENGTH_LONG).show()
//
//
////                val meetingService = zoomSDK.inMeetingService
////                val meetingService2 = zoomSDK.meetingService
//                meeting.addListener(this)
////                meetingService2?.addListener(this)
//            } else {
//                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
//            }
////            val bundle = Bundle()
////            bundle.putString("start_url", it.Data.ZoomStartURL)
////            findNavController().navigate(R.id.videoCallSdk, bundle)
//        })


    }

    private fun setUi() {
        binding.historyRb.setOnClickListener(this)
        binding.currentRb.setOnClickListener(this)
        binding.filterIv.setOnClickListener(this)
        binding.dateTv.setOnClickListener(this)
//        binding.toDateTv.setOnClickListener(this)
        binding.doneTv.setOnClickListener(this)
//        binding.search.setOnClickListener(this)

//        binding.toolbar.ivBack.setOnClickListener {
//            navController.popBackStack()
//        }
//        binding.toolbar.toolbarHeader.text = getString(R.string.schedule)
//        binding.toolbar.ivBackBg.visibility = View.GONE
//        binding.toolbar.ivBack.visibility = View.GONE

        getClinicType()
        getClinincs()
    }

    private fun getClinincs() {
        binding.clinicsHomeRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )

        binding.clinicsHomeRv.adapter = clinicsAdapter
////        ////TODO 29/11/22
//        if(isOnline(mContext)){
//            screenViewModel.getClinics()
//        }
//        else{
//                       noConnectivityDialog.noInternetDialogFragment(requireContext())

//        }
        screenViewModel.getClinics()

        screenViewModel.clinicsResponse.observe(viewLifecycleOwner) {
            clinicsAdapter.submitData(it.Data as ArrayList<ClinicData>)
        }

    }

    private fun getClinicType() {
        binding.staticsRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )

        binding.staticsRv.adapter = clinicTypeAdapter

//        ////TODO 29/11/22
//        if(isOnline(mContext)){
//            clinicsTypeViewModel.getServicesTypes()
//        }
//        else{
//                       noConnectivityDialog.noInternetDialogFragment(requireContext())

//        }
        clinicsTypeViewModel.getServicesTypes()


        clinicsTypeViewModel.servicesTypesResponse.observe(viewLifecycleOwner) {
            clinicTypeAdapter.submitData(it.Data)
            Log.d("getData", "getClinicType: ${it.Data.size} && ${it.Data[0].Name}")
        }


    }

    override fun onClinicTypeSelect(clinicsId: Int, clinicNameTxt: String, position: Int) {
        appointmentId = clinicsId
//        viewModel.getHome(fromDate, toDate, clinicData.ClinicId)
        ////TODO INDER
        if (isOnline(mContext)) {
            filterViewModel.filter(fillFilterRequest(FilterModel()))
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }
//        filterViewModel.filter(fillFilterRequest(FilterModel()))
    }

    override fun onClinicSelect(clinicxId: Int, clinicNameTxt: String, position: Int) {
        clinicId = clinicxId
//        viewModel.getHome(fromDate, toDate, clinicData.ClinicId)
        ////TODO INDER
        if (isOnline(mContext)) {
            filterViewModel.filter(fillFilterRequest(FilterModel()))
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }
//        filterViewModel.filter(fillFilterRequest(FilterModel()))

    }

    override fun onAppointmentClicked(newsData: Item) {
        val bundle = Bundle()
        bundle.putLong(APPOINTMENT_ID, newsData.AppointmentId)
        bundle.putLong(PATIENT_ID, newsData.PatientId)
        bundle.putString(CLINIC_NAME, newsData.ClinicName)
        bundle.putString(CLINIC_TYPE, newsData.MedicalExaminationTypeName)
        bundle.putString(CLINIC_DATE, newsData.AppointmentDate)
        findNavController().navigate(R.id.patientProfileFragment, bundle)

        patientId = newsData.PatientId
    }

    override fun onVideoCallClicked(appointmentId: Long) {
        videoCallClicked = true
        voiceCallClicked = false
        viewModel.getAgoraMeeting(appointmentId = appointmentId.toInt())
    }

    override fun onVoiceCallClicked(appointmentId: Long, appointmentItem: Item) {
        selectedAppointment = appointmentItem
        voiceCallClicked = true
        videoCallClicked = false
        viewModel.getAgoraMeeting(appointmentId = appointmentId.toInt())

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.current_rb -> {
                viewModel.AppointmentModel.observe(viewLifecycleOwner, Observer {
                    binding.appointmentRv.adapter =
                        AppointmentAdapter(
                            it.Data.Items,
                            this,
                            this,
                            this,
                            this,
                            this,
                            mContext
                        )
                })
                ////TODO
                if (isOnline(mContext)) {
                    viewModel.getAppointment()
                    Log.d("getAppointmentLog","Without parameter")
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }
            }

            R.id.history_rb -> {

                viewModel.AppointmentModel.observe(viewLifecycleOwner, Observer {
                    //show history items
                    binding.appointmentRv.adapter = HistoryAppointmentAdapter(it.Data.Items)
                })

                ////TODO
                if (isOnline(mContext)) {
                    viewModel.getHistoryAppointment()
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())

                }

            }

            R.id.filter_iv -> {
//                getClinics()
//                getClinicType()
//                Log.d(TAG, "mansourclick: " + clinicTypelId)
                val filterBottomSheetDialog = FilterBottomSheetDialog(
                    filterViewModel,
                    clinicsTypeViewModel,
                    screenViewModel,
                    navController
                )
                filterBottomSheetDialog.show(childFragmentManager, "")
            }

            R.id.date_tv -> {
//                openDatePicker()
                showBottomSheetDialog()
            }

//            R.id.to_date_tv -> {
//                openToDatePicker()
//            }

            R.id.done_tv -> {
//                viewModel.getHome(fromDate, toDate)
      //          viewModel.getAppointment(today, toDate, medicalTypeId = appointmentId)
//                initObserves()
//                ////TODO
                if (isOnline(mContext)) {

                    filterViewModel.filter(fillFilterRequest(FilterModel()))
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }


            }

        }
    }

    override fun onCancelAppointmentClicked(newsData: Item) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.cancelappointmentbottomsheet)

        bottomSheetDialog.show()
        val yes = bottomSheetDialog.findViewById<MaterialButton>(R.id.yes_btn)
        val no = bottomSheetDialog.findViewById<MaterialButton>(R.id.No_button)


        yes?.setOnClickListener {
            val sucess = CancelAppointmentDialog(viewModel, newsData, findNavController(),messageCode)
            sucess.show(childFragmentManager, "0")
            viewModel.cancel.observe(viewLifecycleOwner, Observer {
                binding.appointmentRv.adapter?.notifyDataSetChanged()
            })
            bottomSheetDialog.dismiss()
        }

        no?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (loading.isShowing) {
            loading.dismiss()
        }
    }


    ////TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }


    private fun inits() {

        viewModel.AgoraModel.observe(viewLifecycleOwner) {
            if (it.MessageCode == 200) {


            }
            Toast.makeText(requireContext(), "${it.Message}", Toast.LENGTH_SHORT).show()
        }

        viewModel.AgoraMeetingModel.observe(viewLifecycleOwner) {
            if (it.MessageCode == 200) {

                if (videoCallClicked) {
                    //open agora view
////                    var intent = Intent(requireActivity(), AgoraVideoCall::class.java)
////                    intent.putExtra(AGORA_TOKEN, it.Data.Token)
////                    intent.putExtra(AGORA_CHANNEL, it.Data.channelName)
////                    intent.putExtra(AGORA_APPOINTMENT_ID, it.Data.AppointmentId)
////                    startActivity(intent)
                    val action=AppointmentsFragmentDirections.actionAppointmentsFragmentToVideoCallFragment(it.Data.Token,it.Data.channelName,it.Data.AppointmentId,"")
                    findNavController().navigate(action)
                }

                if (voiceCallClicked) {
                    //open agora view
//                    var intent = Intent(requireActivity(), AgoraVoiceCall::class.java)
//                    intent.putExtra(AGORA_TOKEN, it.Data.Token)
//                    intent.putExtra(AGORA_CHANNEL, it.Data.channelName)
//                    intent.putExtra(AGORA_APPOINTMENT_ID, it.Data.AppointmentId)
//                    intent.putExtra(PATIENT_NAME, selectedAppointment?.PatientName)
//                    intent.putExtra(PATIENT_IMAGE, selectedAppointment?.PatientImage)
//                    startActivity(intent)
                    val action=AppointmentsFragmentDirections.actionAppointmentsFragmentToAudioCallFragment(it.Data.Token,it.Data.channelName,it.Data.PatientImage,it.Data.PatientName,it.Data.AppointmentId)
                    findNavController().navigate(action)
                }


            }
           Toast.makeText(requireContext(), "${it.Message}", Toast.LENGTH_SHORT).show()
        }

        filterViewModel.filterResponseModel.observe(viewLifecycleOwner) {
            val x = it.Data.Items.toString()
            Log.d(TAG, "initObserve: " + x)
            if (it.Data.Items.isNullOrEmpty()) {
                toast(requireContext(), "No data found")
            } else {
                binding.appointmentRv.adapter =
                    AppointmentAdapter(it.Data.Items,
                        this,
                        this,
                        this,
                        this,
                        this,
                        mContext)
            }
        }

        filterViewModel.updateMeetingResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "${it.Message}", Toast.LENGTH_SHORT).show()
        }
//        filterViewModel.meetingInfoModel.observe(viewLifecycleOwner) {
//            if(!it.Data.ZoomStartURL.isNullOrEmpty()){
//                meeting = ZoomSDK.getInstance().meetingService
//                meeting.handZoomWebUrl(it.Data.ZoomStartURL)
//            }else{
//                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
//            }
//        }

    }

//    private fun getClinics() {
//        screenViewModel.getClinics()
//        screenViewModel.clinicsResponse.observe(viewLifecycleOwner) {
//            clinicsAdapter.submitData(it.Data as ArrayList<ClinicData>)
//        }
//    }

//
//    private fun getClinicType() {
//        clinicsTypeViewModel.getServicesTypes()
//        clinicsTypeViewModel.servicesTypesResponse.observe(viewLifecycleOwner) {
//            val list = ArrayList<ServicesTypesData>()
////            for (i in 1 until it.Data.size)
////                list.add(it.Data[i])
////            Log.e(TAG, "subscribeToObserver: $list")
////            clinicTypeAdapter.submitData(list)
//            clinicTypeAdapter.submitData(it.Data as ArrayList<ServicesTypesData>)
//        }
//    }

//    override fun onSubSpecialitySelect(
//        subSpecialityId: Int,
//        subSpecialityTxt: String,
//        position: Int
//    ) {
//        clinicsIds = clinicsAdapter.getAdditionIds()
//        Log.e(TAG, "onClinicSelect: ${clinicsIds.size}")
////        binding.subSpecializationSelect.text = clinicsAdapter.getAdditionTexts()
//
//    }

//    override fun onSenioritySelect(seniorityId: Int, seniorityTxt: String, position: Int) {
//        clinicTypelId = seniorityId
//    }

//    override fun onClinicTypeSelect(clinicId: Int, clinicNameTxt: String, position: Int) {
//        clinicTypelId = clinicId
//    }

    private fun openDatePicker() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                birthDateCalendar[Calendar.YEAR] = year
                birthDateCalendar[Calendar.MONTH] = monthOfYear
                birthDateCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateBirthDate()
            }

        val datePicker = DatePickerDialog(
            requireContext(),
            date,
            birthDateCalendar[Calendar.YEAR],
            birthDateCalendar[Calendar.MONTH],
            birthDateCalendar[Calendar.DAY_OF_MONTH]
        )

        val maxDateCalender = Calendar.getInstance()
        maxDateCalender.set(currentYear, 11, 31)

        val minDateCalender = Calendar.getInstance()
        minDateCalender.set(currentYear - 100, 0, 1)

        datePicker.datePicker.maxDate = maxDateCalender.timeInMillis
        datePicker.datePicker.minDate = minDateCalender.timeInMillis

        datePicker.show()

    }

    private fun openToDatePicker() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                birthDateCalendar[Calendar.YEAR] = year
                birthDateCalendar[Calendar.MONTH] = monthOfYear
                birthDateCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateToBirthDate()
            }

        val datePicker = DatePickerDialog(
            requireContext(),
            date,
            birthDateCalendar[Calendar.YEAR],
            birthDateCalendar[Calendar.MONTH],
            birthDateCalendar[Calendar.DAY_OF_MONTH]
        )

        val maxDateCalender = Calendar.getInstance()
        maxDateCalender.set(currentYear, 11, 31)

        val minDateCalender = Calendar.getInstance()
        minDateCalender.set(currentYear - 100, 0, 1)

        datePicker.datePicker.maxDate = maxDateCalender.timeInMillis
        datePicker.datePicker.minDate = minDateCalender.timeInMillis

        datePicker.show()

    }

    private fun updateBirthDate() {
//        binding.dateTv.setText("${birthDateCalendar[Calendar.YEAR]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
        var month = birthDateCalendar[Calendar.MONTH] + 1
        var day = birthDateCalendar[Calendar.DAY_OF_MONTH]
        var newMonth = ""
        var newDay = ""

        if (month < 10) {
            newMonth = "0$month"
        }
        if (day < 10) {
            newDay = "0$day"
        }
        if (day >= 10) {
            newDay = birthDateCalendar[Calendar.DAY_OF_MONTH].toString()
        }
        if (month >= 10) {
            newMonth = month.toString()
        }

//        this.today = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")
    }

    private fun updateToBirthDate() {
//        binding.toDateTv.setText("${birthDateCalendar[Calendar.YEAR]}/${birthDateCalendar[Calendar.MONTH] + 1}/${birthDateCalendar[Calendar.DAY_OF_MONTH]}")
        var month = birthDateCalendar[Calendar.MONTH] + 1
        var day = birthDateCalendar[Calendar.DAY_OF_MONTH]
        var newMonth = ""
        var newDay = ""

        if (month < 10) {
            newMonth = "0$month"
        }
        if (day < 10) {
            newDay = "0$day"
        }
        if (day >= 10) {
            newDay = birthDateCalendar[Calendar.DAY_OF_MONTH].toString()
        }
        if (month >= 10) {
            newMonth = month.toString()
        }
//        this.toDate = ("${birthDateCalendar[Calendar.YEAR]}-${newMonth}-${newDay}")
//        viewModel.getHome(fromDate, toDate)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onPatientAccountClicked(newsData: Item) {
//        TODO pending connection
        val bundle = Bundle()
        bundle.putLong(PATIENT_ID, newsData.PatientId)
        bundle.putLong(APPOINTMENT_ID, newsData.AppointmentId)
        bundle.putString(CLINIC_NAME, newsData.ClinicName)
        bundle.putString(CLINIC_TYPE, newsData.MedicalExaminationTypeName)
        bundle.putString(CLINIC_DATE, newsData.AppointmentDate)
        findNavController().navigate(R.id.patientProfileFragment, bundle)
    }

    override fun onVideoClicked(newsData: Item) {
        viewModel.getAgoraMeeting(appointmentId = appointmentId.toInt())
//        startActivity(Intent(requireContext(), AgoraVideoCallOld::class.java))
//        newAppointmentId = newsData.AppointmentId.toInt()
//        if (isOnline(mContext)) {
//            filterViewModel.startMeeting(
//                "Bearer ${sessionManagement.getValue(SessionManagement.KEY_USER_TOKEN)}",
//                newsData.AppointmentId.toString()
//            )
//        } else {
//            noConnectivityDialog.noInternetDialogFragment(requireContext())
//        }
//        findNavController().navigate(R.id.videoCallSdk)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onDestroy() {
//        if (this::meeting.isInitialized)
//            meeting.removeListener(this)
        super.onDestroy()
    }


    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_calendar_view)

        val calendar = bottomSheetDialog.findViewById<DateRangeCalendarView>(R.id.calendar)
        val btnDone = bottomSheetDialog.findViewById<MaterialButton>(R.id.done_btn)

        val sdf = SimpleDateFormat("EEE, dd MMM")

        calendar?.setCalendarListener(object : CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {

                Log.d(TAG, "onFirstDateSelected: ${sdf.format(startDate.time)}")
                val sdfFrom = SimpleDateFormat("yyyy-MM-dd")
                if (!sdfFrom.format(startDate.time)
                        .equals(sdfFrom.format(Calendar.getInstance().time))
                ) {
                    if (Calendar.getInstance().time.before(startDate.time)) {
                        toDate = sdfFrom.format(startDate.time)
                        today = sdfFrom.format(Calendar.getInstance().time)
                    } else {
                        toDate = sdfFrom.format(Calendar.getInstance().time)
                        today = sdfFrom.format(startDate.time)
                    }

                } else {
                    toDate = sdfFrom.format(Calendar.getInstance().time)
                    today = sdfFrom.format(Calendar.getInstance().time)
                }

            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                val fromD = sdf.format(startDate.time)
                val toD = sdf.format(endDate.time)
                binding.textdate.text = "$fromD - $toD"
                val sdfFrom = SimpleDateFormat("yyyy-MM-dd")
                today = sdfFrom.format(startDate.time)
                toDate = sdfFrom.format(endDate.time)
            }
        })

        btnDone?.setOnClickListener {
            if (isOnline(mContext)) {
//                viewModel.getHome(fromDate, toDate)
                filterViewModel.filter(fillFilterRequest(FilterModel()))
                bottomSheetDialog.dismiss()
            } else {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
                bottomSheetDialog.dismiss()
            }

        }


        bottomSheetDialog.show()
    }

    override fun onActionClicked(newsData: Item, position: Int,isAction:Boolean) {
        onActionPosition = position
//        val bottomSheetDialog = BottomSheetDialog(requireContext())
//        bottomSheetDialog.setContentView(R.layout.actionsbottomsheet)
//        bottomSheetDialog.show()
//
//        val btnComplete = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnComplete)
//        val btnNoShow = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnNoShow)
//        val cancel = bottomSheetDialog.findViewById<MaterialButton>(R.id.cancle_button_bottom)
//
//        cancel?.visibility = View.GONE
//
//        btnComplete!!.setOnClickListener {
//            bottomSheetDialog.dismiss()
//            isCompletedClicked = true
//            viewModel.changeAppointmentStatus(newsData.AppointmentId.toInt(), 1)
//        }
//        btnNoShow!!.setOnClickListener {
//            isNoShowClicked = true
//            bottomSheetDialog.dismiss()
//            viewModel.changeAppointmentStatus(newsData.AppointmentId.toInt(), 2)
//        }


        if (isAction){
            isCompletedClicked = true
            viewModel.changeAppointmentStatus(newsData.AppointmentId.toInt(), 1)
        }else{
            isNoShowClicked = true
            viewModel.changeAppointmentStatus(newsData.AppointmentId.toInt(), 2)
        }



//        account?.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putLong(APPOINTMENT_ID, newsData.AppointmentId)
//            bundle.putLong(PATIENT_ID, newsData.PatientId)
//            bundle.putString(CLINIC_NAME, newsData.ClinicName)
//            bundle.putString(CLINIC_TYPE, newsData.MedicalExaminationTypeName)
//            bundle.putString(CLINIC_DATE, newsData.AppointmentDate)
//            Log.d("getData", "onActionClicked: ${newsData.PatientId}")
//            findNavController().navigate(R.id.patientProfileFragment, bundle)
//            bottomSheetDialog.dismiss()
//        }
//        cancel?.setOnClickListener {
//            bottomSheetDialog.dismiss()
//
//            val bottomSheetDialog = BottomSheetDialog(requireContext())
//            bottomSheetDialog.setContentView(R.layout.cancelappointmentbottomsheet)
//            bottomSheetDialog.show()
//            val yes = bottomSheetDialog.findViewById<MaterialButton>(R.id.yes_btn)
//            val no = bottomSheetDialog.findViewById<MaterialButton>(R.id.No_button)
//
//
//            yes?.setOnClickListener {
//                bottomSheetDialog.dismiss()
////                val sucess = CancelAppointmentDialog(viewModel, newsData, findNavController())
////                sucess.show(childFragmentManager, "0")
//                viewModel.cancelAppointment(
//                    newsData.AppointmentId.toInt(),
//                    ""
//                )
//
////                viewModel.cancel.observe(viewLifecycleOwner, Observer {
////
////                })
//            }
//
//            no?.setOnClickListener {
//                bottomSheetDialog.dismiss()
//            }
//
//        }
    }

}