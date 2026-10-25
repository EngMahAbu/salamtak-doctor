package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.register

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.salamtak.R
import com.salamtak.core.localDataBase.SharedPreferencesKeys
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.FragmentSignupBinding
import com.salamtak.domain.entities.GeneralResponse
import com.salamtak.domain.entities.response.UserDataModel
import com.salamtak.domain.entities.resquest.auth.RegisterModel
import com.salamtak.salamtakdoctor.noConnectivityDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.BaseFragment
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule.AUTH_VIEW_MODEL
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SignUpViewModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.*
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.NoInternetDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.StringQualifier
import retrofit2.HttpException
import java.io.File


class SignUpFragment : BaseFragment<SignUpViewModel>(R.layout.fragment_signup), DialogsListener {

    companion object {
        var profileImage: File? = null
    }

    lateinit var binding: FragmentSignupBinding
    lateinit var navController: NavController
    private val args:SignUpFragmentArgs by navArgs()
    lateinit var mContext: Context
    override val screenViewModel: SignUpViewModel by sharedViewModel(StringQualifier(AUTH_VIEW_MODEL))
    private val sessionManagement: SessionManagement by inject()
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics
   // var REQUEST_PHONE_CALL = 1

    private lateinit var loadingDialog: LoadingDialog

    val TAG = SignUpFragment::class.java.simpleName

    //private var imageFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = binding.root.context
        navController = Navigation.findNavController(binding.root)
        loadingDialog = LoadingDialog(mContext)
        noInternetDialog = NoInternetDialog(mContext, this)
        validity()
        bindViews()

        /*      binding.nameEtArabic.addTextChangedListener(object : TextWatcher {
                  override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                      val input = binding.nameEtArabic.text.toString()
                      val arabicPattern = Pattern.compile("\\p{InArabic}")
                      val matcher = arabicPattern.matcher(input)

                      if (matcher.find()) {
                          Log.e("matches", "arabic")
                          // Arabic text has been entered
                      } else {
                          Toast.makeText(mContext, "please enter arabic letter only", Toast.LENGTH_SHORT)
                              .show()
                          Log.e("matches", "english")
                          binding.nameEtArabic.setText("")
                          // Arabic text has not been entered
                      }

                  }
                  // This method is called to notify you that the text is about to change.


                  override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                      // This method is called to notify you that the text has changed.
                      val input = binding.nameEtArabic.text.toString()

                      val arabicPattern = Pattern.compile("\\p{InArabic}")
                      val matcher = arabicPattern.matcher(input)

                      if (matcher.find()) {
                          Log.e("matches", "arabic")
                          // Arabic text has been entered
                      } else {
                          Log.e("matches", "english")
                          // Arabic text has not been entered
                      }

                  }

                  override fun afterTextChanged(s: Editable?) {
                      val input = binding.nameEtArabic.text.toString()

                      val arabicPattern = Pattern.compile("\\p{InArabic}")
                      val matcher = arabicPattern.matcher(input)

                      if (matcher.find()) {
                          Log.e("matches", "arabic")
                          // Arabic text has been entered
                      } else {
                          Toast.makeText(mContext, "please enter arabic letter only", Toast.LENGTH_SHORT)
                              .show()
                          Log.e("matches", "english")
                          // Arabic text has not been entered
                      }
                      // This method is called to notify you that the text has been changed and the user has stopped editing.
                  }
              })*/


//        binding.nameEtArabic.setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL)
//        binding.nameEtArabic.setTextIsSelectable(true)
//
//        val arabicLocale = Locale("ar")
//        val arabicIME = getSystemService(mContext) as InputMethodManager
//        arabicIME.setInputMethod(binding.nameEtArabic.windowToken, arabicLocale.toString())

        setUi()

