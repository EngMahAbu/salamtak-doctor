package com.salamtak.salamtakdoctor.salamtak_doctor.di

import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.salamtak.core.utils.SessionManagement
import com.salamtak.core.usecase.UseCaseModule.PREF_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single { androidContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }

    single { get<SharedPreferences>().edit() }

    single { SessionManagement(get(), get()) }


    single {
        Glide.with(androidContext())
            .setDefaultRequestOptions(get())
    }
}