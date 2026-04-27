package com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.salamtak.R
import com.salamtak.databinding.FragmentDoctorProfileBinding
import com.salamtak.domain.entities.response.CountriesData
import com.salamtak.domain.entities.response.SeniorityLevelsData
import com.salamtak.domain.entities.response.SubSpecialitiesData
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.CountriesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SeniorityLevelsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SpecialitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.adapters.SubSpecialitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.NationalitiesBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SeniorityLevelsBottomSheetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.SpecialitiesBottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DoctorProfileFragment : BaseFragment<SetupProfileViewModel>(R.layout.fragment_doctor_profile),
    View.OnClickListener, RecyclerViewOnClickListener,
    DialogsListener {

    lateinit var binding: FragmentDoctorProfileBinding
    private lateinit var viewModel: DoctorProfileViewModel
    lateinit var loading: Dialog
    private lateinit var noInternetDialog: NoInternetDialog
    var gender = ""
    private lateinit var mContext: Context
    private lateinit var specialitiesAdapter: SpecialitiesAdapter
    private lateinit var subSpecialitiesAdapter: SubSpecialitiesAdapter
    private lateinit var seniorityLevelsAdapter: SeniorityLevelsAdapter
    private lateinit var countriesAdapter: CountriesAdapter
    var countriesList = ArrayList<CountriesData>()
    var specialityList = ArrayList<SubSpecialitiesData>()
    var subSpecialityList = ArrayList<SubSpecialitiesData>()
    var seniorityList = ArrayList<SeniorityLevelsData>()

    override val screenViewModel: SetupProfileViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_VIEW_MODEL))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DoctorProfileViewModel::class.java]
        loading = Dialog(requireContext())
        binding.toolbar.toolbarHeader.text = getString(R.string.update_profile)

        mContext = binding.root.context
        specialitiesAdapter = SpecialitiesAdapter(false, this, mContext)
        subSpecialitiesAdapter = SubSpecialitiesAdapter(true, this, mContext)
        countriesAdapter = CountriesAdapter(false, this, mContext)
        seniorityLevelsAdapter = SeniorityLevelsAdapter(false, this, mContext)

        initListeners()
        initObservers()
        getSpinners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }

    }

    private fun getSpinners() {
        //TODO
        if (isOnline(mContext)) {
            if (binding.nameLayout.firstNameEt.toString().isEmpty()){
                binding.nameLayout.firstNameEt.error = "Please Enter first name"
            }
            if (binding.nameLayout.middleNameEt.toString().isEmpty()){
                binding.nameLayout.middleNameEt.error = "Please Enter middle Name"
            }
            if (binding.nameLayout.lastNameEt.toString().isEmpty()){
                binding.nameLayout.lastNameEt.error = "Please Enter last name"
            }
            if (binding.nameLayout.firstNameArEt.toString().isEmpty()){
                binding.nameLayout.firstNameArEt.error = "Please Enter first name in Arabic"
            }
            if (binding.nameLayout.middleNameArEt.toString().isEmpty()){
                binding.nameLayout.middleNameArEt.error = "Please Enter middle name in Arabic"
            }
            if (binding.nameLayout.lastNameArEt.toString().isEmpty()){
                binding.nameLayout.lastNameArEt.error = "Please Enter last name in Arabic"
            }
            if (binding.nationalitySelect.toString().isEmpty()){
                binding.nationalitySelect.error = "Please Select Nationality"
            }
            if (binding.birthLayout.toString().isEmpty()){
                binding.birthLayout.error = "Please Select Date of birth"
            }
            if (binding.specializationSelect.toString().isEmpty()){
                binding.specializationSelect.error = "Please Select Speciality"
            }
            if (binding.subSpecializationSelect.toString().isEmpty()){
                binding.subSpecializationSelect.error = "Please Select Speciality"
            }
            if (binding.senioritySelect.toString().isEmpty()){
                binding.senioritySelect.error = "Please Select Seniority"
            }
            screenViewModel.getSpecialities()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }



        screenViewModel.specialties.observe(viewLifecycleOwner) {

            specialityList.clear()
            it.Data.forEach { item ->
                specialityList.add(item)
            }
            specialitiesAdapter.submitData(specialityList)
        }
        //TODO 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getSeniorityLevels()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }
        screenViewModel.seniorityLevels.observe(viewLifecycleOwner) {

            seniorityList.clear()
            it.Data.forEach { item ->
                seniorityList.add(item)
            }
            seniorityLevelsAdapter.submitData(seniorityList)
        }
        //TODO 29/11/22
        if (isOnline(mContext)) {
            screenViewModel.getCountries()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }
        screenViewModel.countriesResponse.observe(viewLifecycleOwner) {
            countriesList.clear()
            it.Data.forEach { item ->
                countriesList.add(item)
            }
            countriesAdapter.submitData(countriesList)
        }

        binding.nationalitySelect.setOnClickListener {
            val d = NationalitiesBottomSheetDialog(countriesAdapter, countriesList)
            d.show(childFragmentManager, "")
        }

        binding.specializationSelect.setOnClickListener {
            val d = SpecialitiesBottomSheetDialog(specialitiesAdapter, specialityList)
            d.show(childFragmentManager, "")
        }

        binding.senioritySelect.setOnClickListener {
            val d = SeniorityLevelsBottomSheetDialog(seniorityLevelsAdapter, seniorityList)
            d.show(childFragmentManager, "")
        }

    }

    //TODO
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }


    private fun initListeners() {
        binding.profileBottomToolbar.submitBtn.setOnClickListener(this)
    }

    private fun initObservers() {

        if (isOnline(mContext)) {
            viewModel.getDoctorProfile()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())

        }
1

        viewModel.doctorProfileModel.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.doctorProfile = it

                if (it.Data.GenderId == 1) {
                    binding.genderLayout.maleBtn.setTextColor(resources.getColor(R.color.head_color))
                    binding.genderLayout.femaleBtn.setTextColor(resources.getColor(R.color.black))
                    binding.genderLayout.maleBtn.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_btn_border_gray_transparent_selected))
                    binding.genderLayout.femaleBtn.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_btn_border_gray_transprent))
                } else {
                    binding.genderLayout.femaleBtn.setTextColor(resources.getColor(R.color.head_color))
                    binding.genderLayout.maleBtn.setTextColor(resources.getColor(R.color.black))
                    binding.genderLayout.femaleBtn.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_btn_border_gray_transparent_selected))
                    binding.genderLayout.maleBtn.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_btn_border_gray_transprent))
                }

                binding.nameLayout.firstNameEt.setText(it.Data.FirstName)
                binding.nameLayout.middleNameEt.setText(it.Data.MiddelName)
                binding.nameLayout.lastNameEt.setText(it.Data.LastName)

                binding.nameLayout.firstNameArEt.setText(it.Data.FirstNameAr)
                binding.nameLayout.middleNameArEt.setText(it.Data.MiddelNameAr)
                binding.nameLayout.lastNameArEt.setText(it.Data.LastNameAr)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    binding.birthEt.setText(ConvertDate(it.Data.Birthday))
                    binding.birthEt.setText(ConvertDate(it.Data.Birthday))
                }

            }
        })

        viewModel.showLoader.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()

        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_bottom_toolbar -> {

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ConvertDate(startTime: String): String {
        val inputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val outputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH)
        val date: LocalDate = LocalDate.parse(startTime, inputFormatter)
        val formattedDate: String = outputFormatter.format(date)

        return formattedDate
    }
    override fun onImageSelected(file: File) {
        Glide.with(requireContext())
            .load(file)
            .into(binding.uploadPhotoBg)
    }
}