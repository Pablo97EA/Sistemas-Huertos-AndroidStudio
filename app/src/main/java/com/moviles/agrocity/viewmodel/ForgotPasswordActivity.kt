package com.moviles.agrocity.viewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.agrocity.models.ForgotPasswordDTO
import com.moviles.agrocity.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreen()
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperar Contraseña",
            color = Color(0xFF4CAF50),
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp)
        )

        Button(
            onClick = {
                if (!isValidEmail(email)) {
                    showToast(context, "Ingrese un correo válido")
                } else {
                    sendResetEmail(email, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Enviar enlace", color = Color.White)
        }
    }
}

private fun sendResetEmail(email: String, context: android.content.Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.forgotPassword(ForgotPasswordDTO(email))
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    showToast(context, "Revisa tu correo para recuperar la contraseña")
                } else {
                    showToast(context, "No se pudo enviar el correo. Intenta de nuevo.")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToast(context, "Error: ${e.message}")
            }
        }
    }
}

private fun showToast(context: android.content.Context, message: String) {
    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
}
