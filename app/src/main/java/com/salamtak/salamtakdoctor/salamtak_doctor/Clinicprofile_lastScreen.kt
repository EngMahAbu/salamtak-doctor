package com.salamtak.salamtakdoctor.salamtak_doctor

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.HomeViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.hideLoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.showLoadingDialog
import org.koin.android.ext.android.inject


class Clinicprofile_lastScreen : Fragment() {

    private val sessionManagement: SessionManagement by inject()
    private lateinit var viewModel: HomeViewModel
    lateinit var loading: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clinicprofile_last_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        loading = Dialog(requireContext())

        val buttonClinic = view.findViewById<MaterialButton>(R.id.btn_clinic)
        val doctorNameTv = view.findViewById<TextView>(R.id.docotoname)

        sessionManagement.setLoggedIn()
        val nameDr = sessionManagement.getString()
        if (!nameDr.isNullOrEmpty()) {
           //val text2 ="${getString(R.string.thanks)} <font color=#00D2CB>${getString(R.string.dr)} $nameDr</font> ${getString(R.string.completeing)}"
           val text2 ="${getString(R.string.dr)} $nameDr</font> ${getString(R.string.completeing)}"
          //  val text2 ="${getString(R.string.dr)}${nameDr}"
            doctorNameTv.visibility=View.VISIBLE
            doctorNameTv.text = Html.fromHtml(text2)
        }else{
            initObserver(doctorNameTv)
        }

        buttonClinic.setOnClickListener {
            sessionManagement.setClinicId("0")
            findNavController().navigate(R.id.action_thankYouFragment_to_addNewClinic)

        }
    }

    private fun initObserver(doctorNameTv: TextView) {
        if (isOnline(requireContext())){
            viewModel.getDoctorProfile()
        }
        else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }
        viewModel.doctorProfileModel.observe(viewLifecycleOwner, Observer {
            doctorNameTv.visibility=View.VISIBLE
            val text2 = "${getString(R.string.dr)} ${it.Data.FirstName}"
            doctorNameTv.text = Html.fromHtml(text2)
        })
        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                loading.showLoadingDialog()
            else
                loading.hideLoadingDialog()

        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (loading.isShowing) {
            loading.dismiss()
        }
    }
}