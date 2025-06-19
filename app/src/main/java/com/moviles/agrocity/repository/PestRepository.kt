package com.moviles.agrocity.repository

import com.moviles.agrocity.models.PestDto
import com.moviles.agrocity.network.RetrofitInstance
import retrofit2.Response

class PestRepository {
    suspend fun getLocalPests(): Response<List<PestDto>> {
        return RetrofitInstance.api.getAllPests()
    }

    suspend fun getExternalPests(): Response<List<PestDto>> {
        return RetrofitInstance.api.getExternalPests()
    }
}