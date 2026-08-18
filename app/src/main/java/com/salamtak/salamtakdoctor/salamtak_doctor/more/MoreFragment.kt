package com.salamtak.salamtakdoctor.salamtak_doctor.more

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.salamtak.R
import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.core.utils.SessionManagement.Companion.KEY_LANGUAGE_AR
import com.salamtak.core.utils.SessionManagement.Companion.KEY_LANGUAGE_EN
import com.salamtak.databinding.FragmentMoreBinding
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil
import org.koin.android.ext.android.inject


import androidx.lifecycle.ViewModelProvider
import com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile.DoctorProfileViewModel

class MoreFragment : Fragment(), View.OnClickListener {

    companion object {
        var editClick = ""
        var changePassword = ""
    }

    lateinit var binding: FragmentMoreBinding
    private val sessionManagement: SessionManagement by inject()
    private lateinit var profileViewModel: DoctorProfileViewModel
    var REQUEST_PHONE_CALL = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMoreBinding.inflate(inflater, container, false)

        val image = UserData.getImage()

        ImageUtil.loadPicture(binding.profileDoctorImage, image)

        initListener()

        // new
        sessionManagement.setDoctorId("0")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { findNavController().navigate(R.id.homeFragment) }

        profileViewModel = ViewModelProvider(this)[DoctorProfileViewModel::class.java]
        profileViewModel.doctorProfileModel.observe(viewLifecycleOwner) { response ->
            response?.Data?.let { doctorData ->
                doctorData.Image?.let { imgUrl ->
                    UserData.setImage(imgUrl)
                    ImageUtil.loadPicture(binding.profileDoctorImage, imgUrl)
                }
                val doctorFullName = if (Localization.getLanguageName() == "ar") {
                    val fName = doctorData.FirstNameAr ?: ""
                    val mName = doctorData.MiddelNameAr ?: ""
                    val lName = doctorData.LastNameAr ?: ""
                    "$fName $mName $lName".trim().replace(Regex("\\s+"), " ")
                } else {
                    val fName = doctorData.FirstName ?: ""
                    val mName = doctorData.MiddelName ?: ""
                    val lName = doctorData.LastName ?: ""
                    "$fName $mName $lName".trim().replace(Regex("\\s+"), " ")
                }
                if (doctorFullName.isNotBlank()) {
                    binding.doctorName.text = doctorFullName
                }
            }
        }

