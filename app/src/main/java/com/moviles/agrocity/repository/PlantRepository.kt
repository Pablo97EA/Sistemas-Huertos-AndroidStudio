package com.moviles.agrocity.repository

import com.moviles.agrocity.models.PestDto
import com.moviles.agrocity.models.PlantDto
import com.moviles.agrocity.network.RetrofitInstance
import retrofit2.Response

class PlantRepository {
    suspend fun getLocalPlants(): Response<List<PlantDto>> {
        return RetrofitInstance.api.getAllPlants()
    }
    suspend fun getExternalPlants(): Response<List<PlantDto>> {
        return RetrofitInstance.api.getExternalPlants()
    }

}
