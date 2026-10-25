package com.salamtak.salamtakdoctor


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.salamtak.BuildConfig
import com.salamtak.R
import com.salamtak.core.utils.SessionManagement
import com.salamtak.databinding.ActivityMainBinding
import com.salamtak.salamtakdoctor.dialog.Dialog
import com.salamtak.salamtakdoctor.localization.DefaultLocaleHelper
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Constant.KEY_CURRENT_VERSION
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.Constant.KEY_FORCE_UPDATE_REQUIRED
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.ForceUpdateUtils
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {
    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }
    private val dialog = Dialog()
    private val NOTIFICATION_PERMISSION_CODE = 101
    private val configSettings by lazy {
        FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds((if (BuildConfig.DEBUG) 0 else 3600))
            .build()
    }
    val sharedStorage: SessionManagement by inject()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var type = -1
    // Which bottom menu is inflated; activity_main starts with the Doctor/Polyclinic one.
    private var hospitalMenuShown = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(DefaultLocaleHelper.getInstance(newBase!!).onAttach())
     //  sharedStorage.getLang()?.let { loadLocale(this, it) }
    }
    override fun onResume() {
        super.onResume()
        check()
    }

    init {
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.Q) {
            this.window.decorView.windowInsetsController!!.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
        } else{
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.new_theme_color)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "Mansour Android Device")
        }
