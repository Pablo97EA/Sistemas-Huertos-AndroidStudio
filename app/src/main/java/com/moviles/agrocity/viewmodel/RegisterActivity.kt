package com.moviles.agrocity.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.google.gson.Gson
import com.moviles.agrocity.MainActivity
import com.moviles.agrocity.models.RegisterDTO
import com.moviles.agrocity.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RegisterScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {
    var name by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "AGROCITY",
            color = Color(0xFF4CAF50),
            fontSize = 36.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Mensaje de registro
        Text(
            text = "Crear cuenta nueva",
            color = Color.Black,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo Nombre Completo
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Primer Apellido
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Primer Apellido") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Segundo Apellido
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Segundo Apellido") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Edad
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Teléfono
        OutlinedTextField(
            value = telephone,
            onValueChange = { telephone = it },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo Correo Electrónico
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

        // Campo Contraseña
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

        // Botón de Registro
        Button(
            onClick = {
                if (validateInputs(name, firstName, surname, age, telephone, email, password, context)) {
                    registerUser(
                        name = name,
                        firstName = firstName,
                        surname = surname,
                        age = age.toInt(),
                        telephone = telephone,
                        email = email,
                        password = password,
                        context = context
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Registrarse", color = Color.White)
        }

        // Enlace para volver a Login
        TextButton(
            onClick = {
                context.startActivity(Intent(context, LoginActivity::class.java))
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFF4CAF50))
        }
    }
}

private fun validateInputs(
    name: String,
    firstName: String,
    surname: String,
    age: String,
    telephone: String,
    email: String,
    password: String,
    context: Context
): Boolean {
    return when {
        name.isEmpty() -> {
            showToast(context, "Ingrese su nombre completo")
            false
        }
        firstName.isEmpty() -> {
            showToast(context, "Ingrese su primer apellido")
            false
        }
        surname.isEmpty() -> {
            showToast(context, "Ingrese su segundo apellido")
            false
        }
        age.isEmpty() || !age.matches(Regex("\\d+")) -> {
            showToast(context, "Ingrese una edad válida")
            false
        }
        telephone.isEmpty() -> {
            showToast(context, "Ingrese su teléfono")
            false
        }
        email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            showToast(context, "Ingrese un correo electrónico válido")
            false
        }
        password.isEmpty() || password.length < 6 -> {
            showToast(context, "La contraseña debe tener al menos 6 caracteres")
            false
        }
        else -> true
    }
}

private fun registerUser(
    name: String,
    firstName: String,
    surname: String,
    age: Int,
    telephone: String,
    email: String,
    password: String,
    context: Context
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val user = RegisterDTO(
                name = name,
                firstName = firstName,
                surname = surname,
                age = age,
                telephone = telephone,
                email = email,
                password = password
            )

            Log.d("REGISTER_DEBUG", "Enviando usuario: ${Gson().toJson(user)}")

            val response: Response<Map<String, Any>> = RetrofitInstance.api.registerUser(user)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    showToast(context, "¡Registro exitoso! Bienvenido ${user.name}")
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("REGISTER_ERROR", "Error en registro: ${response.code()} - $errorBody")
                    showToast(context, "Error en el registro: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("REGISTER_ERROR", "Excepción: ${e.message}", e)
            withContext(Dispatchers.Main) {
                showToast(context, "Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}