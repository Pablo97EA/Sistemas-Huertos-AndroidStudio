package com.moviles.agrocity.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.agrocity.viewmodel.GardenViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.moviles.agrocity.common.Constants.IMAGES_BASE_URL
import com.moviles.agrocity.models.Garden

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter




@Composable
fun HomeScreen(
    onGoToPests: () -> Unit,
    onGoToGardens: () -> Unit,

    viewModel: GardenViewModel = viewModel(),
    onGoToGemini: () -> Unit,
    onGoToComment: (Int) -> Unit

) {
    val gardens by viewModel.gardens.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchGardens()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)


        Spacer(modifier = Modifier.height(24.dp))
        Text("Huertos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {items(gardens) { garden ->
            PublicGardenItem(garden = garden, onCommentClick = { gardenId ->
                onGoToComment(gardenId)
            })
        }

        }


    }
}

@Composable
fun PublicGardenItem(garden: Garden, onCommentClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        garden.userName?.let {
            Text(text = "👤 Usuario: $it", style = MaterialTheme.typography.bodySmall)
        }

        Text(text = garden.name, style = MaterialTheme.typography.titleLarge)
        Text(text = garden.description, style = MaterialTheme.typography.bodyMedium)
        Text(text = "🌱 Creado: ${garden.createdAt}", style = MaterialTheme.typography.bodySmall)

        garden.imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(IMAGES_BASE_URL + it),
                contentDescription = "Imagen del jardín",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(140.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Button(
            onClick = { onCommentClick(garden.gardenId ?: 0) },
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFAAF683),
                contentColor = Color.Black
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "Comentar",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