        binding.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.dialogCalling)
        }


    }

    override fun onInternetOKClick(noInternetDialog: NoInternetDialog) {
        noInternetDialog.dismiss()
    }

    fun checkInput(editText: EditText, message: String, text: String) {

        editText.keyListener = DigitsKeyListener.getInstance(text)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val allowedChars = text
                val enteredChars = charSequence.toString()
                for (j in 0 until enteredChars.length) {
                    if (allowedChars.indexOf(enteredChars[j]) == -1) {
                        // Character not allowed, show toast message
                        Toast.makeText(
                            requireContext(), message, Toast.LENGTH_SHORT
                        ).show()
                        editText.setText("")
                        return
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    fun setUi() {

        binding.shapeableImageView.setOnClickListener {
            showDialog(cameraRequestCode, galleryRequestCode)
        }


//       checkInput(binding.nameEtArabic,"Please input in english" ,"غظضذخثتشرقصجفعسنملةىكيطئءؤحزوهدبا")
//       checkInput(binding.nameEt,"Please input in arabic", "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ")

//        binding.toolbar.ivBack.setOnClickListener {
//            navController.popBackStack()
//        }
//        binding.toolbar.toolbarHeader.text = getString(R.string.create_new_account)

        //TODO
        binding.createNew.setOnClickListener {
            // check box for terms and condition
//            if (binding.cbSingle.isChecked) {
//                binding.termsError.visibility = View.GONE

                //  TODO Call CheckValidity Fun
                checkValidity()

//                val logger = AppEventsLogger.newLogger(requireContext())
//                logger.logEvent("Sign up")

                // firebase event
                firebaseAnalytics = Firebase.analytics
                val bundle = Bundle()
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

        }

        binding.toLoginBtn.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }

        binding.newTermsBtn.setOnClickListener {

            findNavController().navigate(R.id.webViewFragment)
        }

//        binding.numTv.setOnClickListener {
//            checkPermisiion("17143")
//        }

    }


//    fun checkPermisiion(phone: String) {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(), Manifest.permission.CALL_PHONE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL
//        )
//        else {
//            makeCall(phone)
//        }
//
//    }


//    private fun makeCall(phone: String) {
//        val intent = Intent(Intent.ACTION_CALL)
//        intent.setData(Uri.parse("tel:$phone"))
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CALL_PHONE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
//            return
//        }
//        startActivity(intent)
//    }

    // TODO
    @SuppressLint("ResourceType")
    private fun checkValidity() {
            if(isvalidateFeilds()){
                if (checkPassw(binding.passwordEt.text.toString())){
                    binding.tvPasswordError.text = getString(R.string.lesstthan6number)
                    binding.tvPasswordError.visibility = View.VISIBLE
                    binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)
                } else {
                    binding.tvPasswordError.visibility = View.GONE
                    binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext)
                    binding.passwordEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                    val str = binding.nameEt.text.toString()
                    val delimiter = " "

                    val name = str.split(delimiter)

                    val strArabic = binding.nameEtArabic.text.toString()


                    val delimiterArabic = " "

                    val nameAr = strArabic.split(delimiterArabic)
                    if (isOnline(mContext)) {
                        if (
//                binding.uploadtxt.visibility == View.GONE &&
                            binding.phoneEt.text.toString().isNotEmpty() &&
                            binding.passwordEt.text.toString().isNotEmpty() &&
                            name.size >= 3 && nameAr.size >= 3 &&
                            binding.confirmPasswordEt.text.toString().isNotEmpty() &&
                            binding.nameEt.text.toString().isNotEmpty() &&
                            binding.nameEt.text.toString().length >= 3 &&
                            binding.passwordEt.text.toString().length >= 6 && binding.confirmPasswordEt.text.toString().length >= 6 &&
                            (binding.passwordEt.text.toString() == binding.confirmPasswordEt.text.toString()) &&
                            isPhoneValid(binding.phoneEt.text.toString())
//                            && isEmailValid(binding.emailEt.text.toString())

//                && isEnglishValid(binding.nameEt.text.toString())
                        ) {

                            if (binding.passwordEt.text.toString().length >= 6 && binding.confirmPasswordEt.text.toString().length >= 6) {
                                if (binding.passwordEt.text.toString() == binding.confirmPasswordEt.text.toString()) {
                                    binding.passwordEt.error = null

                                    if (isPhoneValid(binding.phoneEt.text.toString()) ) {
                                        loadingDialog.show()

                                        var token = ""

                                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                            if (it.isSuccessful) {

                                                sessionManagement.setFireBaseToken(it.result!!)
                                                token = it.result!!
                                                val registerModel = RegisterModel(
                                                    "${binding.phoneEt.text.toString()}",
                                                    null,
                                                    binding.passwordEt.text.toString(),
                                                    binding.nameEt.text.toString(),
                                                    args.typId
                                                )
                                                subscribeToObserver(registerModel)
                                            } else {
                                                token = ""
                                                val registerModel = RegisterModel(
                                                    "${binding.phoneEt.text.toString()}",
                                                    null,
                                                    binding.passwordEt.text.toString(),
                                                    binding.nameEt.text.toString(),
                                                    args.typId
                                                )
                                                subscribeToObserver(registerModel)
                                            }
                                        }

                                    } else {

//                            if (!isPhoneValid(binding.phoneEt.text.toString())) {
//                                binding.phoneEt.error = getString(R.string.phone_valid)
//                            }
//
//                            if (!isEmailValid(binding.emailEt.text.toString())) {
//                                binding.emailEt.error = getString(R.string.email_valid)
//                            }


                                    }
                                } else {
                                    Toast.makeText(
                                        mContext,
                                        "Password and Confirm Password should be matched",
                                        Toast.LENGTH_SHORT
                                    ).show();
//                        if (!isPhoneValid(binding.phoneEt.text.toString())) {
//                            binding.phoneEt.error = getString(R.string.phone_valid)
//                        }
//
//                        if (!isEmailValid(binding.emailEt.text.toString())) {
//                            binding.emailEt.error = getString(R.string.email_valid)
//                        }
//
//                        binding.passwordEt.error = getString(R.string.password_identical)
//                        binding.confirmPasswordEt.error = getString(R.string.password_identical)
                                }
                            } else {
//                    if (binding.passwordEt.text.toString().length < 6) {
//                        binding.passwordEt.error = getString(R.string.password_length_error)
//                    }
//                    if (binding.confirmPasswordEt.text.toString().length < 6) {
//                        binding.confirmPasswordEt.error = getString(R.string.password_length_error)
//                    }

                            }
                        } else {

                            if (binding.passwordEt.text.toString().length < 6) {
                                //  binding.passwordError.visibility = View.VISIBLE
                                //    binding.tvPasswordError.visibility = View.VISIBLE
                                //   binding.passwordEt.error = getString(R.string.password_length_error)

                            } else {
                                //  binding.ivPasserror.visibility=View.GONE
                            }
                            if (binding.confirmPasswordEt.text.toString().length < 6) {
                                //   binding.ivNameError6.visibility = View.VISIBLE
                                // binding.confirmPasswordEt.error = getString(R.string.password_length_error)

                                binding.tvConfirmError.setOnClickListener {
                                    //   binding.confirmPasswordEt.error = getString(R.string.password_length_error)
                                }
                            } else {
                                //  binding.ivError.visibility=View.GONE

                            }

                            if (!isPhoneValid(binding.phoneEt.text.toString())) {
                                //  binding.phoneEt.error = getString(R.string.phone_valid)
                            }

                            if (!isEmailValid(binding.emailEt.text.toString())) {
                                //binding.emailEt.error = getString(R.string.email_valid)
                            }

                            if (binding.passwordEt.text.toString() != binding.confirmPasswordEt.text.toString()
                            ) {
                                Toast.makeText(
                                    mContext,
                                    "Password and Confirm Password should be matched",
                                    Toast.LENGTH_SHORT
                                ).show();
//                    binding.passwordEt.error = getString(R.string.password_identical)
//                    binding.confirmPasswordEt.error = getString(R.string.password_identical)
                                binding.ivNameError6.visibility = View.VISIBLE
                                //  binding.tvConfirmError.visibility = View.VISIBLE
                                //  binding.tvPasswordError.visibility = View.VISIBLE
//                    binding.passwordEt.error = getString(R.string.password_identical)
//                    binding.confirmPasswordEt.error = getString(R.string.password_identical)

                            } else {
//                    binding.ivError.visibility=View.GONE
//                    binding.ivPasserror.visibility=View.GONE
                            }


                            if (binding.phoneEt.text.toString().isEmpty()) {
                                //   binding.phoneEt.error = getString(R.string.phone_required)
                            }
                            if (binding.passwordEt.text.toString().isEmpty()) {

                                // binding.tvPasswordError.visibility = View.VISIBLE
//                    binding.passwordEt.hint= getString(R.string.confirm_password_required)
                                //binding.passwordEt.error = getString(R.string.password_required)

                            } else {
                                //      binding.ivPasserror.visibility=View.GONE
                            }
                            if (binding.confirmPasswordEt.text.toString().isEmpty()) {
                                //    binding.ivNameError6.visibility = View.VISIBLE
                                //  binding.confirmPasswordEt.error = getString(R.string.confirm_password_required)


                            } else {
                                //       binding.ivError.visibility=View.GONE
                            }
                            if (binding.nameEt.text.toString().isEmpty()) {
                                //binding.nameEt.error = getString(R.string.name_required)
                            }
                            if (binding.emailEt.text.toString().isEmpty()) {
                                //   binding.emailEt.error = getString(R.string.email_required)
                            }
                            if (binding.nameEt.text.toString().length < 3) {
                                //  binding.nameEt.error = getString(R.string.please_enter_valid_name)

                            }

                            if (name.size < 3) {
                                //  binding.nameEt.error = getString(R.string.name_size)
                            }

                            if (nameAr.size < 3) {
                                //  binding.nameEtArabic.error = getString(R.string.name_size)
//                    binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(
//                        0,
//                        0,
//                        R.drawable.ic_error,
//                        0
//                    )
                            }


                            if (!isEnglishValid(binding.nameEt.text.toString())) {
                                // binding.nameEt.error = getString(R.string.english_name)
                            }
                            if (isEnglishValid(binding.nameEtArabic.text.toString())) {
                                //  binding.nameEtArabic.error = getString(R.string.enter_arabic_only)
                            }

                        }
                    } else {
                        noInternetDialog.show(
                            childFragmentManager, ""
                        )
                    }
                }
            }

        }

    private fun validity(){
        binding.nameEt.doOnTextChanged { text, start, before, count ->

            if (binding.nameEt.text?.trim().isNullOrEmpty()) {
//                binding.nameEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
                binding.tvFullNameError.text = getString(R.string.requiredEnglish)
                binding.tvFullNameError.visibility = View.VISIBLE
                binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)


            } else if (binding.nameEt.text?.trim()?.split(" ")!!.size < 3) {
                binding.tvFullNameError.visibility = View.VISIBLE
                binding.tvFullNameError.text = getString(R.string.Enter_fullName_english)
//                binding.nameEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
                binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)


            } else {
                binding.tvFullNameError.visibility = View.GONE
                binding.mCardName.setBackgroundResource(R.drawable.bg_edittext)
                binding.nameEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }
        }
        binding.nameEtArabic.doOnTextChanged { text, start, before, count ->
            if (binding.nameEtArabic.text?.trim().isNullOrEmpty()) {
//                binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
                binding.tvFullNameARError.text = getString(R.string.requiredarabic)
                binding.tvFullNameARError.visibility = View.VISIBLE
                binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)


            } else if (binding.nameEtArabic.text?.trim()!!.split(" ").size < 3) {
                binding.tvFullNameARError.text = getString(R.string.Enter_fullName_Arabic)
                binding.tvFullNameARError.visibility = View.VISIBLE
//                binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
                binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)


            } else {
                binding.tvFullNameARError.visibility = View.GONE
                binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }


        }
        binding.phoneEt.doOnTextChanged { text, start, before, count ->
            if (binding.phoneEt.text?.trim()!!.isEmpty()) {
                binding.tvPohneError.text = getString(R.string.requiredmobile)
                binding.tvPohneError.visibility = View.VISIBLE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )

            } else if (binding.phoneEt.text?.length!! < 11) {
                binding.tvPohneError.text = getString(R.string.phonedigits)
                binding.tvPohneError.visibility = View.VISIBLE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
            }else if (!isPhoneValid(binding.phoneEt.text.toString())) {
                binding.tvPohneError.text = getString(R.string.phone_valid)
                binding.tvPohneError.visibility = View.VISIBLE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
             }
            else {
                binding.tvPohneError.visibility = View.GONE
                binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }

        }
        binding.emailEt.doOnTextChanged { text, start, before, count ->
            if (binding.emailEt.text?.trim()!!.isEmpty()) {
                binding.tvEmailError.text = getString(R.string.requiredemail)
                binding.tvEmailError.visibility = View.VISIBLE
                binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
        //        binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0)

            } else if (!(binding.emailEt.text?.toString()?.isValidEmail() == true)) {
                binding.tvEmailError.text = getString(R.string.match_email)
                binding.tvEmailError.visibility = View.VISIBLE
                binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            //    binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0)

            } else {
                binding.tvEmailError.visibility = View.GONE
                binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }

        }
        binding.passwordEt.doOnTextChanged { text, start, before, count ->
            if (binding.passwordEt.text?.trim()!!.isEmpty()) {
                binding.tvPasswordError.text = getString(R.string.requiredpassword)
                binding.tvPasswordError.visibility = View.VISIBLE
                binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)
           //     binding.passwordEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0)

            } else if (binding.passwordEt.text?.length!! < 6) {
                binding.tvPasswordError.text = getString(R.string.lesstthan6number)
                binding.tvPasswordError.visibility = View.VISIBLE
                binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)
           //     binding.passwordEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0)

            }