        initButton()
    }

    override fun onResume() {
        super.onResume()
        val image = UserData.getImage()
        ImageUtil.loadPicture(binding.profileDoctorImage, image)
        if (::profileViewModel.isInitialized) {
            profileViewModel.getDoctorProfile()
        }
    }

    private fun initButton() {
        binding.aboutPrivacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.privacyPolicyFragment)
        }
        binding.mcDoctorAccont.setOnClickListener {
            findNavController().navigate(R.id.doctorAccountFragment)
        }
        binding.aboutAppTv.setOnClickListener {
            findNavController().navigate(R.id.aboutAppFragment)
        }
        binding.deleteAccount.setOnClickListener {
            findNavController().navigate(R.id.dialogDeleteBottomSheet)
        }
        binding.callUsTv.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }
        binding.mcOffers.setOnClickListener {
            findNavController().navigate(R.id.offersFragment)
        }
        binding.mcVideos.setOnClickListener {
            findNavController().navigate(R.id.doctorsVideosFragment)
        }
        binding.mcPatientQuestions.setOnClickListener {
            findNavController().navigate(R.id.answersFragment)
        }
    }

    private fun initListener() {
        binding.updateProfileTv.setOnClickListener(this)
        binding.signOutTv.setOnClickListener(this)
        binding.changePasswordTv.setOnClickListener(this)
        binding.aboutAppTv.setOnClickListener(this)
        binding.toolbar.toolbarHeader.text = ""
        binding.toolbar.ivBackBg.visibility = View.GONE
        binding.toolbar.ivBack.visibility = View.GONE
        binding.englishsTv.setOnClickListener(this)
        binding.arabicsTv.setOnClickListener(this)
        binding.callUsTv.setOnClickListener(this)
        binding.termsConditionTv.setOnClickListener(this)
        binding.notifictionTv.setOnClickListener(this)
        binding.langTv.setOnClickListener(this)

        if (DateUtils.getAdminOwnerId().isNullOrEmpty()) {
            binding.view2.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("edit", "edit")
                val action =
                    MoreFragmentDirections.actionMoreFragmentToSetupProfileFragment(0, "edit")
                findNavController().navigate(action)

                editClick = "edit"
            }
        } else {
            binding.mcVideos.visibility = View.GONE
            binding.mcPatientQuestions.visibility = View.GONE
            binding.profileDoctorImage.visibility=View.GONE
            binding.view2.setOnClickListener {
                findNavController().navigate(R.id.editEnterpriseInfoFragment)
            }
        }

        if (Localization.getLanguageName() == "ar") {
            UserData.getUserNameAR()?.let {
                binding.doctorName.text = it.trim().split("\\s+".toRegex()).take(3).joinToString(" ")
            }
        } else {
            UserData.getUserName()?.let {
                binding.doctorName.text = it.trim().split("\\s+".toRegex()).take(3).joinToString(" ")
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.update_profile_tv -> {
                val bundle = Bundle()
                bundle.putString("edit", "edit")
                findNavController().navigate(R.id.setupProfileFragment, bundle)
            }

            R.id.sign_out_tv -> {
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(R.layout.cancelappointmentbottomsheet)

                val textView = bottomSheetDialog.findViewById<TextView>(R.id.tv_display)!!
                val yesBtn = bottomSheetDialog.findViewById<MaterialButton>(R.id.yes_btn)!!
                val noBtn = bottomSheetDialog.findViewById<MaterialButton>(R.id.No_button)!!

                textView.text = getString(R.string.are_you_sure_you_wnat_sign_out)

                yesBtn.setOnClickListener {
                    DateUtils.setStatus("")
                    sessionManagement.clearUser()
                    UserData.logout()
                    findNavController().navigate(
                        R.id.loginFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                    )
                    bottomSheetDialog.dismiss()
                }
                noBtn.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()

            }

            R.id.englishs_tv -> {
                toggleLanguage()
                binding.arabicsTv.isChecked
//                findNavController().navigate(R.id.splashFragment)
            }

            R.id.arabics_tv -> {
                toggleArabicLanguage()
                binding.englishsTv.isChecked
//                findNavController().navigate(R.id.splashFragment)
            }

            R.id.change_password_tv -> {
                val bundle = Bundle()
                bundle.putString("loged", "loged")
                findNavController().navigate(R.id.newPasswordFragment, bundle)

                changePassword = "edit"
            }

            R.id.about_app_tv -> {
                findNavController().navigate(R.id.aboutUsFragment)
            }

            R.id.call_us_tv -> {
//                checkPermisiion("02 21256299")
                //             checkPermisiion("17143")
            }

            R.id.terms_condition_tv -> {
//                val defaultBrowser =
//                    Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
//                defaultBrowser.data = Uri.parse("https://salamtakdoctor.azurewebsites.net/terms")
//                startActivity(defaultBrowser)
                findNavController().navigate(R.id.webViewFragment)
            }

            R.id.notifiction_tv -> {
                findNavController().navigate(R.id.notificationFragment)
//                findNavController().navigate(R.id.videoCallSdk)
            }

            R.id.lang_tv -> {

                var selectedLang = ""

                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(R.layout.language_bottom_sheet)

                val englishBtn = bottomSheetDialog.findViewById<MaterialCardView>(R.id.lang_en)!!
                val arabicBtn = bottomSheetDialog.findViewById<MaterialCardView>(R.id.lang_ar)!!
                val doneBtn = bottomSheetDialog.findViewById<MaterialButton>(R.id.done_btn)!!
                val radioEn = bottomSheetDialog.findViewById<RadioButton>(R.id.radio_en_bottom)!!
                val radioAr = bottomSheetDialog.findViewById<RadioButton>(R.id.radio_ar_bottom)!!
//
                englishBtn.setOnClickListener {
                    selectedLang = KEY_LANGUAGE_EN
                    radioAr.isChecked = false
                    radioEn.isChecked = true
                    sessionManagement.setString("text", "English")
                }
                arabicBtn.setOnClickListener {
                    selectedLang = KEY_LANGUAGE_AR
                    radioEn.isChecked = false
                    radioAr.isChecked = true
                    sessionManagement.setString("text", "Arabic")
                }

                radioAr.setOnCheckedChangeListener { compoundButton, b ->
                    selectedLang = KEY_LANGUAGE_AR
                    radioEn.isChecked = false
                    sessionManagement.setString("text", "Arabic")
                }
                radioEn.setOnCheckedChangeListener { compoundButton, b ->
                    selectedLang = KEY_LANGUAGE_EN
                    radioAr.isChecked = false
                    sessionManagement.setString("text", "English")
                }

                doneBtn.setOnClickListener {
                    if (selectedLang == KEY_LANGUAGE_AR) {
                        toggleArabicLanguage()
                        UserData.setLanguageID(Localization.ARABIC_VALUE)
                        //   Localization.setApplicationLanguage(requireContext())
                    } else {
                        toggleLanguage()
                        UserData.setLanguageID(Localization.ENGLISH_VALUE)
                        //  Localization.setApplicationLanguage(requireContext())
                    }

                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.show()

//                val sucess = LanguageDialog(findNavController())
//                sucess.show(childFragmentManager, "0")
//                if (sessionManagement.getLang() == KEY_LANGUAGE_AR) {
//                    toggleLanguage()
//
//                    // Mansour Comment New
//                    UserData.setLanguageID(Localization.ENGLISH_VALUE)
//                    Localization.setApplicationLanguage(requireContext())
//                }
//                else {
//                    toggleArabicLanguage()
//                    // Mansour Comment New
//                    UserData.setLanguageID(Localization.ARABIC_VALUE)
//                    Localization.setApplicationLanguage(requireContext())
//
//                }

            }

        }
    }


    private fun toggleLanguage() {
        val lang = KEY_LANGUAGE_EN
        //  setLocale(requireActivity(), lang)
        DateUtils.setLanguageFromMore("en")
        DefaultLocaleHelper.getInstance(requireContext()).setCurrentLocale("en")
        requireActivity().finish()
        startActivity(requireActivity().intent)
        // sessionManagement.setLang(lang)
        DateUtils.setLang("en")
    }

    private fun toggleArabicLanguage() {
        val lang = KEY_LANGUAGE_AR
        //  setLocale(requireActivity(), lang)
        DateUtils.setLanguageFromMore("ar")
        DefaultLocaleHelper.getInstance(requireContext()).setCurrentLocale("ar")
        requireActivity().finish()
        startActivity(requireActivity().intent)
        //  sessionManagement.setLang(lang)
        DateUtils.setLang("ar")
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

}