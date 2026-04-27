package com.salamtak.salamtakdoctor.statistics.model


import com.google.gson.annotations.SerializedName

data class InsuranceCompanyRequest(
    @SerializedName("InsuranceCompaniesList")
    val insuranceCompaniesList: List<InsuranceCompanies>
)

data class InsuranceCompanies(
    @SerializedName("HealthEntityID")
    val healthEntityID: Int,
    @SerializedName("InsCompanyID")
    val insCompanyID: String,
    @SerializedName("InsuranceCompanyName")
    val insuranceCompanyName: String
)