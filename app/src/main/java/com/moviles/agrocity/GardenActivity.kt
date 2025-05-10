package com.moviles.agrocity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.moviles.agrocity.models.Garden
import com.moviles.agrocity.ui.theme.AgrocityTheme
import com.moviles.agrocity.viewmodel.GardenViewModel


class GardenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AgrocityTheme {
                val viewModel: GardenViewModel = viewModel()
                GardenScreen(viewModel)
            }
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AgrocityTheme{
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun GardenScreenPreview(){
    AgrocityTheme {
        var viewModel: GardenViewModel = viewModel()
        GardenScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(viewModel: GardenViewModel) {

    val gardens by viewModel.gardens.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedGarden by remember { mutableStateOf<Garden?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    LaunchedEffect(Unit) {
        Log.i("Activity", "Coming here???")
        viewModel.fetchGardens()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Jardín") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedGarden = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Jardín")
            }
        }


    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Button with padding
            Button(
                modifier = Modifier
                    .padding(16.dp) // Add padding around the button
                    .fillMaxWidth(),
                onClick = { viewModel.fetchGardens() }
            ) {
                Text("Refrescar Events")
            }

            // Spacer to ensure some space between button and the list
            Spacer(modifier = Modifier.height(8.dp))

            GardenList(gardens,
                onEdit = { garden ->
                    selectedGarden = garden
                    showDialog = true
                },
                onDelete = { garden -> viewModel.deleteGarden(garden.gardenId) })
        }
    }
    if (showDialog) {
        val context = LocalContext.current
        GardenDialog(
            garden = selectedGarden,
            onDismiss = { showDialog = false },
            onSave = { garden ->
                if (garden.gardenId == 0) viewModel.addGarden(garden, imageUri, context)
                else viewModel.updateGarden(garden)
                showDialog = false
            },
            imageUri = imageUri,
            onImageSelected = { uri -> imageUri = uri }
        )
    }

}

@Composable
fun GardenList(gardens: List<Garden>, onEdit: (Garden) -> Unit, onDelete: (Garden) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(gardens) { garden ->
            GardenItem(garden = garden, onEdit = onEdit, onDelete = onDelete)
        }
    }
}

@Composable
fun GardenItem(garden: Garden, onEdit: (Garden) -> Unit, onDelete: (Garden) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Mostrar el userId si es necesario
            Text(text = "User ID: ${garden.userId}", style = MaterialTheme.typography.bodySmall)
            Text(text = garden.name, style = MaterialTheme.typography.titleLarge)
            Text(text = garden.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "🌱 Created At: ${garden.createdAt}", style = MaterialTheme.typography.bodySmall)

            // Si hay una URL de imagen, mostrar la imagen
            garden.imageUrl?.let {
                Image(painter = rememberImagePainter(it), contentDescription = "Garden Image", modifier = Modifier.fillMaxWidth().height(200.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onEdit(garden) }) {
                    Text("Edit", color = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = { onDelete(garden) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
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
    onImageSelected: (Uri?) -> Unit
) {
    var name by remember { mutableStateOf(garden?.name ?: "") }
    var description by remember { mutableStateOf(garden?.description ?: "") }
    var createdAt by remember { mutableStateOf(garden?.createdAt ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        onImageSelected(it)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (garden == null) "Agregar Jardín" else "Editar Jardín") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { showDatePicker = true }
                        .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50))
                        .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50))
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (createdAt.isEmpty()) "Seleccionar Fecha" else createdAt,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Seleccionar Imagen")
                }

                imageUri?.let {
                    Image(
                        painter = rememberImagePainter(it),
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
                val updatedGarden = Garden(
                    gardenId = garden?.gardenId ?: 0,
                    userId = garden?.userId,  // o asigna el userId actual si es obligatorio
                    name = name,
                    description = description,
                    createdAt = createdAt,
                    imageUrl = imageUri?.toString()
                )
                onSave(updatedGarden)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { selectedDateMillis ->
                selectedDateMillis?.let {
                    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    createdAt = formatter.format(java.util.Date(it))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

