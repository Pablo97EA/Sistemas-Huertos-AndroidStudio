package com.moviles.agrocity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moviles.agrocity.models.PestDto
import kotlinx.coroutines.launch
//
import com.moviles.agrocity.repository.PestRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class PestViewModel(
    private val repository: PestRepository = PestRepository()
) : ViewModel() {

    var localPests by mutableStateOf<List<PestDto>>(emptyList())
        private set

    var externalPests by mutableStateOf<List<PestDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchLocalPests() {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = repository.getLocalPests()
                if (response.isSuccessful) {
                    localPests = response.body() ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Error al cargar plagas locales"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchExternalPests() {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = repository.getExternalPests()
                if (response.isSuccessful) {
                    externalPests = response.body() ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Error al cargar plagas"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }
}
