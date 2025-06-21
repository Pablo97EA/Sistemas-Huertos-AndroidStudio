package com.moviles.agrocity.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.moviles.agrocity.viewmodel.PestViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.agrocity.models.PestDto

@Composable
fun PestScreen(viewModel: PestViewModel = viewModel()) {
    val pests = viewModel.externalPests
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    // Ejecuta la carga una sola vez
    LaunchedEffect(Unit) {
        viewModel.fetchExternalPests()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Plagas", style = MaterialTheme.typography.headlineMedium)

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

            pests.isEmpty() -> {
                Text("No se encontraron plagas.")
            }

            else -> {
                LazyColumn {
                    items(pests) { pest ->
                        PestItem(pest = pest)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun PestItem(pest: PestDto) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        // Nombre común y científico
        Text(
            text = pest.commonName ?: "Sin nombre",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = pest.scientificName ?: "Sin nombre científico",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Imagen (si existe)
        pest.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = pest.commonName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Text("Imagen no disponible")

        Spacer(modifier = Modifier.height(4.dp))

        // Descripción
        Text(
            text = pest.description ?: "Sin descripción disponible",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Host
        Text(
            text = "Hospedantes: ${pest.host ?: "No especificado"}",
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Solución
        Text(
            text = "Solución: ${pest.solution ?: "No disponible"}",
            style = MaterialTheme.typography.labelSmall
        )
    }
}
