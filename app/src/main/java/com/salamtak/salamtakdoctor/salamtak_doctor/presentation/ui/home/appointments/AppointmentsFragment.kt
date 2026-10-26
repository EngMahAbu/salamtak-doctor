package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.databinding.BottomSheetQueueClinicsBinding
import com.salamtak.databinding.FragmentAppointmentsBinding
import com.salamtak.domain.entities.response.*
import com.salamtak.domain.entities.response.poly.ListOfDoctors
import com.salamtak.domain.entities.resquest.auth.FilterModel
import com.salamtak.salamtakdoctor.adapter.AppointemtsPollyClinicsAdapter
import com.salamtak.salamtakdoctor.adapter.PollyClinicsDoctorsAdapter
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.model.PatientProfile
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.filter.FilterViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.queue.QueueClinicsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicFilterAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicTypesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.HomeViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.adapter.SliderAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.OffersViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.CancelAppointmentDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.FilterBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.PickerItem
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SearchablePickerBottomSheet
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.HospitalViewModel
import com.salamtak.core.localDataBase.Localization
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.text.SimpleDateFormat
import java.util.*


class AppointmentsFragment : Fragment(),
    AppointmentAdapter.OnAppointmentClickListener,
    AppointmentAdapter.OnCancelAppointmentClickListener,
    AppointmentAdapter.OnPatientAccountClickListener,
    AppointmentAdapter.OnVideoClickedClickListener,
    View.OnClickListener, RecyclerViewOnClickListener, DialogsListener,
    AppointmentAdapter.OnActionClickedClicklistener, PollyClinicsDoctorsAdapter.ISelectDoctor,AppointemtsPollyClinicsAdapter.ISelectClinics{

    private lateinit var binding: FragmentAppointmentsBinding
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private lateinit var viewModel: AppointmentViewModel
    private val sharedDataViewModel: SharedDataViewModel by activityViewModels()
    private lateinit var polyclinicsviewModel: PolyClinicsViewModel
    var selectedAppointment: Item? = null
    private var doctorID: Int? = null
    private var polyInfoID: Int? = null
    lateinit var loading: Dialog
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomsheetbeahavoirClinics: BottomSheetBehavior<ConstraintLayout>
    private lateinit var pollyClinicsDoctorsAdapter: PollyClinicsDoctorsAdapter
    private lateinit var appointmetsPollyClinicsDoctorsAdapter: AppointemtsPollyClinicsAdapter
    // queue clinic picker
    private lateinit var offersViewModel: OffersViewModel
    private lateinit var queueClinicsAdapter: QueueClinicsAdapter
    private var queueClinicsSheet: BottomSheetDialog? = null
    private var queueSheetBinding: BottomSheetQueueClinicsBinding? = null
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
    var clinicId: Int? = null
    var isCompletedClicked = false
    var isNoShowClicked = false
    var x = ""
    var formatted: String = ""
    var voiceCallClicked = false
    var videoCallClicked = false
    var messageCode=0

    // Hospital mode: every filter reloads the paged list with PolyInfoID = hospital InfoID
    private val isHospital = DateUtils.isHospital()
    private lateinit var hospitalViewModel: HospitalViewModel
    private var patientName: String? = null
    private var showClinicsPickerOnLoad = false
    private var showDoctorsPickerOnLoad = false

    companion object {
        var patientId: Long = 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        clinicTypeAdapter = ClinicTypesAdapter(requireContext(), this)
        clinicsAdapter = ClinicFilterAdapter(this)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
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
        binding =FragmentAppointmentsBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        filterViewModel = ViewModelProvider(this).get(FilterViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        polyclinicsviewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        offersViewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        hospitalViewModel = ViewModelProvider(this).get(HospitalViewModel::class.java)
//        clinicsAdapter = ClinicFilterAdapter(this)
//        clinicTypeAdapter = ClinicTypesAdapter(requireContext(), this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        // Clinic areas in the app's language for the appointment cards; redraw once loaded
        ClinicAreas.load(viewLifecycleOwner.lifecycleScope)
        ClinicAreas.areas.observe(viewLifecycleOwner) {
            binding.appointmentRv.adapter?.notifyDataSetChanged()
        }
        noInternetDialog = NoInternetDialog(mContext, this)
        binding.bottomnav.visibility=View.GONE
        doctorsRecylerview()
        clinicsRecylerview()
        bindViews()
        initListner()
        setUi()
        inits()
        initObserve()
        initButton()
        initBottomSheetServices()
        initQueueClinics()
        if (isHospital) initHospitalObservers()
    }

    private fun initQueueClinics() {
        queueClinicsAdapter = QueueClinicsAdapter { clinicId, clinicName ->
            queueClinicsSheet?.dismiss()
            findNavController().navigate(
                AppointmentsFragmentDirections.actionAppointmentsFragmentToQueueFragment()
                    .setClinicId(clinicId)
                    .setClinicName(clinicName)
            )
        }

        offersViewModel.clinicsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    queueSheetBinding?.progressBar?.visibility = View.VISIBLE
                }

                is Resource.sucess -> {
                    queueSheetBinding?.progressBar?.visibility = View.GONE
                    queueClinicsAdapter.submitList(it.data?.data.orEmpty())
                }

                is Resource.Error -> {
                    queueSheetBinding?.progressBar?.visibility = View.GONE
                    it.message?.let { message -> errorMessage(message) }
                }
            }
        }

        binding.btnQueue.setOnClickListener { showQueueClinicsSheet() }
    }

    private fun showQueueClinicsSheet() {
        if (queueClinicsAdapter.currentList.isEmpty()) {
            if (!isOnline(mContext)) {
                noConnectivityDialog.noInternetDialogFragment(requireContext())
                return
            }
            offersViewModel.getDoctorClinics()
        }
        val sheetBinding = BottomSheetQueueClinicsBinding.inflate(layoutInflater)
        sheetBinding.rvClinics.adapter = queueClinicsAdapter
        sheetBinding.progressBar.visibility =
            if (offersViewModel.clinicsResponse.value is Resource.Loading) View.VISIBLE else View.GONE
        queueSheetBinding = sheetBinding

        queueClinicsSheet = BottomSheetDialog(requireContext(), R.style.QueueSheetDialogTheme).apply {
            setContentView(sheetBinding.root)
            // open fully but leave the top of the screen visible; the list scrolls inside
            behavior.maxHeight = (resources.displayMetrics.heightPixels * 0.85).toInt()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            setOnDismissListener {
                queueClinicsSheet = null
                queueSheetBinding = null
            }
            show()
        }
    }

    private fun initButton(){
        if (isHospital) {
            binding.tvClinics.setOnClickListener { loadHospitalClinicsPicker() }
            binding.tvListofDoctors.setOnClickListener { loadHospitalDoctorsPicker() }
            return
        }
        binding.tvListofDoctors.setOnClickListener {
            callGetDoctorsClinicDayId()
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }

        binding.tvClinics.setOnClickListener {
            clinicsRecylerview()
            initObserveClinics()
            bottomsheetbeahavoirClinics.state =
                if (bottomsheetbeahavoirClinics.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }

        binding.layoutBottomsheetpersistant.btnApply.setOnClickListener {
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.layoutBottomsheetpersistantclinics.btnApply.setOnClickListener {
            bottomsheetbeahavoirClinics.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun doctorsRecylerview() {
        pollyClinicsDoctorsAdapter = PollyClinicsDoctorsAdapter(this)
        binding.layoutBottomsheetpersistant.rvClinks.apply {
            adapter = pollyClinicsDoctorsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun clinicsRecylerview() {
        appointmetsPollyClinicsDoctorsAdapter = AppointemtsPollyClinicsAdapter(this)
        binding.layoutBottomsheetpersistantclinics.rvClinks.apply {
            adapter = appointmetsPollyClinicsDoctorsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
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
        filterModel.MaxResultCount = 20
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
            val query = it?.toString().orEmpty()
            if (isHospital) {
                if (query.length >= 3 || query.isEmpty()) {
                    patientName = query
                    loadHospitalAppointments()
                }
                return@addTextChangedListener
            }
            if (query.length >= 3) {
                if (isOnline(mContext)) {
                    filterViewModel.filter(fillFilterRequest(FilterModel()))
                } else {
                    noConnectivityDialog.noInternetDialogFragment(requireContext())
                }
            } else if (query.isEmpty()) {
                if (isOnline(mContext)) {
                    Log.d("getReq", "initObserve: $today, $toDate,$clinicId, $appointmentId")
                    Log.d("getAppointmentLog","on line 252")
                    viewModel.getAppointment(today, toDate, clinicId, appointmentId, 0, 10,polyInfoID,doctorID)
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
            showAppointments(it.Data.Items)
            if (isHospital) {
                binding.tvNoAppointments.visibility =
                    if (it.Data.Items.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

        })

        viewModel.loadingMore.observe(viewLifecycleOwner, Observer {
            binding.appointmentsLoadMore.visibility = if (it) View.VISIBLE else View.GONE
        })

        // The list sits inside a NestedScrollView, so load the next page when that nears its bottom
        binding.appointmentsScroll.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
                val content = v.getChildAt(0) ?: return@OnScrollChangeListener
                val nearBottom = scrollY + v.height >= content.height - v.height / 2
                if (scrollY > oldScrollY && nearBottom) loadMoreAppointments()
            }
        )

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
        if (isHospital) {
            loadHospitalAppointments()
        } else if (isOnline(mContext)) {
            Log.d("getReq", "initObserve: $today, $toDate,$clinicId, $appointmentId")
            Log.d("getAppointmentLog","on line 345")
            viewModel.getAppointment(today, toDate, null, appointmentId, 0, 10,polyInfoID,doctorID)
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
                    AppointmentAdapter(it.Data.Items, this, this, this, this, this, mContext)
            }
        })
    }

    // A grown page list reuses the adapter (keeps the scroll position); anything else gets a new one
    private fun showAppointments(items: List<Item>) {
        val current = binding.appointmentRv.adapter as? AppointmentAdapter
        if (current != null && current.appointList === items) {
            current.notifyDataSetChanged()
        } else {
            binding.appointmentRv.adapter =
                AppointmentAdapter(items, this, this, this, this, this, mContext)
        }
    }

    // Only page while the paged current-appointments list is what's shown (not search or history)
    private fun loadMoreAppointments() {
        val shown = (binding.appointmentRv.adapter as? AppointmentAdapter)?.appointList
        if (shown !== viewModel.loadedAppointments) return
        if (!viewModel.hasMoreAppointments() || !isOnline(mContext)) return
        viewModel.loadMoreAppointments()
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
        // The doctor clinic list doesn't apply to hospitals
        if (!isHospital) getClinincs()
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
            // Data is null when the account has no doctor clinics (e.g. hospital)
            clinicsAdapter.submitData(ArrayList(it.Data.orEmpty()))
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
            Log.d("getData", "getClinicType: ${it.Data?.size} && ${it.Data?.firstOrNull()?.Name}")
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
                    showAppointments(it.Data.Items)
                })
                ////TODO
                if (isOnline(mContext)) {
                    viewModel.getAppointment(null,null,null,appointmentId,0,10,polyInfoID,doctorID)
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
                if (isHospital) {
                    showHospitalBookingTypePicker()
                    return
                }
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
//                viewModel.getAppointment(today, toDate, medicalTypeId = appointmentId)
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
//            viewModel.cancel.observe(viewLifecycleOwner, Observer {
//            })
            bottomSheetDialog.dismiss()
        }

        no?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        queueClinicsSheet?.dismiss()
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
        bundle.putString(Patient_NAME, newsData.PatientName)
        bundle.putString(Doctor_NAME, newsData.DoctorName)
        sharedDataViewModel.getPatientProfile(PatientProfile(newsData.PatientName,newsData.PatientPhone,newsData.ClinicName,newsData.ClinicAddress,newsData.ClinicPhone))
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
            if (isHospital) {
                loadHospitalAppointments()
                bottomSheetDialog.dismiss()
            } else if (isOnline(mContext)) {
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

        if (isAction){
            isCompletedClicked = true
            viewModel.changeAppointmentStatus(newsData.AppointmentId.toInt(), 1)
        }else{
            isNoShowClicked = true
            viewModel.changeAppointmentStatus(newsData.AppointmentId.toInt(), 2)
        }

    }

    private fun bindViews(){
        if (isHospital) {
            // CLINIC_ID defaults to 0 when Home opened the tab without a clinic filter
            if (clinicId == 0) clinicId = null
            // Not in the hospital design: Queue button
            binding.btnQueue.visibility = View.GONE
            binding.tvOr.visibility = View.VISIBLE
            binding.tvAllBookings.visibility = View.VISIBLE
        } else if (!DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            polyInfoID= DateUtils.getAdminInfoId()?.toInt()
        }else{
            binding.linearLayout2.visibility=View.GONE
        }

    }

    private fun initBottomSheetServices() {
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.doctorsBottomsheet)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN

        bottomsheetbeahavoirClinics =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistantclinics.appointmentsClinicsBottomsheet)
        bottomsheetbeahavoirClinics.state = BottomSheetBehavior.STATE_HIDDEN

    }

    private fun initObserveClinics(){

        homeViewModel.clinicModel.observe(viewLifecycleOwner, Observer {
            appointmetsPollyClinicsDoctorsAdapter.submitList(it.Data as MutableList<ClinicData>)
            appointmetsPollyClinicsDoctorsAdapter.notifyDataSetChanged()
            Log.d(TAG, "initObserve: ${it.Data}")
        })

        homeViewModel.showLoading.observe(viewLifecycleOwner, Observer {

        })

        homeViewModel.error.observe(viewLifecycleOwner, Observer {
//            errorMessage(it.Message)
            if (it.Message != null) {
                errorMessage(it.Message!!)
            } else {
                if (it.code == RequestCode.UNAUTHORIZED) {
                    //send to login scren
                }
            }
        })

        //todo 29/11/22
        if (isOnline(mContext)) {

            homeViewModel.getHomeClinics()

        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }
    }


    private fun callGetDoctorsClinicDayId() {
        polyclinicsviewModel.polyClinicDoctorsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility=View.VISIBLE
                }

                is Resource.sucess -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility=View.GONE
                    pollyClinicsDoctorsAdapter.submitList(it.data?.data)
                    pollyClinicsDoctorsAdapter.notifyDataSetChanged()

                }

                is Resource.Error -> {
                    binding.layoutBottomsheetpersistant.progressBar.visibility=View.GONE
                }

            }
        })
        clinicId?.let { polyclinicsviewModel.getDoctorByClinicId(it) }
    }

    override fun onDoctorSelected(listOfDoctors: ListOfDoctors) {
        this.doctorID=listOfDoctors.id
        viewModel.getAppointment(today, toDate, null, appointmentId, 0, 10,polyInfoID,doctorID)
        if (DefaultLocaleHelper.getInstance(requireContext()).getCurrentLocale()=="en"){
            binding.tvListofDoctors.text=listOfDoctors.fullName
        }else{
            binding.tvListofDoctors.text=listOfDoctors.fullNameAr
        }
    }
    override fun onClinicsSelected(clinicsdata: ClinicData) {
        this.clinicId=clinicsdata.ClinicId
        viewModel.getAppointment(today, toDate, clinicId, appointmentId, 0, 10,polyInfoID,doctorID)
        if (DefaultLocaleHelper.getInstance(requireContext()).getCurrentLocale()=="en"){
            binding.tvClinics.text=clinicsdata.Name
        }else{
            binding.tvClinics.text=clinicsdata.NameAr
        }
    }

    // region Hospital mode

    private fun loadHospitalAppointments() {
        if (!isOnline(mContext)) {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
            return
        }
        val hospitalId = DateUtils.getHospitalId() ?: return
        // Per doctor: the polyclinic list (PolyInfoID) returns nothing for hospital clinics
        viewModel.getHospitalAppointments(
            hospitalId, clinicId, doctorID,
            today.ifBlank { null }, toDate.ifBlank { null }, appointmentId, patientName
        )
    }

    private fun showHospitalBookingTypePicker() {
        val types = clinicsTypeViewModel.servicesTypesResponse.value?.Data.orEmpty()
        SearchablePickerBottomSheet.show(
            childFragmentManager, getString(R.string.filter_clinic_services),
            getString(R.string.hospital_all_booking_types),
            types.map { PickerItem(it.Id, it.Name) },
            appointmentId.takeIf { it != 0 }
        ) { picked ->
            // 0 = every booking type, as the list uses when opened without a type
            appointmentId = picked.id ?: 0
            binding.services.text =
                if (picked.id == null) getString(R.string.filter_clinic_services) else picked.name
            loadHospitalAppointments()
        }
    }

    private fun loadHospitalClinicsPicker() {
        val hospitalId = DateUtils.getHospitalId() ?: return
        showClinicsPickerOnLoad = true
        hospitalViewModel.getHospitalClinicList(hospitalId)
    }

    private fun loadHospitalDoctorsPicker() {
        val hospitalId = DateUtils.getHospitalId() ?: return
        showDoctorsPickerOnLoad = true
        hospitalViewModel.getDoctors(hospitalId, clinicId)
    }

    private fun initHospitalObservers() {
        val arabic = Localization.getLanguageName() == "ar"

        hospitalViewModel.clinicsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> if (showClinicsPickerOnLoad) loading.showLoadingDialog()
                is Resource.sucess -> {
                    loading.hideLoadingDialog()
                    if (!showClinicsPickerOnLoad) return@observe
                    showClinicsPickerOnLoad = false
                    val items = it.data?.Data.orEmpty().map { clinic ->
                        PickerItem(clinic.ClinicId, (if (arabic) clinic.NameAr else clinic.Name).orEmpty())
                    }
                    SearchablePickerBottomSheet.show(
                        childFragmentManager, getString(R.string.all_clinics),
                        getString(R.string.all_clinics), items, clinicId
                    ) { picked ->
                        if (picked.id != clinicId) {
                            clinicId = picked.id
                            // The chosen doctor may not work in the new clinic.
                            doctorID = null
                            binding.tvListofDoctors.text = getString(R.string.alldoctors)
                        }
                        binding.tvClinics.text = picked.name
                        loadHospitalAppointments()
                    }
                }
                is Resource.Error -> {
                    loading.hideLoadingDialog()
                    showClinicsPickerOnLoad = false
                }
            }
        }

        hospitalViewModel.doctorsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> if (showDoctorsPickerOnLoad) loading.showLoadingDialog()
                is Resource.sucess -> {
                    loading.hideLoadingDialog()
                    if (!showDoctorsPickerOnLoad) return@observe
                    showDoctorsPickerOnLoad = false
                    val items = it.data?.Data.orEmpty().map { doctor ->
                        PickerItem(doctor.DoctorId, (if (arabic) doctor.DoctorNameAr else doctor.DoctorName).orEmpty())
                    }
                    SearchablePickerBottomSheet.show(
                        childFragmentManager, getString(R.string.alldoctors),
                        getString(R.string.alldoctors), items, doctorID
                    ) { picked ->
                        doctorID = picked.id
                        binding.tvListofDoctors.text = picked.name
                        loadHospitalAppointments()
                    }
                }
                is Resource.Error -> {
                    loading.hideLoadingDialog()
                    showDoctorsPickerOnLoad = false
                }
            }
        }
    }

    // endregion

}
