package com.moviles.agrocity.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onGoToPests: () -> Unit,
    onGoToPlants: () -> Unit,
    onGoToGardens: () -> Unit,
    onGoToGemini: () -> Unit,
    onGoToComment: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pantalla principal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onGoToPests) {
            Text("Ver plagas externas")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onGoToPlants) {
            Text("Ver plantas externas")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onGoToGardens) {
            Text("Ver mis jardines")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onGoToGemini) {
            Text("Analizar cultivo (Gemini)")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { onGoToComment(1) }) {
            Text("Ver comentarios del jardín 1")
        }


    }
}