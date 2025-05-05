package com.moviles.agrocity.models

data class LoginDTO (
    val email: String,
    val password: String = ""
)