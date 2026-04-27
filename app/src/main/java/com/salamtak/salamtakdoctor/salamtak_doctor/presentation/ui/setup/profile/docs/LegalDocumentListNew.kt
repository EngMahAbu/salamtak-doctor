package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.profile.docs

class LegalDocumentListNew (
    var id : Int,

    var titleName : String ,
    var items : MutableList<Item>,
    var LegalDocumentTypeId: Int = 0
     )


data class Item(
//    var IdFront: Int = 0,
//    var IdBack: Int = 0,
//    var LegalDocumentTypeName: String = "",
    var DocumentUrlFront: String? = null,
    var DocumentUrlBack: String? = null,
    var LegalDocumentTypeId: Int = 0
        )