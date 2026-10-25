package com.salamtak.domain.entities.response.hospital

/** Hospital/GetHospitalOwner (Hospital Admin tab). */
data class HospitalOwnerResponse(
    val Data: HospitalOwner?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalOwner(
    val OwnerId: Int?,
    val Name: String?,
    val NameAr: String?,
    val IdNumber: String?,
    val IdImageFront: String?,
    val IdImageBack: String?,
    val Email: String?,
    val Phone: String?,
    val Mobile1: String?,
    val Mobile2: String?,
    val UserType: Int?,
    val UserID: Int?
)

/** Hospital/GetHospitalBranches. */
data class HospitalBranchesResponse(
    val Data: List<HospitalBranch>?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalBranch(
    val BranchId: Int,
    val HospitalId: Int?,
    val Name: String?,
    val NameAr: String?,
    val CityId: Int?,
    val CityName: String?,
    val AreaId: Int?,
    val AreaName: String?,
    val Address: String?,
    val Latitude: String?,
    val Longitude: String?,
    val Phone: String?,
    val IsMain: Boolean?,
    val IsActive: Boolean?
)

/** Body of Hospital/CreateHospitalBranch; [BranchId] and [IsActive] are only sent on update. */
data class HospitalBranchRequest(
    val HospitalId: Int,
    val Name: String,
    val NameAr: String,
    val CityId: Int?,
    val AreaId: Int?,
    val Address: String?,
    val Latitude: String?,
    val Longitude: String?,
    val Phone: String?,
    val IsMain: Boolean,
    val BranchId: Int? = null,
    val IsActive: Boolean? = null
)

/** Id/Name lookups such as Services/GetServices and Services/GetFeatures. */
data class HospitalLookupResponse(
    val Data: List<HospitalLookup>?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalLookup(
    val Id: Int,
    val Name: String?,
    /** Only LookUp/GetDurationMedicalExamination: minutes */
    val Duration: Int? = null
)

/** Responses of the Hospital save/delete calls; only the message and status are used. */
data class HospitalActionResponse(
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)
