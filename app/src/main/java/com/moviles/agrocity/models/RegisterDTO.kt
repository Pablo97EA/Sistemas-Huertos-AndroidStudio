package com.moviles.agrocity.models

data class RegisterDTO(
    val name: String?,
    val firstName: String?,
    val surname: String?,
    val age: Int?,
    val telephone: String?,
    val email: String,
    val password: String
)
