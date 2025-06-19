package com.moviles.agrocity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*
import com.moviles.agrocity.network.GeminiClient

class GeminiImageAnalysisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageAnalyzerScreen()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageAnalyzerScreen() {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var result by remember { mutableStateOf("Toma una foto para analizar la planta") }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val geminiClient = remember { GeminiClient(apiKey = "AIzaSyD_FIqvsukQPWJtkq1wVlrlL5TmMsdFHlg") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        it?.let {
            bitmap = it
            geminiClient.analyzePlantImage(it) { analysis ->
                result = analysis
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Analizar Cultivo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                when {
                    cameraPermissionState.status.isGranted -> {
                        launcher.launch(null)
                    }
                    cameraPermissionState.status.shouldShowRationale -> {
                        result = "La app necesita permiso para usar la cámara."
                        cameraPermissionState.launchPermissionRequest()
                    }
                    else -> {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text(text = "Tomar foto de planta", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Foto de planta",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDFF0D8), shape = MaterialTheme.shapes.medium)
                .border(1.dp, Color(0xFF4CAF50), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ){
        Text(
            text = result,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp)
        )}
    }
}
