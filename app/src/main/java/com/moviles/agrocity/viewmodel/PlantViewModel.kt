package com.moviles.agrocity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.moviles.agrocity.models.PlantDto
import com.moviles.agrocity.repository.PlantRepository

class PlantViewModel(
    private val repository: PlantRepository = PlantRepository()
) : ViewModel() {

    var externalPlants by mutableStateOf<List<PlantDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchExternalPlants() {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = repository.getExternalPlants()
                if (response.isSuccessful) {
                    externalPlants = response.body() ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Error al cargar plantas externas"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }
}
