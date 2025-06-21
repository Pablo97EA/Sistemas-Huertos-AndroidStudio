package com.moviles.agrocity.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.moviles.agrocity.R
import com.moviles.agrocity.common.Constants.IMAGES_BASE_URL
import com.moviles.agrocity.models.Comment
import com.moviles.agrocity.session.SessionManager
import com.moviles.agrocity.viewmodel.GardenViewModel
import com.moviles.agrocity.viewmodels.CommentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    gardenId: Int,
    commentViewModel: CommentViewModel = viewModel(),
    gardenViewModel: GardenViewModel = viewModel()
) {
    val garden by gardenViewModel.selectedGarden.collectAsState()
    val comments by commentViewModel.comments.collectAsState()
    val uiState by commentViewModel.uiState.collectAsState()

    var newCommentText by remember { mutableStateOf("") }
    var editingCommentId by remember { mutableStateOf<Int?>(null) }
    var editingCommentText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val currentUserId = SessionManager.userId ?: 0


    LaunchedEffect(gardenId) {
        gardenViewModel.fetchGardenById(gardenId)
        commentViewModel.fetchCommentsByGarden(gardenId)
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            editingCommentId = null
            editingCommentText = ""
            commentViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Column(
                        modifier = Modifier.padding(start = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(90.dp)
                        )
                    }
                },
                title = {
                    Text(
                        "Comentarios del Jardín",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (garden == null) {
                Text("Cargando jardín...")
                return@Column
            }

            AsyncImage(
                model = IMAGES_BASE_URL + (garden?.imageUrl ?: ""),
                contentDescription = "Foto del Jardín",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 16.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(
                        comment = comment,
                        onDelete = { commentViewModel.deleteComment(comment.commentId ?: 0, gardenId) },
                        onEdit = {
                            editingCommentId = comment.commentId
                            editingCommentText = comment.description ?: ""
                        },
                        currentUserId = currentUserId // <-- Aquí se pasa
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (editingCommentId != null) {
                OutlinedTextField(
                    value = editingCommentText,
                    onValueChange = { editingCommentText = it },
                    label = { Text("Editar comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        editingCommentId = null
                        editingCommentText = ""
                    }) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        editingCommentId?.let { id ->
                            val updated = comments.find { it.commentId == id }?.copy(description = editingCommentText)
                            if (updated != null) {
                                commentViewModel.updateComment(id, updated)
                            }
                        }
                    }) {
                        Text("Guardar")
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Nuevo comentario") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                commentViewModel.createComment(
                                    Comment(
                                        commentId = 0,
                                        userId = currentUserId,
                                        gardenId = gardenId,
                                        description = newCommentText,
                                        createdAt = null,
                                        garden = null,
                                        name = null,
                                        firstName = null,
                                        surname = null,
                                        user = null,
                                        userName = null
                                    )
                                )
                                newCommentText = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar comentario",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun CommentItem(
    comment: Comment,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    currentUserId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = comment.user?.name?.take(1)?.uppercase() ?: " ",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val baseName = comment.user?.name ?: comment.userName ?: "Usuario"
                val displayName = if (!comment.firstName.isNullOrBlank()) {
                    "$baseName ${comment.firstName}"
                } else {
                    baseName
                }

                Text(
                    text = displayName.ifBlank { "Usuario" },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatDate(comment.createdAt as Date?),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = comment.description ?: "",
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Log.d("CommentItem", "comment.userId = ${comment.userId}, currentUserId = $currentUserId")

            if (comment.userId == currentUserId){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onEdit) {
                        Text("Editar", style = MaterialTheme.typography.labelSmall)
                    }
                    TextButton(onClick = onDelete) {
                        Text(
                            "Eliminar",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}


fun formatDate(date: Date?): String {
    return date?.let {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(it)
    } ?: ""
}
