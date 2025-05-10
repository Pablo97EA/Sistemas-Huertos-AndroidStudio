package com.moviles.agrocity.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.agrocity.models.Garden
import com.moviles.agrocity.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class GardenViewModel : ViewModel() {

    private val _garden = MutableStateFlow<List<Garden>>(emptyList())
    val gardens: StateFlow<List<Garden>> get() = _garden

    fun fetchGardens() {
        viewModelScope.launch {
            try {
                _garden.value = RetrofitInstance.api.getGardens()
                Log.i("MyViewModel", "Fetching data from API... ${_garden.value}")
            } catch (e: Exception) {
                Log.e("ViewmodelError", "Error: ${e}")
            }
        }
    }

    fun addGarden(garden: Garden, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            try {
                val namePart = garden.name?.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = garden.description?.toRequestBody("text/plain".toMediaTypeOrNull())
                val createdAtPart = garden.createdAt?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val userIdPart = garden.userId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                val filePart = imageUri?.let {
                    val file = FileUtils.getFileFromUri(context, it)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("File", file.name, requestFile)
                }

                // Llamada a la API
                val response = RetrofitInstance.api.addGarden(
                    namePart,
                    descriptionPart,
                    createdAtPart,
                    userIdPart,
                    filePart
                )

                Log.i("ViewModelInfo", "Jardín creado exitosamente: $response")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun updateGarden(garden: Garden) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "Garden: $garden")

                // Llamada a la API
                val response = RetrofitInstance.api.updateGarden(garden.gardenId, garden)

                // Actualizar el estado de los gardens en el ViewModel
                _garden.value = _garden.value.map { g ->
                    if (g.gardenId == response.gardenId) response else g
                }

                Log.i("ViewModelInfo", "Garden actualizado: $response")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }


    fun deleteGarden(gardenId: Int?) {
        gardenId?.let { id ->
            viewModelScope.launch {
                try {
                    RetrofitInstance.api.deleteGarden(id)
                    _garden.value = _garden.value.filter { it.gardenId != gardenId }
                } catch (e: Exception) {
                    Log.e("ViewModelError", "Error deleting garden: ${e.message}")
                }
            }
        } ?: Log.e("ViewModelError", "Error: gardenId is null")
    }


    object FileUtils {
        fun getFileFromUri(context: Context, uri: Uri): File {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            return tempFile
        }
    }

}