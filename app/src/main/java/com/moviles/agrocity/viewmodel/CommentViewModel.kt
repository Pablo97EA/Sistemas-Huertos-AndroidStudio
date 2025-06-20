package com.moviles.agrocity.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.agrocity.models.Comment
import com.moviles.agrocity.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {
    private val apiService = RetrofitInstance.api

    // Estados para los comentarios
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    // Estado de la UI
    private val _uiState = MutableStateFlow(CommentUiState())
    val uiState: StateFlow<CommentUiState> = _uiState.asStateFlow()

    // Obtener comentarios por publicación (suspendible)
    suspend fun fetchCommentsByGardenSuspend(gardenId: Int): Boolean {
        _uiState.update { it.copy(isLoading = true, error = null, updateSuccess = false) }

        return try {
            val response = apiService.getCommentsByPublication(gardenId)
            if (response.isSuccessful) {
                val fetchedComments = response.body() ?: emptyList()
                Log.d("CommentViewModel", "fetchCommentsByGarden: recibidos ${fetchedComments.size} comentarios")
                _comments.value = fetchedComments
                _uiState.update { it.copy(isLoading = false, error = null) }
                true
            } else {
                Log.e("CommentViewModel", "Error al cargar comentarios: ${response.code()}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar comentarios: ${response.code()}",
                        updateSuccess = false
                    )
                }
                false
            }
        } catch (e: Exception) {
            Log.e("CommentViewModel", "Error de conexión en fetchCommentsByGarden", e)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}",
                    updateSuccess = false
                )
            }
            false
        }
    }

    // Método normal que lanza coroutine para UI
    fun fetchCommentsByGarden(gardenId: Int) {
        viewModelScope.launch {
            fetchCommentsByGardenSuspend(gardenId)
        }
    }

    // Crear nuevo comentario
    fun createComment(comment: Comment) {
        _uiState.update { it.copy(isLoading = true, error = null, updateSuccess = false) }

        viewModelScope.launch {
            try {
                val response = apiService.createComment(comment)
                if (response.isSuccessful) {
                    delay(500) // Espera para que el backend procese bien
                    fetchCommentsByGarden(comment.gardenId ?: 0)
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("API_ERROR", "Error al crear comentario: ${response.code()} - $errorMessage")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error al crear comentario. Revisa Logcat para más detalles.",
                            updateSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Excepción al crear comentario", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión. Revisa Logcat para más detalles.",
                        updateSuccess = false
                    )
                }
            }
        }
    }

    // Eliminar comentario
    fun deleteComment(commentId: Int, gardenId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null, updateSuccess = false) }

        viewModelScope.launch {
            try {
                val response = apiService.deleteComment(commentId)
                if (response.isSuccessful) {
                    fetchCommentsByGarden(gardenId)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error al eliminar comentario: ${response.code()}",
                            updateSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión: ${e.localizedMessage}",
                        updateSuccess = false
                    )
                }
            }
        }
    }

    // Actualizar comentario
    fun updateComment(commentId: Int, updatedComment: Comment) {
        _uiState.update { it.copy(isLoading = true, error = null, updateSuccess = false) }

        viewModelScope.launch {
            try {
                val response = apiService.updateComment(commentId, updatedComment)
                if (response.isSuccessful) {
                    delay(500) // Evita race condition
                    fetchCommentsByGarden(updatedComment.gardenId ?: 0)
                    val success = fetchCommentsByGardenSuspend(updatedComment.gardenId ?: 0)
                    if (success) {
                        _uiState.update { it.copy(isLoading = false, updateSuccess = true, error = null) }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error al actualizar comentario: ${response.code()}",
                            updateSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión: ${e.localizedMessage}",
                        updateSuccess = false
                    )
                }
            }
        }
    }

    // Limpiar errores y flags
    fun clearError() {
        _uiState.update { it.copy(error = null, updateSuccess = false) }
    }
}

// Estado de la UI
data class CommentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val updateSuccess: Boolean = false
)
