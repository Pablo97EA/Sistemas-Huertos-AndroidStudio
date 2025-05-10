package com.moviles.agrocity.models

data class Garden(
    val gardenId: Int,
    val userId: Int?,
    val name: String,
    val description: String,
    val createdAt: String,
    val imageUrl: String?,
    //val user: User? = null
)
