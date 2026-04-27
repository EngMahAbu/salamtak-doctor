package com.salamtak.salamtakdoctor.salamtak_doctor.di

import android.content.Context
import com.salamtak.core.network.NetworkModule
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.reflect.KClass

object ViewModelsModule : (Context) -> Module {


    const val AUTH_VIEW_MODEL = "com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SignUpViewModel"

    const val LOGIN_VIEW_MODEL = "com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.LoginViewModel"
    const val SETUP_VIEW_MODEL = "com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupProfileViewModel"
    const val SETUP_Clinic_VIEW_MODEL = "com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupClinicViewModel"
    const val SETUP_SERVICE_VIEW_MODEL = "com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms.SetupServicesViewModel"

    override fun invoke(p1: Context): Module = module {

        viewModel(StringQualifier(AUTH_VIEW_MODEL)) {
            SignUpViewModel(get(),get(StringQualifier(UseCaseModule.REGISTER)),
                get(StringQualifier(UseCaseModule.CREATE_USER))
            )
        } bind SignUpViewModel::class

        viewModel(StringQualifier(LOGIN_VIEW_MODEL)) {
            LoginViewModel(get(),get(StringQualifier(UseCaseModule.LOGIN))
            )
        } bind LoginViewModel::class


        viewModel(StringQualifier(SETUP_VIEW_MODEL)) {
            SetupProfileViewModel(get(),
                get(StringQualifier(UseCaseModule.GET_SUB_SPECIALTIES)),
                get(StringQualifier(UseCaseModule.CREATE_SUB_SPECIALIST)),
                get(StringQualifier(UseCaseModule.GET_SPECIALIST)),
                get(StringQualifier(UseCaseModule.GET_SENIORITY_LEVELS)),
                get(StringQualifier(UseCaseModule.CREATE_DOCTOR_PROFILE)),
                get(StringQualifier(UseCaseModule.CREATE_EXTRA_INFO)),
                get(StringQualifier(UseCaseModule.GET_COUNTRIES)),
                get(StringQualifier(UseCaseModule.GET_LEGAL_DOCUMENTS_ITEMS)),
                get(StringQualifier(UseCaseModule.DELETE_DOCUMENT)),
                get(StringQualifier(UseCaseModule.DELETE_CERTIFICATE)),
                get(StringQualifier(UseCaseModule.GET_DOCTOR_CERTIFICATES)),
                get(StringQualifier(UseCaseModule.GET_NEW_DOCUMENT_DOCTOR))
            )
        } bind SignUpViewModel::class as KClass<SetupProfileViewModel>

        viewModel(StringQualifier(SETUP_Clinic_VIEW_MODEL)) {
            SetupClinicViewModel(
                get(),
                get(StringQualifier(NetworkModule.CONTENT_RETROFIT_CLIENT)),
                get(StringQualifier(UseCaseModule.GET_SERVICES_BY_SPECIALIST)),
                get(StringQualifier(UseCaseModule.GET_SERVICES)),
                get(StringQualifier(UseCaseModule.GET_AREAS)),
                get(StringQualifier(UseCaseModule.GET_CITIES)),
                get(StringQualifier(UseCaseModule.GET_CLINIC_GALLERY)),
                get(StringQualifier(UseCaseModule.DELETE_CLINIC_GALLERY)),
                get(StringQualifier(UseCaseModule.GET_CLINIC_SCHEDULE_BY_CLINIC)),
                get(StringQualifier(UseCaseModule.GET_CLINIC_SCHEDULE_BY_DAY)),
                get(StringQualifier(UseCaseModule.GET_MEDICAL_EXAMINATION_DURATION)),
                get(StringQualifier(UseCaseModule.CREATE_CLINIC_SCHEDULE)),
                get(StringQualifier(UseCaseModule.EDIT_SCHEDULE)),
                get(StringQualifier(UseCaseModule.GET_CLINICS)),
                get(StringQualifier(UseCaseModule.SET_LOCATION)),

            )
        }

        viewModel(StringQualifier(SETUP_SERVICE_VIEW_MODEL)) {
            SetupServicesViewModel(
                get(),
                get(StringQualifier(NetworkModule.CONTENT_RETROFIT_CLIENT)),
                get(StringQualifier(UseCaseModule.GET_SERVICES_TYPES)),
                get(StringQualifier(UseCaseModule.GET_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)),
                get(StringQualifier(UseCaseModule.CREATE_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)),
                get(StringQualifier(UseCaseModule.GET_MEDICAL_EXAMINATION_DURATION)),
                get(StringQualifier(UseCaseModule.EDIT_SCHEDULE)),
                get(StringQualifier(UseCaseModule.GET_POlyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)),
                get(StringQualifier(UseCaseModule.CREATE_PolyClinic_DOCTOR_SERVICE_SCHEDULE_BY_SERVICE_ID)),
                get(StringQualifier(UseCaseModule.CREATE_CLINIC_SCHEDULE)),
            )
        }

    }

}