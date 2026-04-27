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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salamtak.R
import com.salamtak.databinding.DialogBottomSheetfragmentBinding
import com.salamtak.domain.entities.response.AreaData
import com.salamtak.domain.entities.response.CitysData
import com.salamtak.salamtakdoctor.adapter.GetAllAreaAdapter
import com.salamtak.salamtakdoctor.adapter.GetAllCitiesAdapter
import com.salamtak.salamtakdoctor.model.ChooseSession
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.InsuranceViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SalamtakViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SharedDataViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.errorMessage


class DialogBottomSheetFragment : BottomSheetDialogFragment(),GetAllCitiesAdapter.SelectCity,GetAllAreaAdapter.SelectArea {

    private lateinit var binding:DialogBottomSheetfragmentBinding
    private lateinit var viewmodel:SalamtakViewModel
    private val sharedDataViewmodel: SharedDataViewModel by activityViewModels()
    private val args:DialogBottomSheetFragmentArgs by navArgs()
    private lateinit var getAllCitiesAdapter: GetAllCitiesAdapter
    private lateinit var getAllAreaAdapter: GetAllAreaAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DialogBottomSheetfragmentBinding.inflate(layoutInflater,container,false)
        viewmodel = ViewModelProvider(this).get(SalamtakViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.status?.let { changeViews(it) }
        setupresyclerviewGetAllCities()
        setupresyclerviewGetAllArea()
        chooseSession()
        initButton()
    }

    private fun initButton(){
        binding.btnDone.setOnClickListener {
            dismiss()
        }
        binding.itemSession.btnDone.setOnClickListener {
            dismiss()
        }
    }

    fun setupresyclerviewGetAllCities() {
        getAllCitiesAdapter = GetAllCitiesAdapter(this)
        binding.itemAllcities.rvAllCities.apply {
            adapter = getAllCitiesAdapter
            layoutManager = LinearLayoutManager(requireContext())
           // addItemDecoration(object : DividerItemDecoration(activity, LinearLayout.VERTICAL) {})
        }
    }

    fun setupresyclerviewGetAllArea() {
        getAllAreaAdapter = GetAllAreaAdapter(this)
        binding.itemAllArea.rvAllArea.apply {
            adapter = getAllAreaAdapter
            layoutManager = LinearLayoutManager(requireContext())
          //  addItemDecoration(object : DividerItemDecoration(activity, LinearLayout.VERTICAL) {})
        }
    }

    private fun changeViews(selectviews: String) {

        when (selectviews) {

            "AllCity" -> {
                binding.layoutAllCities.visibility = View.VISIBLE
                binding.tvAllSpecial.text = getString(R.string.choose_city)
                callBack()
            }

            "AllArea" -> {
                binding.layoutAllArea.visibility = View.VISIBLE
                binding.tvAllSpecial.text = getString(R.string.choose_area)
                callBackAreas()
            }
            "session" -> {
                binding.layoutSession.visibility = View.VISIBLE
                binding.btnDone.visibility = View.GONE
            }

        }

    }

    private fun chooseSession() {

        binding.itemSession.radio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_male -> {
                    val chooseGender = ChooseSession(getString(R.string.session), 1)
                    sharedDataViewmodel.getSession(chooseGender)
                }

            }
        }

    }

    private fun callBack() {

        viewmodel.citiesResponse.observe(viewLifecycleOwner, Observer {

            if (it.data != null) {
                getAllCitiesAdapter.submitList(it.data)
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

    private fun callBackAreas() {

        viewmodel.areaResponse.observe(viewLifecycleOwner, Observer {

            if (it.data != null) {
                getAllAreaAdapter.submitList(it.data)
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

        sharedDataViewmodel.getCity.observe(viewLifecycleOwner) {
            viewmodel.getAllAreas(it.id)
        }

    }

    private fun showprogtessbar() {

        binding.itemAllcities.progressBar.visibility = View.VISIBLE
        binding.itemAllArea.progressBar.visibility = View.VISIBLE
    }

    private fun hideprogressbar() {

        binding.itemAllcities.progressBar.visibility = View.GONE
        binding.itemAllArea.progressBar.visibility = View.GONE

    }

    override fun onItemSelected(city: CitysData) {
        sharedDataViewmodel.getCity(city)
    }

    override fun onSelectArea(Area: AreaData) {
        sharedDataViewmodel.getArea(Area)
    }

}