package com.moviles.agrocity.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.agrocity.MainActivity
import com.moviles.agrocity.models.LoginDTO
import com.moviles.agrocity.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.moviles.agrocity.session.SessionManager


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("AgroCityPrefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContent {
            LoginScreen()
        }


    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text(
            text = "AGROCITY",
            color = Color(0xFF4CAF50),
            fontSize = 36.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Mensaje de bienvenida
        Text(
            text = "¡Bienvenido!",
            color = Color.Black,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo de email
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

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Botón de login
        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    showToast(context, "Por favor ingrese email y contraseña")
                } else if (!isValidEmail(email)) {
                    showToast(context, "Ingrese un correo electrónico válido")
                } else {
                    loginUser(email, password, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Iniciar Sesión", color = Color.White)
        }

        // Enlaces
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }
            ) {
                Text("Registrarse", color = Color(0xFF4CAF50))
            }

            Spacer(modifier = Modifier.width(16.dp))

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
                }
            ) {
                Text("Olvidé mi contraseña", color = Color(0xFF4CAF50))
            }
        }
    }
}

@SuppressLint("CommitPrefEdits")
private fun loginUser(email: String, password: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.loginUser(LoginDTO(email, password))

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.get("token") as? String

                    if (!token.isNullOrEmpty()) {

                        val userId = (body["userId"] as? Double)?.toInt()
                        val name = body["name"] as? String
                        val firstName = body["firstName"] as? String
                        val surname = body["surname"] as? String

                        SessionManager.token = token
                        SessionManager.userId = userId
                        SessionManager.name = name
                        SessionManager.firstName = firstName
                        SessionManager.surname = surname

                        // Guardar token en SharedPreferences
                        val prefs = context.getSharedPreferences("AgroCityPrefs", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putString("authToken", token)
                            .putBoolean("isLoggedIn", true)


                        showToast(context, "¡Bienvenido a AgroCity!")

                        // Redirigir a MainActivity
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as? ComponentActivity)?.finish()
                    } else {
                        showToast(context, "Token inválido")
                    }
                } else {
                    when (response.code()) {
                        401 -> showToast(context, "Correo o contraseña incorrectos")
                        404 -> showToast(context, "Usuario no encontrado")
                        else -> showToast(context, "Error al iniciar sesión")
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToast(context, "Error de conexión: ${e.message}")
            }
        }
    }
}
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}