package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentClinicsBinding
import com.salamtak.domain.entities.response.ClinicData
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.clinics.adapters.ClinicsAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.CLINIC_ID
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier


class ClinicsFragment : com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment<SetupClinicViewModel>(R.layout.fragment_clinics), ClinicsAdapter.OnClinicClickListener,
    ClinicsAdapter.OnCallClickListener, DialogsListener,ClinicsAdapter.AddDoctors {

    private lateinit var binding: FragmentClinicsBinding
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var clinicsAdapter: ClinicsAdapter
    override val screenViewModel: SetupClinicViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_Clinic_VIEW_MODEL))

    var REQUEST_PHONE_CALL = 1
    private val sessionManagement: SessionManagement by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clinics, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (DateUtils.getAdminOwnerId().isNullOrEmpty()){
//            binding.emptyPageLayout.emptyGoToBtn.visibility=View.VISIBLE
//        }else{
//            binding.emptyPageLayout.emptyGoToBtn.visibility=View.GONE
//        }

        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        clinicsAdapter = ClinicsAdapter(this, this,this)
        setUi()
        subscribeToObservers()
        requireActivity().onBackPressedDispatcher.addCallback {  findNavController().navigate(R.id.homeFragment) }

    }

    private fun setUi() {
       if (!DateUtils.getAdminOwnerId().isNullOrEmpty()){
           binding.addBtn.visibility=View.GONE
       }
        binding.toolbar.ivBack.setOnClickListener {
            navController.popBackStack()
        }
        binding.toolbar.toolbarHeader.text = getString(R.string.clinics)
        binding.clinicsRv.layoutManager = LinearLayoutManager(mContext)
        binding.clinicsRv.adapter = clinicsAdapter
        binding.toolbar.ivBackBg.visibility = View.GONE
        binding.toolbar.ivBack.visibility = View.GONE

        binding.addBtn.setOnClickListener {
            sessionManagement.setClinicId("0")
            findNavController().navigate(R.id.addNewClinicFragment)
        }

    }

    private fun subscribeToObservers() {
        //TODO
        if (isOnline(mContext)) {
            screenViewModel.getClinics()
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }


        screenViewModel.clinicsResponse.observe(viewLifecycleOwner) {

            if (it.Data.isNullOrEmpty()) {
                binding.emptyPageLayout.emptyPage.visibility = View.VISIBLE
                binding.addBtn.visibility = View.GONE
                binding.emptyPageLayout.emptyGoToBtn.setOnClickListener {
                    sessionManagement.setClinicId("0")
                    findNavController().navigate(R.id.addNewClinicFragment)
                }

            } else
                binding.emptyPageLayout.emptyPage.visibility = View.GONE

            clinicsAdapter.submitData(it.Data as ArrayList<ClinicData>)
        }

    }

    override fun onClinicClicked(clinicData: ClinicData) {
        val bundle = Bundle()
        clinicData.ClinicId?.let { bundle.putInt(CLINIC_ID, it) }
        val name = clinicData.Name
        //new
        sessionManagement.setClinicId(clinicData.ClinicId.toString())
        bundle.putString("edit", "edit")
        bundle.putString("name",name)

        findNavController().navigate(R.id.addNewClinicFragment, bundle)
    }

    override fun onCallClicked(clinicData: ClinicData) {
        clinicData.Phone?.let { checkPermisiion(it) }
    }

    fun checkPermisiion(phone: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL
        )
        else {
            makeCall(phone)
        }

    }

    private fun makeCall(phone: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:$phone"))
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
            return
        }
        startActivity(intent)
    }
    //todo
    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }
    override fun getClincId(clincId: Int) {
        val action=ClinicsFragmentDirections.actionClinicsFragmentToAppointmentFragment(clincId.toString())
        findNavController().navigate(action)
    }

    override fun adddoctors(clinicId: Int) {
        val action=ClinicsFragmentDirections.actionClinicsFragmentToSetupProfileFragment(clinicId,"")
        findNavController().navigate(action)
    }

    override fun viewDoctors(clinicId: Int) {
        val action=ClinicsFragmentDirections.actionClinicsFragmentToPolyClinicsDoctorsFragment(clinicId)
        findNavController().navigate(action)
    }

}