//            else if (!checkPassw(binding.passwordEt.text.toString())){
//                binding.tvPasswordError.text = getString(R.string.lesstthan6number)
//                binding.tvPasswordError.visibility = View.VISIBLE
//                binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//            }
            else {
                binding.tvPasswordError.visibility = View.GONE
                binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.passwordEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }

        }
        binding.confirmPasswordEt.doOnTextChanged { text, start, before, count ->
            if (binding.confirmPasswordEt.text?.trim().isNullOrEmpty()) {
                binding.tvConfirmpasswordError.text = getString(R.string.requiredconfirmpassword)
                binding.tvConfirmpasswordError.visibility = View.VISIBLE
                binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.confirmPasswordEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )

            }
//            else if (!checkPassw(binding.confirmPasswordEt.text.toString())){
//                binding.tvConfirmpasswordError.text = getString(R.string.lesstthan6number)
//                binding.tvConfirmpasswordError.visibility = View.VISIBLE
//                binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//            }
            else if (binding.confirmPasswordEt.text?.toString() != binding.passwordEt.text?.toString()) {
                binding.tvConfirmpasswordError.text = getString(R.string.doesnotmatch)
                binding.tvConfirmpasswordError.visibility = View.VISIBLE
                binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.confirmPasswordEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )
                binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//                binding.confirmPasswordEt.setCompoundDrawablesWithIntrinsicBounds(
