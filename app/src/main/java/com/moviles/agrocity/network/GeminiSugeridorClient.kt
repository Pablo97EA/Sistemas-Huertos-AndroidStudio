package com.moviles.agrocity.network

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.moviles.agrocity.network.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.util.Locale
import android.os.Handler
import android.os.Looper

class GeminiSugeridorClient(private val apiKey: String) {

    private val client = OkHttpClient()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun sugerirCultivos(
        ubicacion: String,
        clima: String,
        mes: String,
        dia: LocalDate?,
        callback: (String) -> Unit
    ) {
        val prompt = """
            Estoy en $ubicacion, el clima actual es $clima, y hoy es $dia de $mes. 
            ¿Qué cultivos puedo sembrar en esta zona en esta época?, pensando que es en lugares urbanos con poco espacio,Dame recomendaciones breves.
        """.trimIndent()

        val url =
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val body = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "$prompt" }
                  ]
                }
              ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainHandler.post {
                    callback("Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val bodyStr = it.body?.string() ?: ""
                    try {
                        val json = JSONObject(bodyStr)
                        val result = json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")

                        mainHandler.post {
                            callback(result)
                        }
                    } catch (e: Exception) {
                        mainHandler.post {
                            callback("Error al procesar la respuesta: ${e.message}")
                        }
                    }
                }
            }
        })
    }
}

// ==========================
// Funciones de lógica extra
// ==========================

fun extraerCultivos(texto: String): List<String> {
    val cultivosPosibles = listOf(
        "maíz", "tomate", "cilantro", "lechuga", "zanahoria",
        "frijol", "pimiento", "calabaza", "cebolla", "platanos",
        "aguacate", "pepino", "limon", "sandia"
    )
    val textoLower = texto.lowercase()
    return cultivosPosibles.filter { textoLower.contains(it) }
}

fun getLocationName(context: Context, location: Location): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            listOfNotNull(
                address.thoroughfare,
                address.subLocality,
                address.locality,
                address.adminArea,
                address.countryName
            ).joinToString(", ")
        } else {
            "Ubicación desconocida"
        }
    } catch (e: Exception) {
        "Error al obtener la ubicación"
    }
}

fun fetchWeatherByLocation(
    lat: String,
    lon: String,
    weatherViewModel: WeatherViewModel,
    scope: CoroutineScope
) {
    scope.launch {
        weatherViewModel.fetchWeather(lat, lon)
    }
}

fun procesarRespuestaSugerencia(
    respuesta: String,
    onResult: (sugerencia: String, cultivos: List<String>, emojis: Map<String, String>) -> Unit
) {
    val cultivos = extraerCultivos(respuesta)

    val mapaEmojis = mapOf(
        "maíz" to "🌽",
        "tomate" to "🍅",
        "cilantro" to "🌿",
        "lechuga" to "🥬",
        "zanahoria" to "🥕",
        "frijol" to "🫘",
        "pimiento" to "🫑",
        "calabaza" to "🎃",
        "cebolla" to "🧅",
        "platanos" to "🍌",
        "aguacate" to "🥑",
        "pepino"  to "🥒",
        "limon" to "🍋‍",
        "sandia" to "🍉"
    )

    val cultivosEmojis = cultivos.associateWith { mapaEmojis[it] ?: "🌱" }

    onResult(respuesta, cultivos, cultivosEmojis)
}
