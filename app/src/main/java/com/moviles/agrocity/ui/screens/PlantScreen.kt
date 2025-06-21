package com.moviles.agrocity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.moviles.agrocity.viewmodel.PlantViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.agrocity.models.PlantDto

@Composable
fun PlantScreen(viewModel: PlantViewModel = viewModel()) {
    val plants = viewModel.externalPlants
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.fetchExternalPlants()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Plantas Externas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }
            plants.isEmpty() -> {
                Text("No se encontraron plantas.")
            }
            else -> {
                LazyColumn {
                    items(plants) { plant ->
                        PlantItem(plant = plant)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun PlantItem(plant: PlantDto) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        Text(
            text = plant.plantName ?: "Sin nombre",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        plant.imageUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = plant.plantName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Text("Imagen no disponible")

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = plant.description ?: "Sin descripción disponible",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
