package com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.bottom_sheet.newbottomsheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.salamtak.R
import com.salamtak.databinding.DialogBottomSheetfragmentBinding
import com.salamtak.databinding.DialogDeleteBottomSheetBinding
import com.salamtak.domain.entities.response.AreaData
import com.salamtak.domain.entities.response.CitysData
import com.salamtak.salamtakdoctor.adapter.GetAllAreaAdapter
import com.salamtak.salamtakdoctor.adapter.GetAllCitiesAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SalamtakViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage


class DialogDeleteBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDeleteBottomSheetBinding
    private lateinit var viewmodel: SalamtakViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DialogDeleteBottomSheetBinding.inflate(layoutInflater, container, false)
        viewmodel = ViewModelProvider(this).get(SalamtakViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        callBack()
    }

    private fun initButton() {
        binding.itemDelete.btnNo.setOnClickListener {
            dismiss()
        }
        binding.itemDelete.btnYes.setOnClickListener {
            deleteUserCallBack()
        }
    }


    private fun callBack() {

        viewmodel.citiesResponse.observe(viewLifecycleOwner, Observer {

            if (it.data != null) {

            }

        })



        viewmodel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewmodel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                showprogtessbar()
            else
                hideprogressbar()
        })

        viewmodel.getAllCities()

    }

    private fun deleteUserCallBack() {

        viewmodel.deleteResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                Snackbar.make(
                    requireView(),
                    "our Account deleted Successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.loginFragment)
            }
        })
        viewmodel.error.observe(viewLifecycleOwner, Observer {
            errorMessage(it.Message.toString())
        })

        viewmodel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                showprogtessbar()
            else
                hideprogressbar()
        })
        viewmodel.deleteUser()

    }

    private fun showprogtessbar() {

//        binding.itemAllcities.progressBar.visibility = View.VISIBLE
//        binding.itemAllArea.progressBar.visibility = View.VISIBLE
    }

    private fun hideprogressbar() {

//        binding.itemAllcities.progressBar.visibility = View.GONE
//        binding.itemAllArea.progressBar.visibility = View.GONE

    }


}