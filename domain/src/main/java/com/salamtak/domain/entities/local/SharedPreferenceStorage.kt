package com.salamtak.domain.entities.local

interface SharedPreferenceStorage {

    var language: String
    var refrashToken: String
    var accessToken: String


     fun clearStorage()
}