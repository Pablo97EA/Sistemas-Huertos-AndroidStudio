package com.moviles.agrocity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.moviles.agrocity.models.RegisterDTO
import com.moviles.agrocity.models.User
import com.moviles.agrocity.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etAge: EditText
    private lateinit var etTelephone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvBackToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        etName = findViewById(R.id.etName)
        etFirstName = findViewById(R.id.etFirstName)
        etSurname = findViewById(R.id.etSurname)
        etAge = findViewById(R.id.etAge)
        etTelephone = findViewById(R.id.etTelephone)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        // Configurar el botón de registro
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val firstName = etFirstName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val age = etAge.text.toString().trim()
            val telephone = etTelephone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInputs(name, firstName, surname, age, telephone, email, password)) {
                registerUser(
                    name = name,
                    firstName = firstName,
                    surname = surname,
                    age = age.toInt(),
                    telephone = telephone,
                    email = email,
                    password = password
                )
            }
        }

        // Configurar el enlace para volver al login
        tvBackToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(
        name: String,
        firstName: String,
        surname: String,
        age: String,
        telephone: String,
        email: String,
        password: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                showError("Ingrese su nombre completo")
                false
            }
            firstName.isEmpty() -> {
                showError("Ingrese su primer apellido")
                false
            }
            surname.isEmpty() -> {
                showError("Ingrese su segundo apellido")
                false
            }
            age.isEmpty() || !age.matches(Regex("\\d+")) -> {
                showError("Ingrese una edad válida")
                false
            }
            telephone.isEmpty() -> {
                showError("Ingrese su teléfono")
                false
            }
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Ingrese un correo electrónico válido")
                false
            }
            password.isEmpty() || password.length < 6 -> {
                showError("La contraseña debe tener al menos 6 caracteres")
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
        password: String
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
                    password = password, // Usar la contraseña hasheada
                )

                Log.d("REGISTER_DEBUG", "Enviando usuario: ${Gson().toJson(user)}")

                // Usar el método 'registerUser' para enviar la solicitud de registro
                val response: Response<Map<String, Any>> = RetrofitInstance.api.registerUser(user)

                runOnUiThread {
                    if (response.isSuccessful) {
                        // Si la respuesta es exitosa, mostrar mensaje y redirigir
                        Toast.makeText(
                            this@RegisterActivity,
                            "¡Registro exitoso! Bienvenido ${user.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // Si la respuesta no es exitosa, mostrar el error
                        val errorBody = response.errorBody()?.string()
                        Log.e("REGISTER_ERROR", "Error en registro: ${response.code()} - $errorBody")
                        showError("Error en el registro: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("REGISTER_ERROR", "Excepción: ${e.message}", e)
                runOnUiThread {
                    showError("Error de conexión: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
