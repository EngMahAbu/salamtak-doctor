package com.salamtak.domain.entities.response

data class LegalDocumentsItemsResponse(
    val Data: List<LegalDocumentsData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)
//
//data class LegalDocumentsData(
//    val Id: Int,
//    val Name: String,
//    var imageUri: String? = null
//)


//data class LegalDocumentsData(
//    val Id: Int,
//    val LegalDocumentTypeName: String,
//    var DocumentUrl: String? = null,
////    val DocumentUrl2: String? = null,
//    val LegalDocumentTypeId: Int
//    )


data class LegalDocumentsData(
    val Doc_Desc: String,
    val DocumentUrl: String?,
    val DocumentOrder: Int,
    val DocumentUrl2: String?,
    val Id: Int,
    val LegalDocumentTypeId: Int,
    val LegalDocumentTypeName: String,
    val ProfileStatus: Int,
    val RequiredImages: Int,
    val ImageCount: Int,
    var URLs: List<URL>
)

data class URL(
    val DocumentOrder: Int,
    val LegalDocumentTypeId: Int,
    val URL: String,
    var id: Int
)