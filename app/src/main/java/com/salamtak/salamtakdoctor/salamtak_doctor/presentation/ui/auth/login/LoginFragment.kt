package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.salamtak.R
import com.salamtak.core.localDataBase.SharedPreferencesKeys
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentLoginBinding
import com.salamtak.domain.entities.GeneralResponse
import com.salamtak.domain.entities.response.UserDataModel
import com.salamtak.domain.entities.resquest.auth.LoginModel
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.LoginViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import retrofit2.HttpException

class LoginFragment : BaseFragment<LoginViewModel>(R.layout.fragment_login), DialogsListener {

    lateinit var binding: FragmentLoginBinding
    lateinit var mContext: Context
    lateinit var navController: NavController
    override val screenViewModel: LoginViewModel by sharedViewModel(StringQualifier(ViewModelsModule.LOGIN_VIEW_MODEL))
    private val sessionManagement: SessionManagement by inject()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var REQUEST_PHONE_CALL = 1
    var fcm_token = ""

    val TAG = LoginFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAnalytics = Firebase.analytics
        Log.d(com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG, "TokenNew: " + UserData.getToken())
        Log.d(com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG, "Tokenold: " + sessionManagement.getToken())

        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        binding.phoneEt.setOnClickListener {
            binding.errorPhone.visibility = View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext = binding.root.context
        noInternetDialog = NoInternetDialog(mContext, this)
        loadingDialog = LoadingDialog(mContext)
        setUi()
//        subscribeToObserve()


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("fcmToken", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fcm_token = task.result

        })

    }

    //    @SuppressLint("MissingPermission")
    private fun setUi() {

        binding.toolbar.ivBack.setOnClickListener {
            navController.popBackStack()
        }
        binding.createNew.setOnClickListener {
            navController.navigate(R.id.signUpFragment)
        }

        binding.toLoginBtn.setOnClickListener {

            binding.errorPassword.visibility = View.GONE
            binding.errorPhone.visibility = View.GONE

            checkValidity()

            val logger = AppEventsLogger.newLogger(requireContext())
            logger.logEvent("Login")

            // firebase event
//            firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
            val bundle = Bundle()
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)


            bundle.putString(FirebaseAnalytics.Param.METHOD, "methd")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)


            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.DISCOUNT, "Mansour Android Device")
            }

