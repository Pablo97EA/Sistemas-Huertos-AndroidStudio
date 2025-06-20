package com.moviles.agrocity.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.moviles.agrocity.alerts.safeToast
import com.moviles.agrocity.common.Constants.IMAGES_BASE_URL
import com.moviles.agrocity.models.Garden
import com.moviles.agrocity.viewmodel.GardenViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(viewModel: GardenViewModel = viewModel(), userId: Int) {
    val gardens by viewModel.gardens.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedGarden by remember { mutableStateOf<Garden?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var gardenToDelete by remember { mutableStateOf<Garden?>(null) }


    LaunchedEffect(userId) {
        viewModel.fetchGardensByUser(userId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Huerto") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedGarden = null
                    imageUri = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Huerto")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                onClick = { viewModel.fetchGardensByUser(userId) }
            ) {
                Text("Refrescar Huerto")
            }
            Spacer(modifier = Modifier.height(8.dp))
            GardenList(
                gardens = gardens,
                onEdit = { garden ->
                    selectedGarden = garden
                    imageUri = garden.imageUrl?.let { Uri.parse(it) }
                    showDialog = true
                },
                onDelete = { garden ->
                    //viewModel.deleteGarden(garden.gardenId)
                    gardenToDelete = garden
                }
            )
        }
    }

    gardenToDelete?.let { garden ->
        AlertDialog(
            onDismissRequest = { gardenToDelete = null },
            text = { Text("¿Desea eliminar el huerto '${garden.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGarden(garden.gardenId)
                    safeToast(context, "Huerto eliminado con éxito")
                    gardenToDelete = null
                }) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { gardenToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDialog) {
        GardenDialog(
            garden = selectedGarden,
            onDismiss = {
                showDialog = false
                imageUri = null
            },
            onSave = { garden ->
                if (garden.gardenId == 0) {
                    viewModel.addGarden(garden, imageUri, context, userId)
                    safeToast(context, "Huerto registrado con éxito")
                } else {
                    viewModel.updateGarden(garden, imageUri, context, userId)
                    safeToast(context, "Huerto actualizado con éxito")
                }

                showDialog = false
                imageUri = null
            },
            imageUri = imageUri,
            onImageSelected = { uri -> imageUri = uri },
            userId = userId
        )
    }
}

@Composable
fun GardenList(
    gardens: List<Garden>,
    onEdit: (Garden) -> Unit,
    onDelete: (Garden) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(gardens) { garden ->
            GardenItem(garden = garden, onEdit = onEdit, onDelete = onDelete)
        }
    }
}

@Composable
fun GardenItem(
    garden: Garden,
    onEdit: (Garden) -> Unit,
    onDelete: (Garden) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            garden.userName?.let {
                Text(text = "👤 Usuario: $it", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = garden.name, style = MaterialTheme.typography.titleLarge)
            Text(text = garden.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "🌱 Created At: ${garden.createdAt}", style = MaterialTheme.typography.bodySmall)
            /*garden.imageUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(IMAGES_BASE_URL + it),
                    contentDescription = "Garden Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            }*/
            garden.imageUrl?.takeIf { it.isNotBlank() }?.let {
                runCatching {
                    Image(
                        painter = rememberAsyncImagePainter(IMAGES_BASE_URL + it),
                        contentDescription = "Garden Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentScale = ContentScale.Crop
                    )
                }.onFailure { error ->
                    Log.e("ImageLoadError", "Error cargando imagen: $it", error)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onEdit(garden) }) {
                    Text("Editar", color = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = { onDelete(garden) }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
@Composable
fun GardenDialog(
    garden: Garden?,
    onDismiss: () -> Unit,
    onSave: (Garden) -> Unit,
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    userId: Int
) {
    var name by remember { mutableStateOf(garden?.name ?: "") }
    var description by remember { mutableStateOf(garden?.description ?: "") }
    var createdAt by remember { mutableStateOf(garden?.createdAt ?: "") }
    var showConfirmSave by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImageSelected(uri)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (garden == null) "Agregar huerto" else "Editar huerto") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, maxLines = 3)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (createdAt.isEmpty()) "Seleccionar Fecha" else createdAt,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Seleccionar Imagen")
                }
                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) {
                    safeToast(context, "El nombre es obligatorio")

                    //Toast.makeText(context, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                showConfirmSave = true  // SOLO MOSTRAR CONFIRMACIÓN, NO GUARDAR AÚN
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )

    // Diálogo de confirmación
    if (showConfirmSave) {
        AlertDialog(
            onDismissRequest = { showConfirmSave = false },

            text = {
                Text(if (garden == null) "¿Desea registrar este huerto?" else "¿Desea actualizar este huerto?")
            },
            confirmButton = {
                TextButton(onClick = {
                    val updatedGarden = Garden(
                        gardenId = garden?.gardenId ?: 0,
                        userId = userId,
                        name = name.trim(),
                        description = description.trim(),
                        createdAt = createdAt,
                        imageUrl = imageUri?.toString(),
                        userName = garden?.userName
                    )
                    onSave(updatedGarden)
                    showConfirmSave = false
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSave = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { selectedDateMillis ->
                selectedDateMillis?.let {
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    createdAt = formatter.format(Date(it))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}


