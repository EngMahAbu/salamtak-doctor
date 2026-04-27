package com.salamtak.salamtakdoctor

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.salamtak.BuildConfig
import com.salamtak.core.localDataBase.UserData
import com.salamtak.core.network.NetworkModule
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.core.usecase.UseCaseModule.BASE_URL
import com.salamtak.core.utils.ConnectivityInterceptor
import com.salamtak.salamtakdoctor.salamtak_doctor.di.ViewModelsModule
import com.salamtak.salamtakdoctor.salamtak_doctor.di.appModule
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.lang.ref.WeakReference


class MyApplication : Application() {
    lateinit var sInstance : MyApplication

    companion object{

        val application = Application()

        private const val TAG = "MyApplication"
    }

    override fun onCreate() {
        super.onCreate()
        onTokenRefresh()
        UserData.setup(applicationContext)
        DateUtils.setup(applicationContext)
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule,
                UseCaseModule(applicationContext),
                ViewModelsModule(applicationContext)
                ,NetworkModule(
                    applicationContext,
                    BASE_URL,
                    BuildConfig.DEBUG,
                    arrayListOf(
                        ConnectivityInterceptor(
                            WeakReference(
                                applicationContext
                            )
                        )
                    )
                ))
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        sInstance = this

    }

    fun onTokenRefresh(){
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
            DateUtils.setFcmToken(token)
//            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

    }



}