//            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, "searchTerm")
//            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        }

        binding.forgetPassword.setOnClickListener {
            navController.navigate(R.id.forgetPasswordFragment)
        }



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

    private fun checkValidity() {

        if (isOnline(mContext))
        {
            if (binding.phoneEt.text.toString().isNotEmpty() && binding.passwordEt.text.toString()
                    .isNotEmpty() && isPhoneValid(binding.phoneEt.text.toString()) && binding.passwordEt.text.toString().length >= 6)
            {

//            subscribeToObserve()

                try {
                    var token = ""

                    FirebaseMessaging.getInstance().token.addOnCompleteListener {
                        if (it.isSuccessful) {


                            token = it.result!!
                            val loginModel = LoginModel(
                                binding.phoneEt.text.toString(),
                                binding.passwordEt.text.toString(),
                                2, fcm_token
                            )

//                            loadingDialog.show()
//                            Log.e(TAG, "checkValidity: ${it.result} \n ${loginModel.username}")
                            screenViewModel.login(loginModel)
                        } else {
                            token = ""
                            val loginModel = LoginModel(
                                binding.phoneEt.text.toString(),
                                binding.passwordEt.text.toString(),
                                2, fcm_token
                            )
                            loadingDialog.show()
                            screenViewModel.login(loginModel)
                        }
                        subscribeToObserve()

                    }


                } catch (e: Exception) {

                    Log.e(TAG, "checkValidity: $e")
//                convertErrorBody(e)
                }

            }
            else {

                if (binding.phoneEt.text.toString().isEmpty()) {
//                    binding.phoneEt.error = getString(R.string.phone_required)

                }
                if (binding.passwordEt.text.toString().isEmpty()) {
//                    binding.passwordEt.error = getString(R.string.password_required)
                }
                if (!isPhoneValid(binding.phoneEt.text?.isEmpty().toString())) {
                    binding.errorPhone.visibility = View.VISIBLE
                }

                if (binding.passwordEt.text.toString().length < 6) {
                    binding.errorPassword.visibility = View.VISIBLE
//                binding.errorPassword.error = getString(R.string.password_length_error)
                    binding.errorPassword.setText(R.string.password_length_error)
                }

            }

        }
        else {
            noInternetDialog.show(childFragmentManager, "")
        }



    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }


    private fun subscribeToObserve() {

        screenViewModel.loginResponse.observe(viewLifecycleOwner) {

            if (it != null) {

                if (it.Success) {

                    loadingDialog.dismiss()
                    Log.e(TAG, "subscribeToObserve: ${it.Data}")

                    toast(mContext, "Login successfully!")

                    sessionManagement.createSession(
                        it.Data.Id.toString(),
                        it.Data.Token,
                        it.Data.Phone
                    )
                    // new comment Mansour
                    UserData.setToken(it.Data.Token)
                    if (it.Data.Image != null) {
                        UserData.setImage(it.Data.Image!!)
                    }
                    UserData.setProfileStatus(SharedPreferencesKeys.PREF_PROFILE_STATUS.name, it.Data.ProfileStatus)
                    UserData.setUserName(it.Data.Name)
                    sessionManagement.setStatus(it.Data.ProfileStatus.toString())
                    sessionManagement.setLoggedIn()
                    val userData = UserDataModel(
                        "",
                        it.Data.Name,
                        it.Data.NameAR!!,
                        it.Data.Phone
                    )
                    val gson = Gson()
                    val jsonData = gson.toJson(userData)
                    sessionManagement.saveUserData(jsonData)
                    if (it.Data.ProfileStatus == 4) {
//                    sessionManagement.setStatus(it.Data.ProfileStatus.toString())
                        navController.navigate(R.id.homeFragment)
//                navController.navigate(R.id.action_homeFragment_to_signInFragment)
                    }else{
                        findNavController().navigate(R.id.setupProfileFragment)
                    }
//                    if (it.Data.ProfileStatus == 0) {
//                    sessionManagement.setStatus(it.Data.ProfileStatus.toString())
//                        findNavController().navigate(R.id.setupProfileFragment)
//                    }
//                    if (it.Data.ProfileStatus == 1) {
////                    sessionManagement.setStatus(it.Data.ProfileStatus.toString())
//                        findNavController().navigate(R.id.certificatesFragment)
//                    }
//                    if (it.Data.ProfileStatus == 2) {
////                    sessionManagement.setStatus(it.Data.ProfileStatus.toString())
//                        findNavController().navigate(R.id.documentsFragment)
//                    }

                } else {
                    binding.errorPhone.visibility = View.VISIBLE
                    binding.errorPhone.setText(it.Message)
//                showError(
//                    it.Message
//                )
                }

            }

        }


        screenViewModel.failure.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            when (it) {
                is GeneralResponse.Failure.JSonServerError -> {
                    when (it.errorCode) {
                        401 -> {
//                            showError(
//                                getString(R.string.error_unauthorized)
//                            )
                            binding.errorPhone.visibility = View.VISIBLE
                            binding.errorPhone.setText(it.errorMessage)
                        }

                        400 -> {

//                            showError(
//                                getString(R.string.error_unauthorized)
//                            )
                            if (it.errorMessage.equals("Incorrect Password")) {
                                binding.errorPassword.visibility = View.VISIBLE
                                binding.errorPassword.setText("Incorrect Password")
                            }
                            else if (it.errorMessage.equals("Phone is not registered")){
                                binding.errorPhone.visibility = View.VISIBLE
                                binding.errorPhone.setText("Phone is not registered")
                            }
                            else {
                                binding.errorPhone.visibility = View.VISIBLE
                                binding.errorPhone.setText("Incorrect phone number")
                            }

                        }

                        404 -> {

//                            showError(
//                                getString(R.string.error_unauthorized)
//                            )
                            if (it.errorMessage.equals("Incorrect password")) {
                                binding.errorPassword.visibility = View.VISIBLE
                                binding.errorPassword.setText(it.errorMessage)
                            } else {
                                binding.errorPhone.visibility = View.VISIBLE
                                binding.errorPhone.setText(it.errorMessage)
                            }


                        }

                        else -> {
                            Log.e("TAG BaseFragmentss", "onCreate: ${it.errorMessage}  ")
                        }
                    }
                    it.errorCode = 200
                }
                is GeneralResponse.Failure.Error -> {
                    when (it.ex) {
                        is HttpException -> if (canShowNoNetworkDialog) {
                            Log.e("Tag", "${it.ex.localizedMessage} ")
                        }
                        is JsonSyntaxException -> {
                            Log.e("TAGJson BaseFragment123", "onCreate: ${it.ex.printStackTrace()}")
                        }
                        else -> {
                            Log.e(
                                "TAG BaseFragment",
                                "onCreate: ${it.ex.cause?.message}  == ${it.ex.message}"
                            )
                        }
                    }
                }
            }

        }

    }

}