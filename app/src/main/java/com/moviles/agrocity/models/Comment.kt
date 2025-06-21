package com.moviles.agrocity.models

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("commentId") val commentId: Int?,
    @SerializedName("userId") val userId: Int?,
    @SerializedName("gardenId") val gardenId: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("garden") val garden: Garden?,
    @SerializedName("user") val user: User?,
    @SerializedName("name") val name: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("surname") val surname: String?,
    @SerializedName("userName") val userName: String?

)