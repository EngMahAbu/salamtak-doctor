//package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule
//
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.activity.addCallback
//import androidx.databinding.DataBindingUtil
//import androidx.navigation.NavController
//import androidx.navigation.Navigation
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.gson.JsonSyntaxException
//import com.salamtak.R
//import com.salamtak.core.utils.SessionManagement
//import com.salamtak.databinding.FragmentClinicScheduleBinding
//import com.salamtak.domain.entities.GeneralResponse
//import com.salamtak.domain.entities.response.MedicalExaminationDurationData
//import com.salamtak.domain.entities.response.ClinicScheduleData
//import com.salamtak.domain.entities.response.ServicesTypesData
//import com.salamtak.domain.entities.resquest.auth.ClinicScheduleModel
//import com.salamtak.salamtakdoctor.noConnectivityDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters.ClinicDurationsAdapter
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters.ClinicScheduleAdapter
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters.NewServicesAdapter
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
//import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
//import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
//import org.koin.android.ext.android.inject
//import org.koin.android.viewmodel.ext.android.sharedViewModel
//import org.koin.core.qualifier.StringQualifier
//import retrofit2.HttpException
//
//
//class ClinicScheduleFragment : com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment<SetupClinicViewModel>(), DialogsListener,
//    RecyclerViewOnClickListener {
//    // TODO: Rename and change types of parameters
//
//    private lateinit var binding: FragmentClinicScheduleBinding
//    lateinit var navController: NavController
//    lateinit var mContext: Context
//    private val sessionManagement: SessionManagement by inject()
//    private lateinit var clinicScheduleAdapter: ClinicScheduleAdapter
//    private lateinit var clinicDurationsAdapter: ClinicDurationsAdapter
//
//    //    private lateinit var periodScheduleAdapter: SchedulePeriodsAdapter
//    private val TAG = ClinicScheduleFragment::class.java.simpleName
//    private lateinit var noInternetDialog: NoInternetDialog
//    private lateinit var loadingDialog: LoadingDialog
//    private var durationsList = ArrayList<MedicalExaminationDurationData>()
//    private var isCreated = false
//    var editClinicScedule: String = ""
//
//    companion object{
//        var latestClinicPosition = 0
//    }
//
//    override val screenViewModel: SetupClinicViewModel
//            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_Clinic_VIEW_MODEL))
//
//    private lateinit var clinicTypeAdapter: NewServicesAdapter
//
//    val serviceViewModel: SetupServicesViewModel
//            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_SERVICE_VIEW_MODEL))
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        arguments?.let {
//            editClinicScedule = it.getString("edit", "")
//        }
//
//        binding =
//            DataBindingUtil.inflate(inflater, R.layout.fragment_clinic_schedule, container, false)
//        clinicTypeAdapter = NewServicesAdapter(this)
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        navController = Navigation.findNavController(binding.root)
//        mContext = binding.root.context
//
//        noInternetDialog = NoInternetDialog(mContext, this)
//        loadingDialog = LoadingDialog(mContext)
//
//        setUi()
//        subscribeToObserver()
//
//
//        if (editClinicScedule.equals("edit")) {
//            editClinic()
//
//        }
//
//
//            requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }
//
//
//
//    }
//
//    private fun editClinic() {
//
////        binding.clinicToolbar.root.visibility = View.GONE
//        binding.clinicToolbar.root.visibility = View.VISIBLE
//        binding.clinicToolbar.circularBackground.visibility = View.INVISIBLE
//        binding.tvLogout.visibility = View.GONE
//        binding.profileBottomToolbar.submitBtn.setText(R.string.update)
//        binding.profileBottomToolbar.saveBtn.setText(R.string.ignore)
//        binding.toolbar.toolbarHeader.text = getString(R.string.edit_clinic)
////        binding.editClinicToolbar.root.visibility = View.VISIBLE
//        binding.stepsRadio.visibility = View.VISIBLE
//
//        binding.infoRb.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("edit", "edit")
//            bundle.putInt(CLINIC_ID, sessionManagement.getClinicId()!!.toInt())
//            findNavController().navigate(R.id.addNewClinicFragment, bundle)
//        }
//
//        binding.galleryRb.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("edit", "edit")
//            findNavController().navigate(R.id.createClinicGalleryFragment, bundle)
//        }
//
//        binding.vactionRb.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("edit", "edit")
//            findNavController().navigate(R.id.vacationClinicFragment, bundle)
//        }
//
//    }
//
//    private fun setUi() {
//
//        binding.clinicToolbar.ivBackBg.setOnClickListener {
//            if (editClinicScedule.equals("edit")) {
//                findNavController().navigate(R.id.clinicsFragment)
//            } else {
//                findNavController().popBackStack()
//            }
//        }
//
//        binding.toolbar.toolbarHeader.text = getString(R.string.new_clinic)
//
//        binding.clinicToolbar.oneTv.text = "2"
//        binding.clinicToolbar.circularDeterminativePb.progress = 66
//        binding.clinicToolbar.clinicHeaderTv.text = getString(R.string.working_times)
//        binding.clinicToolbar.profileNextTv.text = getString(R.string.next_gallery)
//
//
//
//        clinicDurationsAdapter = ClinicDurationsAdapter(this)
//        clinicScheduleAdapter = ClinicScheduleAdapter(
//            this,
//            mContext,
//            screenViewModel,
//            childFragmentManager,
//            clinicDurationsAdapter,
//            viewLifecycleOwner,
//            sessionManagement.getClinicId()!!,
//            loadingDialog,
//            clinicTypeAdapter
//        )
//
//
//        // new
//        binding.servicesRv.layoutManager = LinearLayoutManager(
//            requireContext(),
//            LinearLayoutManager.HORIZONTAL, false
//        )
//        binding.servicesRv.adapter = clinicTypeAdapter
//
////        periodScheduleAdapter = SchedulePeriodsAdapter(this,mContext,childFragmentManager,screenViewModel
////            ,clinicDurationsAdapter,this,sessionManagement.getClinicId()!!,loadingDialog)
//
//        binding.scheduleRv.layoutManager = LinearLayoutManager(mContext)
//        binding.scheduleRv.adapter = clinicScheduleAdapter
//
//        binding.tvLogout.setOnClickListener {
//
//            sessionManagement.clearUser()
//            navController.navigate(R.id.action_logout_Schedule)
//
//        }
//
//        binding.profileBottomToolbar.submitBtn.setOnClickListener {
////            if (isCreated)
////            {
////                navController.navigate(R.id.createClinicGalleryFragment)
////            }else{
////                toast(mContext,getString(R.string.schedule_time_required))
////            }
//
//            if (editClinicScedule.equals("edit")) {
//                val sucess = UpdateDialog(findNavController(), "")
//                sucess.show(childFragmentManager, "0")
//            } else {
//                navController.navigate(R.id.createClinicGalleryFragment)
//            }
//
//        }
//
//
//    }
//
//    override fun onScheduleSubmit(
//        dayId: Int,
//        durationId: Int,
//        workingFrom: String,
//        workingTo: String,
//        fees: Double,
//        position: Int,
//        serviceId: Int,
//        MaxNoOfPatients: Int,
//        followUpFees: Double,
//        switchInactive: Boolean,
//        overlapping: Boolean
//    ) {
//        loadingDialog.show()
//
//        //TODO hit this api on the dialog ok
//        latestClinicPosition = position
//
//        // todo 29/11/22
//        if (isOnline(mContext)) {
//            screenViewModel.createClinicSchedule(
//                ClinicScheduleModel(
//                    dayId,
//                    workingFrom,
//                    workingTo,
//                    fees,
//                    durationId,
//                    sessionManagement.getClinicId()?.toInt()!!,
//                    serviceId, followUpFees, switchInactive, overlapping
//                )
//            )
//        } else {
//            noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//        }
//
//    }
//
//    private fun subscribeToObserver() {
//
//        //TODO 29/11/22
//        if (isOnline(mContext)) {
//            loadingDialog.show()
//
//            getClinicSchedule()
//            getClinicType()
//            screenViewModel.getClinicVisitDuration(sessionManagement.getClinicId()!!)
//        } else {
//            noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//        }
//
//
//        screenViewModel.clinicDurationScheduleResponse.observe(viewLifecycleOwner) {
//
//            if (it.Success) {
//
//                loadingDialog.dismiss()
//                durationsList = it.Data as ArrayList<MedicalExaminationDurationData>
//                clinicDurationsAdapter.submitData(durationsList)
//
//            }
//        }
//
//
//
//        screenViewModel.createScheduleResponse.observe(viewLifecycleOwner) {
//
//            if (it.Success) {
//
//                loadingDialog.dismiss()
//                getClinicSchedule()
//                isCreated = true
//                Log.e(TAG, "subscribeToObserver:Schedule ${it.Data.toString()}")
//            } else {
//                if (it.MessageCode == 401) {
//                    //TODO your message here
//                }
//                loadingDialog.dismiss()
//                Log.e(TAG, "subscribeToObserver:Schedule error ${it.Message} ")
//            }
//
//        }
//
//        screenViewModel.failure.observe(viewLifecycleOwner) {
//
//            loadingDialog.dismiss()
//            when (it) {
//                is GeneralResponse.Failure.JSonServerError -> {
//                    when (it.errorCode) {
//                        401 -> {
//                            showError(
//                                getString(R.string.error_unauthorized)
//                            )
//                        }
//
//                        400 -> {
//
//                            showError(
//                                it.errorMessage!!
//                            )
//
//                        }
//
//                        else -> {
//                            Log.e("TAG BaseFragmentss", "onCreate: ${it.errorMessage}  ")
//                        }
//                    }
//                    it.errorCode = 200
//                }
//                is GeneralResponse.Failure.Error -> {
//                    when (it.ex) {
//                        is HttpException -> if (canShowNoNetworkDialog) {
//                            Log.e("Tag", "${it.ex.localizedMessage} ")
//                        }
//                        is JsonSyntaxException -> {
//                            Log.e("TAGJson BaseFragment123", "onCreate: ${it.ex.printStackTrace()}")
//                        }
//                        else -> {
//                            Log.e(
//                                "TAG BaseFragment",
//                                "onCreate: ${it.ex.cause?.message}  == ${it.ex.message}"
//                            )
//                        }
//                    }
//                }
//            }
//
//        }
//
//
//    }
//
//    private fun getClinicSchedule() {
//        //TODO 29/11/22
//        if (isOnline(mContext)) {
//            screenViewModel.getClinicSchedule(sessionManagement.getClinicId()!!)
//        } else {
//            noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//        }
//
//
//
//        screenViewModel.clinicScheduleResponse.observe(viewLifecycleOwner) {
//
//            if (it.Success) {
//                loadingDialog.dismiss()
//                clinicScheduleAdapter.submitData(it.Data as ArrayList<ClinicScheduleData>)
//
//            }
//
//        }
//
//    }
//
//    private fun getClinicType() {
//        //TODO 29/11/22
//        if (isOnline(mContext)) {
//            serviceViewModel.getServicesTypes()
//        } else {
//            noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//        }
//
//        serviceViewModel.servicesTypesResponse.observe(viewLifecycleOwner) {
//            clinicTypeAdapter.submitData(it.Data as ArrayList<ServicesTypesData>)
//        }
//    }
//
//    override fun onClinicTypeSelect(clinicId: Int, clinicNameTxt: String, position: Int) {
//        getShudalClinicByService(clinicId.toString())
//    }
//
//    private fun getShudalClinicByService(selectedServiceId: String) {
//
//        // should visible and remove "" clinic id
//        //TODO 29/11/22
//        if (isOnline(mContext)) {
//            serviceViewModel.getServiceSchedule(selectedServiceId, "")
//        } else {
//            noConnectivityDialog.noInternetDialogFragment(requireContext())
//
//        }
//
//
//        serviceViewModel.serviceScheduleResponse.observe(viewLifecycleOwner) {
//            if (it.Success) {
//                loadingDialog.dismiss()
//                clinicScheduleAdapter.submitData(it.Data as ArrayList<ClinicScheduleData>)
//            }
//        }
//
//    }
//
//    //TODO
//    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
//        noInternetDialog.dismiss()
//    }
//
//
//
//
//}