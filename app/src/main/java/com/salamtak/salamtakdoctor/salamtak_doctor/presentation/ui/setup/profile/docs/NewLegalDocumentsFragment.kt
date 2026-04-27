package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentDocumentsBinding
import com.salamtak.databinding.FragmentLegalDocumentBinding
import com.salamtak.databinding.FragmentNewLegalDocumentsBinding
import com.salamtak.domain.entities.response.LegalDocumentsData
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DialogsListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RecyclerViewOnClickListener
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.RequestCode
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.isOnline
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import java.io.File


class NewLegalDocumentsFragment : BaseFragment<SetupProfileViewModel>(R.layout.fragment_legal_document),
    DialogsListener {

    private lateinit var binding: FragmentLegalDocumentBinding
    private lateinit var mContext: Context
    private val sessionManagement: SessionManagement by inject()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var noInternetDialog: NoInternetDialog
    override val screenViewModel: SetupProfileViewModel
            by sharedViewModel(StringQualifier(ViewModelsModule.SETUP_VIEW_MODEL))
    private lateinit var nationalIdFront: File
    private lateinit var nationalIdBack: File
    private lateinit var syndicateIdFront: File
    private lateinit var syndicateIdBack: File
    private lateinit var professionCertFront: File
    private lateinit var professionCertBack: File
    var editLabel: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            editLabel = it.getString("edit", "")
        }

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_legal_document, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext,this)
        loadingDialog = LoadingDialog(mContext)

        cameraRequestCode = RequestCode.CAMERA_REQUEST_CODE
        galleryRequestCode = RequestCode.MEDIA_REQUEST_CODE
        editGalleryRequestCode = RequestCode.MEDIA_EDIT_REQUEST_CODE

        getDocuments()
    }

    private fun getDocuments() {

        if(isOnline(mContext)){
            loadingDialog.show()
            screenViewModel.getDocuments()
        }
        else{
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        screenViewModel.getDocumentResponse.observe(viewLifecycleOwner) {
            val gson = Gson()
            val json = gson.toJson(it)
            loadingDialog.dismiss()
            Log.d("legalDoc", "getDocuments: $json")

        }

    }

}