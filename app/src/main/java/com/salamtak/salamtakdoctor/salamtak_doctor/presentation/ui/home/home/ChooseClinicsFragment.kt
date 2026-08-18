package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.ChooseClinicsfragmentBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.salamtakdoctor.adapter.ChooseClinicsAdapter
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Resource
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.hideLoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showLoadingDialog
import org.koin.android.ext.android.inject
import java.util.Calendar

import com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile.DoctorProfileViewModel

class ChooseClinicsFragment : Fragment(),ChooseClinicsAdapter.ISelectClinics {
    private lateinit var binding:ChooseClinicsfragmentBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var polyViewModel: PolyClinicsViewModel
    private lateinit var profileViewModel: DoctorProfileViewModel
    private lateinit var chooseClinicsAdapter:ChooseClinicsAdapter
    private val sessionManagement: SessionManagement by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=ChooseClinicsfragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        profileViewModel = ViewModelProvider(this)[DoctorProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DateUtils.setClinicName(getString(R.string.all_clinics))
        binding.tvGreeting.text = setGreeting()
        ImageUtil.loadPicture(binding.profileDoctorImage, UserData.getImage())
        if (Localization.getLanguageName() == "ar") {
            UserData.getUserNameAR()?.let {
                binding.descNameTv.text = it.trim().split(" ").firstOrNull() ?: it
            }
        } else {
            UserData.getUserName()?.let {
                binding.descNameTv.text = it.trim().split(" ").firstOrNull() ?: it
            }
        }

        profileViewModel.doctorProfileModel.observe(viewLifecycleOwner) { response ->
            response?.Data?.let { doctorData ->
                doctorData.Image?.let { imgUrl ->
                    UserData.setImage(imgUrl)
                    ImageUtil.loadPicture(binding.profileDoctorImage, imgUrl)
                }
                val doctorFirstName = if (Localization.getLanguageName() == "ar") {
                    doctorData.FirstNameAr?.trim()?.split(" ")?.firstOrNull()?.takeIf { it.isNotBlank() }
                        ?: UserData.getUserNameAR()?.trim()?.split(" ")?.firstOrNull() ?: ""
                } else {
                    doctorData.FirstName?.trim()?.split(" ")?.firstOrNull()?.takeIf { it.isNotBlank() }
                        ?: UserData.getUserName()?.trim()?.split(" ")?.firstOrNull() ?: ""
                }
                if (doctorFirstName.isNotBlank()) {
                    binding.descNameTv.text = doctorFirstName
                }
            }
        }

        callCheckUser()
        doctorsRecylerview()
        initObserve()
    }

    override fun onResume() {
        super.onResume()
        val image = UserData.getImage()
        ImageUtil.loadPicture(binding.profileDoctorImage, image)
        if (::profileViewModel.isInitialized) {
            profileViewModel.getDoctorProfile()
        }
    }

    private fun doctorsRecylerview() {
        chooseClinicsAdapter = ChooseClinicsAdapter(this)
        binding.rvClinks.apply {
            adapter = chooseClinicsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    private fun initObserve() {

            viewModel.clinicModel.observe(viewLifecycleOwner, Observer {

               if (it.Data.size<=1){
                   findNavController().navigate(R.id.homeFragment)
               }else{
                   val list= mutableListOf<ClinicData>()
                   val clinicData=ClinicData("",0,"","All Clinics","جميع العيادات",null," ")
                   list.add(clinicData)
                   it.Data.forEach {
                       list.add(it)
                   }
                   chooseClinicsAdapter.submitList(list)
                   Log.d(TAG, "initObserve: ${it.Data}")
               }

            })

            viewModel.error.observe(viewLifecycleOwner, Observer {
//            errorMessage(it.Message)
                if (it.Message != null) {
                    errorMessage(it.Message!!)
                    if (it.code == RequestCode.UNAUTHORIZED) {
                        //send to login scren
                        sessionManagement.clearUser()
                        UserData.logout()
                        findNavController().navigate(R.id.loginFragment)
                    }
                } else {
                    if (it.code == RequestCode.UNAUTHORIZED) {
                        //send to login scren
                        sessionManagement.clearUser()
                        UserData.logout()
                        findNavController().navigate(R.id.loginFragment)
                    }
                }
            })
        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.progressBar.visibility=View.VISIBLE
            else
                binding.progressBar.visibility=View.GONE
        })

            //todo 29/11/22

                viewModel.getHomeClinics()

    }

    override fun onClinicsSelected(listOfDoctors: ClinicData) {
       DateUtils.setClinicId(listOfDoctors.ClinicId.toString())
        if (DefaultLocaleHelper.getInstance(requireContext()).getCurrentLocale()=="ar"){
            DateUtils.setClinicName(listOfDoctors.NameAr.toString())
        }else{
            DateUtils.setClinicName(listOfDoctors.Name.toString())
        }
        findNavController().navigate(R.id.homeFragment)
    }

    fun setGreeting(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]

        var greeting = ""

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = getString(R.string.good_morning)
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = getString(R.string.good_afternoon)
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting = getString(R.string.good_evening)
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            greeting = getString(R.string.good_night)
        }
        return greeting
    }

    private fun callCheckUser() {

        polyViewModel.checkUserResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {


                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    findNavController().navigate(R.id.loginFragment)
                }

            }
        })

        polyViewModel.checkUser()

    }

}