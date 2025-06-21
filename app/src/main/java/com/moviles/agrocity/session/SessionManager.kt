package com.moviles.agrocity.session

object SessionManager {
    var userId: Int? = null
    var token: String? = null
    var name: String? = null
    var firstName: String? = null
    var surname: String? = null

    fun clearSession() {
        userId = null
        token = null
        name = null
        firstName = null
        surname = null
    }
}