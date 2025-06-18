package com.moviles.agrocity.models

import com.google.gson.annotations.SerializedName


data class Garden(
    val gardenId: Int,
    val userId: Int?,
    val name: String,
    val description: String,
    val createdAt: String,
    val imageUrl: String?,
    val userName: String?
)