//        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
//        val bundle = Bundle()
//        bundle.putString("Id", "5")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            setStatusBarColor(
                ctx = this,
                activity = this,
                rootLayout = binding.rootLayout,
                window = window
            )
        }
        if (intent.hasExtra("type")){
            type = intent.getIntExtra("type", -1);
        }
         requestNotificationPermission()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        navController = navHostFragment?.findNavController()!!
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        navController.addOnDestinationChangedListener(this)
        binding.navigation.selectedItemId = R.id.home_item
        if (type == 0){
            navGraph.setStartDestination(R.id.loginFragment)
        }else if (type == 1){
            navGraph.setStartDestination(R.id.homeFragment)
        }
        binding.navigation.setOnNavigationItemSelectedListener(this)

    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        Log.e("NAV", destination.label.toString())
        when (destination.id) {
            R.id.thankYouFragment,
            R.id.splashFragment, R.id.splash2Fragment, R.id.chooseFragment,R.id.screen1Fragment,
            R.id.screen2Fragment,
            R.id.screen3Fragment,R.id.welcomeFragment, R.id.loginFragment, R.id.signUpFragment, R.id.chatFragment,R.id.passwordResetSuccess,
            R.id.otpFragment, R.id.forgetPasswordFragment, R.id.resetOtpFragment, R.id.newPasswordFragment,R.id.privacyPolicyFragment,R.id.aboutAppFragment,R.id.appointmentFragment,R.id.statisticsFragment,
            R.id.setupProfileFragment, R.id.certificatesFragment, R.id.documentsFragment, R.id.clinicScheduleFragment,R.id.videoCallFragment,R.id.gatesDetailsFragment,R.id.referenceFragment,R.id.salamtakgatesFragment,R.id.offersFragment,R.id.addOffersFragment,
            R.id.polyClinicAdminFragment,R.id.enterpriseInfoFragment,R.id.editPolyClinicAdminFragment,R.id.editEnterpriseInfoFragment,R.id.chooseClinicsFragment,R.id.doctorsVideosFragment,R.id.doctorAccountFragment,R.id.patientProfileFragment,
            R.id.patientSyemtomsFragment,R.id.emrFragment,R.id.queueFragment,R.id.hospitalProfileFragment,
            R.id.hospitalDoctorClinicsFragment,R.id.hospitalClinicFormFragment,R.id.hospitalDoctorScheduleFragment,R.id.hospitalDoctorProfileFragment,
            R.id.hospitalSignUpAdminFragment,R.id.hospitalSignUpInfoFragment,R.id.hospitalSignUpThanksFragment,
            R.id.addNewClinicFragment, R.id.createClinicGalleryFragment, R.id.languageFragment, R.id.vacationClinicFragment, R.id.webViewFragment,R.id.Specaility,R.id.locationFragment-> {
                binding.navigation.visibility = GONE
            }
            else ->
                binding.navigation.visibility = VISIBLE

        }

        applyBottomMenu()
        if (destination.id == R.id.appointmentsFragment){
            binding.navigation.menu.findItem(R.id.schedule_item).isChecked = true
        }else if (destination.id == R.id.moreFragment){
            binding.navigation.menu.findItem(R.id.more_item).isChecked = true
        }else if (destination.id == R.id.hospitalDoctorsFragment){
            binding.navigation.menu.findItem(R.id.doctors_item)?.isChecked = true
        }
    }

    // Hospital accounts get their own tabs; Doctor and Polyclinic keep res/menu/menu.xml.
    private fun applyBottomMenu() {
        val hospital = DateUtils.isHospital()
        if (hospitalMenuShown == hospital) return
        hospitalMenuShown = hospital
        binding.navigation.menu.clear()
        binding.navigation.inflateMenu(if (hospital) R.menu.menu_hospital else R.menu.menu)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.home_item -> {
//            if (DateUtils.getAdminOwnerId().isNullOrEmpty())
//            {
                navController.navigate(R.id.homeFragment)
                true
//            }else{
//                navController.navigate(R.id.clinicsFragment)
//                true
//            }
        }

        R.id.services_item -> {
//            navController.navigate(R.id.setupServicesFragment)
            navController.navigate(R.id.myQuestionsFragment)
            true
        }

        R.id.doctors_item -> {
            navController.navigate(R.id.hospitalDoctorsFragment)
            true
        }

        R.id.clinics_item -> {
            navController.navigate(R.id.clinicsFragment)
            true
        }

        R.id.more_item -> {
//            navController.navigate(R.id.moreSettingsFragment)
            navController.navigate(R.id.moreFragment)

            true
        }

        R.id.schedule_item -> {
            navController.navigate(R.id.appointmentsFragment)
            true
        }

        else -> {
            navController.navigate(R.id.loginFragment)
            true
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun check() {
        val appVersion: String? = ForceUpdateUtils.getAppVersion(this)
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val forceUpdate = firebaseRemoteConfig.getBoolean(KEY_FORCE_UPDATE_REQUIRED)
                val currentVersion = firebaseRemoteConfig.getString(KEY_CURRENT_VERSION)
                if (forceUpdate) {
                    if (currentVersion > appVersion!!) {
                        showDialog()
                    }
                }
            }
        }
    }
    private fun showDialog() {
        dialog.show(supportFragmentManager, "Exit Dialog")
    }

    private fun requestNotificationPermission() {
        // التحقق من أن مستوى API هو Android 13 (TIRAMISU) أو أحدث
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // 1. التحقق أولاً مما إذا كان الإذن ممنوحاً بالفعل
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 2. إذا لم يكن ممنوحاً، نقوم بطلب الإذن من المستخدم
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
            // إذا كان الإذن ممنوحاً، لا يحدث شيء ونتابع العمل
        }
    }

    // (اختياري) معالجة استجابة المستخدم لطلب الإذن
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // تم منح الإذن: يمكن إظهار رسالة تأكيد
                // Log.d("Permission", "Notification permission granted.")
            } else {
                // تم رفض الإذن: يمكن إظهار رسالة توضيحية للمستخدم
                // Log.d("Permission", "Notification permission denied.")
            }
        }
    }

    fun setStatusBarColor(
        ctx: Context,
        activity: AppCompatActivity,
        rootLayout: View,
        window: Window
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            // Android 15+
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                view.setBackgroundColor(Color.WHITE)
                insets
            }

            ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                val statusBarHeight = insets.top

                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = statusBarHeight
                    bottomMargin = insets.bottom
                }

                val statusBarView = View(ctx)
                statusBarView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    statusBarHeight
                )

                statusBarView.setBackgroundColor( ContextCompat.getColor(ctx, R.color.purple_700))
                activity.addContentView(statusBarView, statusBarView.layoutParams)

                WindowInsetsCompat.CONSUMED
            }
        } else {
            // Android 14 و أقل → نستخدم View أيضاً بدل statusBarColor
            val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (resourceId > 0) ctx.resources.getDimensionPixelSize(resourceId) else 80

            val statusBarView = View(ctx)
            statusBarView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight
            )

            // هنا التحويل إلى Drawable
            statusBarView.setBackgroundColor( ContextCompat.getColor(ctx, R.color.purple_700))

            activity.addContentView(statusBarView, statusBarView.layoutParams)
        }

        // ضبط لون الأيقونات — هنا ثابت كما هو
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }


}