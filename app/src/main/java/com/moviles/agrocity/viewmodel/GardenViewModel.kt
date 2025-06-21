package com.moviles.agrocity.viewmodel


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.plus


class GardenViewModel : ViewModel() {


    private val _gardens = MutableStateFlow<List<Garden>>(emptyList())
    val gardens: StateFlow<List<Garden>> = _gardens


    fun fetchGardensByUser(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGardensByUserId(userId)
                _gardens.value = response
            } catch (e: Exception) {
                Log.e("GardenViewModel", "Error al obtener los jardines: ${e.message}", e)
            }
        }
    }

    fun fetchGardens() {
        viewModelScope.launch {
            try {
                _gardens.value = RetrofitInstance.api.getGardens()
                Log.i("MyViewModel", "Fetching data from API... ${_gardens.value}")
            } catch (e: Exception) {
                Log.e("ViewmodelError", "Error: ${e}")
            }
        }
    }


    fun addGarden(garden: Garden, imageUri: Uri?, context: Context, userId: Int) {
        viewModelScope.launch {
            try {
                // Convertir los campos a RequestBody
                val userIdPart = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val namePart = garden.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart =
                    garden.description.toRequestBody("text/plain".toMediaTypeOrNull())
                val createdAtPart = garden.createdAt.toRequestBody("text/plain".toMediaTypeOrNull())

                // Convertir Uri a MultipartBody.Part
                val filePart = imageUri?.let {
                    val file = FileUtils.getFileFromUri(context, it)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("File", file.name, requestFile)
                }
                // Llamar a la API
                val response = RetrofitInstance.api.addGarden(
                    userIdPart, namePart, descriptionPart, createdAtPart, filePart
                )
                fetchGardensByUser(userId)
                _gardens.value += response
                Log.i("ViewModelInfo", "Jardín creado exitosamente: $response")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }


    // Reemplaza tu updateGarden con esto:
    fun updateGarden(garden: Garden, imageUri: Uri?, context: Context, userId: Int) {
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val createdAtString = try {
                    dateFormat.format(dateFormat.parse(garden.createdAt))
                } catch (e: Exception) {
                    dateFormat.format(Date())
                }

                val userIdPart = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val namePart = garden.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = (garden.description ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val createdAtPart = createdAtString.toRequestBody("text/plain".toMediaTypeOrNull())

                val filePart = imageUri?.let {
                    val file = FileUtils.getFileFromUri(context, it)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("File", file.name, requestFile)
                }

                RetrofitInstance.api.updateGarden(
                    garden.gardenId,
                    userIdPart,
                    namePart,
                    descriptionPart,
                    createdAtPart,
                    filePart
                )
                val updatedGardens = RetrofitInstance.api.getGardensByUserId(userId)
                _gardens.value = updatedGardens

                Log.i("ViewModelInfo", "Jardín actualizado correctamente")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }



    fun deleteGarden(gardenId: Int?) {
        gardenId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.deleteGarden(id)
                    if (response.isSuccessful) {
                        _gardens.value = _gardens.value.filter { it.gardenId != id }
                        Log.i("GardenViewModel", "Garden deleted: $id")
                    } else {
                        Log.e(
                            "GardenViewModel",
                            "Error deleting garden: ${response.errorBody()?.string()}"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("GardenViewModel", "Error deleting garden: ${e.message}")
                }
            }
        } ?: Log.e("GardenViewModel", "Error: gardenId is null")
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

    private val _selectedGarden = MutableStateFlow<Garden?>(null)
    val selectedGarden: StateFlow<Garden?> = _selectedGarden

    fun fetchGardenById(gardenId: Int = 1) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGardenById(gardenId)  // Cambia el tipo de retorno en la interfaz también
                Log.d("GARDEN_API", "Jardín recibido: $response")
                _selectedGarden.value = response.data
            } catch (e: Exception) {
                Log.e("GARDEN_API", "Error al obtener jardín", e)
                _selectedGarden.value = null
            }
        }
    }





}