//                    0,
//                    0,
//                    R.drawable.ic_error,
//                    0
//                )

            }

            else {
                binding.tvConfirmpasswordError.visibility = View.GONE
                binding.tvPasswordError.visibility = View.GONE
                binding.ivNameError6.visibility=View.GONE
                binding.passwordError.visibility=View.GONE
                binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.confirmPasswordEt.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
                binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext)
                binding.passwordEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }

        }
    }

    private fun subscribeToObserver(registerModel: RegisterModel) {

        if (isOnline(mContext)) {
            screenViewModel.signUp(registerModel)
        } else {
            noConnectivityDialog.noInternetDialogFragment(requireContext())
        }

        screenViewModel.signUpResponse.observe(viewLifecycleOwner, Observer {

            if (it != null) {

                if (it.Success) {
                    loadingDialog.dismiss()
                    sessionManagement.setCode(it.Data.Code.toString(), it.Data.UserId.toString())
                    toast(mContext, getString(R.string.successful_signup))
                    val bundle = Bundle()
                    bundle.putString(EMAIL, registerModel.email)
                    bundle.putString(PHONE, registerModel.phone)
                    bundle.putString(PASSWORD, registerModel.password)
                    bundle.putString(DOCTOR_NAME, registerModel.name)
                    bundle.putString(NEW_OTP_CODE, it.Data.Code.toString())
                    bundle.putInt(TypeId, args.typId)
                    Log.d(TAG, "subscribeToObserver: ${it.Data.Code}")
                    val gson = Gson()
                    val userData = UserDataModel(
                        binding.emailEt.text.toString(),
                        binding.nameEt.text.toString(),
                        binding.nameEtArabic.text.toString(),
                        binding.phoneEt.text.toString()
                    )

                    val jsonData = gson.toJson(userData)
                    sessionManagement.saveUserData(jsonData)

                    sessionManagement.setString(registerModel.name)

                    UserData.setProfileStatus(SharedPreferencesKeys.PREF_PROFILE_STATUS.name, 0)

                    navController.navigate(R.id.otpFragment, bundle)
                }

            }

        })

        screenViewModel.failure.removeObservers(viewLifecycleOwner)
        screenViewModel.failure.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()

            if (it != null) {
                when (it) {
                    is GeneralResponse.Failure.JSonServerError -> {
                        when (it.errorCode) {
                            401 -> {
                                showError(
                                    getString(R.string.error_unauthorized)
                                )
                            }

                            400 -> {
                                showError(
                                    it.errorMessage!!
                                )

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
                                Log.e(
                                    "TAGJson BaseFragment123",
                                    "onCreate: ${it.ex.printStackTrace()}"
                                )
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
        screenViewModel.loading.observe(viewLifecycleOwner, Observer {
            showLoading(it)
        })

    }

    fun isEnglish(str: String): Boolean {
        for (i in 0 until str.length) {
            val c = str[i]
            if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
                return false
            }
        }
        return true
    }

    fun isArabic(str: String): Boolean {
        for (i in 0 until str.length) {
            val c = str[i]
            if (c < '\u0600' || c > '\u06FF') {
                return false
            }
        }
        return true
    }



    fun String.isValidEmail() =
        !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun isvalidateFeilds(): Boolean {

        var value = false

        if (binding.nameEt.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameError.text = getString(R.string.requiredEnglish)
            binding.tvFullNameError.visibility = View.VISIBLE
            binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)
        } else if (binding.nameEt.text?.trim()?.split(" ")!!.size < 3) {
            binding.tvFullNameError.visibility = View.VISIBLE
            binding.tvFullNameError.text = getString(R.string.Enter_fullName_english)
            binding.mCardName.setBackgroundResource(R.drawable.bg_edittext_error)
        } else {
            binding.tvFullNameError.visibility = View.GONE
            binding.mCardName.setBackgroundResource(R.drawable.bg_edittext)
            binding.nameEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        if (binding.nameEtArabic.text?.trim().isNullOrEmpty()) {
            binding.tvFullNameARError.text = getString(R.string.requiredarabic)
            binding.tvFullNameARError.visibility = View.VISIBLE
            binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)


        } else if (binding.nameEtArabic.text?.trim()!!.split(" ").size < 3) {
            binding.tvFullNameARError.text = getString(R.string.Enter_fullName_Arabic)
            binding.tvFullNameARError.visibility = View.VISIBLE
            binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext_error)


        } else {
            binding.tvFullNameARError.visibility = View.GONE
            binding.mcardArabicLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.nameEtArabic.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        if (binding.phoneEt.text?.trim()!!.isEmpty()) {
            binding.tvPohneError.text = getString(R.string.requiredmobile)
            binding.tvPohneError.visibility = View.VISIBLE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)

        } else if (binding.phoneEt.text?.length!! < 11) {
            binding.tvPohneError.text = getString(R.string.phonedigits)
            binding.tvPohneError.visibility = View.VISIBLE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)

        }else if (!isPhoneValid(binding.phoneEt.text.toString())) {
            binding.tvPohneError.text = getString(R.string.phone_valid)
            binding.tvPohneError.visibility = View.VISIBLE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext_error)
        }
        else {
            binding.tvPohneError.visibility = View.GONE
            binding.mcardphoneLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.phoneEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        }
//        if (binding.emailEt.text?.trim()!!.isEmpty()) {
//            binding.tvEmailError.text = getString(R.string.requiredemail)
//            binding.tvEmailError.visibility = View.VISIBLE
//            binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//
//        } else if (!(binding.emailEt.text?.toString()?.isValidEmail() == true)) {
//            binding.tvEmailError.text = getString(R.string.match_email)
//            binding.tvEmailError.visibility = View.VISIBLE
//            binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//
//        } else {
//            binding.tvEmailError.visibility = View.GONE
//            binding.mcardEmailLayout.setBackgroundResource(R.drawable.bg_edittext)
//            binding.emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
//
//        }
        if (binding.passwordEt.text?.trim()!!.isEmpty()) {
            binding.tvPasswordError.text = getString(R.string.requiredpassword)
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)

        } else if (binding.passwordEt.text?.length!! < 6  ) {
            binding.tvPasswordError.text = getString(R.string.lesstthan6number)
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)
        }
