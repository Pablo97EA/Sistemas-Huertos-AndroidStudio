package com.moviles.agrocity.models

data class PestDto(
    val pestId: Int,
    val commonName: String?,
    val scientificName: String?,
    val description: String?,
    val solution: String?,
    val host: String?,
    val imageUrl: String?
)
