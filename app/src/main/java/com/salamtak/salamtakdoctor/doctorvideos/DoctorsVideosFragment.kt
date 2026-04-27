package com.salamtak.salamtakdoctor.doctorvideos

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salamtak.R
import com.salamtak.databinding.DoctorsVideosfragmentBinding
import com.salamtak.domain.entities.response.HomeAdsData
import com.salamtak.domain.entities.resquest.auth.DoctorVideoRequest
import com.salamtak.salamtakdoctor.adapter.ImageVideoAdapter
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.PolyClinicsViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.UpdateDialog
import java.util.regex.Pattern


class DoctorsVideosFragment : Fragment(),ImageVideoAdapter.IDelete {

private lateinit var binding:DoctorsVideosfragmentBinding
    private lateinit var bottomsheetbeahavoir: BottomSheetBehavior<ConstraintLayout>
    private lateinit var polyViewModel: PolyClinicsViewModel
    private lateinit var imageVideoAdapter: ImageVideoAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DoctorsVideosfragmentBinding.inflate(layoutInflater,container,false)
        polyViewModel = ViewModelProvider(this).get(PolyClinicsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomsheetbeahavoir =
            BottomSheetBehavior.from(binding.layoutBottomsheetpersistant.addvideos)
        bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        capsVideoRecylerview()
        initButton()
        callGetVideos()
        calldeleteVideos()
    }

    private fun initButton(){
        binding.ivArrow.setOnClickListener {
            findNavController().navigate(R.id.moreFragment)
        }
        binding.btnAddvideo.setOnClickListener {
            bottomsheetbeahavoir.state =
                if (bottomsheetbeahavoir.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_EXPANDED
        }
        binding.layoutBottomsheetpersistant.btnAddCard.setOnClickListener {
            bottomsheetbeahavoir.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.layoutBottomsheetpersistant.ivAdd.setOnClickListener {
            if (isValidURL(binding.layoutBottomsheetpersistant.etVideoLink.text.toString())){
                callCreateVideos()
            }else{
                Toast.makeText(requireContext(),"invalid Link", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun capsVideoRecylerview() {
        imageVideoAdapter = ImageVideoAdapter(this)
        binding.viewPager.apply {
            adapter = imageVideoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun callGetVideos() {
        polyViewModel.getDoctorVideoReponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                 binding.progressBar.visibility=View.VISIBLE
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                    val listBlogs= mutableListOf<HomeAdsData>()
                    for (i in it.data?.data?.indices!!){
                       val homeAdsData= it.data.data!![i]?.videoURL?.let { it1 ->
                           it.data.data!![i]?.id?.let { it2 ->
                               HomeAdsData(true,
                                   it1, it2
                               )
                           }
                       }
                        if (homeAdsData != null) {
                            listBlogs.add(homeAdsData)
                        }
                    }
                    imageVideoAdapter.submitList(listBlogs)
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

            }
        })
        DateUtils.getDoctorId()?.toInt()?.let { polyViewModel.getDoctorVideos(it) }
    }

    private fun callCreateVideos() {
        polyViewModel.createDoctorVideoReponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {

                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {

                    val sucess = UpdateDialog(findNavController(), "")
                    sucess.show(childFragmentManager, "0")
                        Handler(Looper.getMainLooper()).postDelayed(Runnable{
                            sucess.dismiss()
                        },2000)
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {

                }

            }
        })
        DateUtils.getDoctorId()?.toInt()
            ?.let { DoctorVideoRequest(it, binding.layoutBottomsheetpersistant.etVideoLink.text.toString()) }
            ?.let { polyViewModel.createDoctorVideo(it) }
    }

    private fun calldeleteVideos() {
        polyViewModel.deleteDoctorVideoReponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.sucess -> {
                    binding.progressBar.visibility=View.GONE
                   imageVideoAdapter.notifyDataSetChanged()
                    callGetVideos()
                }

                is com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource.Error -> {
                    binding.progressBar.visibility=View.GONE

                }

            }
        })
    }

    override fun deleteVideo(videoId: Int) {
        polyViewModel.deleteDoctorVideo(videoId)
    }

    fun isValidURL(url: String?): Boolean{
        // Regex to check valid URL
        val regex = ("((http|https)://)(www.)?"
                + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%"
                + "._\\+~#?&//=]*)")

        // Compile the ReGex
        val p = Pattern.compile(regex)

        // If the string is empty
        // return false
        if (url == null) {
            return false
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        val m = p.matcher(url)

        // Return if the string
        // matched the ReGex
        return m.matches()
    }

}