//        else if (!checkPassw(binding.passwordEt.text.toString())){
//            binding.tvPasswordError.text = getString(R.string.lesstthan6number)
//            binding.tvPasswordError.visibility = View.VISIBLE
//            binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//        }
        else {
            binding.tvPasswordError.visibility = View.GONE
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.passwordEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        if (binding.confirmPasswordEt.text?.trim().isNullOrEmpty()) {
            binding.tvConfirmpasswordError.text = getString(R.string.requiredconfirmpassword)
            binding.tvConfirmpasswordError.visibility = View.VISIBLE
            binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext_error)
        }
        else if (binding.confirmPasswordEt.text?.toString() != binding.passwordEt.text?.toString()) {
            binding.tvConfirmpasswordError.text = getString(R.string.doesnotmatch)
            binding.tvConfirmpasswordError.visibility = View.VISIBLE
            binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext_error)
            binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext_error)

        }
//        else if (!checkPassw(binding.confirmPasswordEt.text.toString())){
//            binding.tvPasswordError.text = getString(R.string.lesstthan6number)
//            binding.tvPasswordError.visibility = View.VISIBLE
//            binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext_error)
//        }
        else {
            binding.tvConfirmpasswordError.visibility = View.GONE
            binding.tvPasswordError.visibility = View.GONE
            binding.confirmLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.confirmPasswordEt.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
            binding.passwordLayout.setBackgroundResource(R.drawable.bg_edittext)
            binding.passwordEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            value = true
        }

        return value

    }

    fun checkPassw(value: String): Boolean {
        value.forEach { example ->
            if(value.contains(Regex("([a-za-z]){10}"))) {
                return true
            }
        }
        return false
    }


    private fun bindViews(){
        when (args.typId){
            4->{
                binding.signUpTv.text=getString(R.string.create_poly_clinic_account)
                binding.createNew.text=getString(R.string.create_poly_clinic_account)
                binding.filledTextField.hint=getString(R.string.poly_full_name_in_english)
                binding.arFilledTextField.hint=getString(R.string.poly_full_name_in_arabic)
            }
            7->{
                binding.signUpTv.text=getString(R.string.create_hospital_account)
                binding.createNew.text=getString(R.string.create_hospital_account)
            }
        }
    }

}
