package com.moviles.agrocity.network

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Base64
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class GeminiClient(private val apiKey: String) {

    private val client = OkHttpClient()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun analyzePlantImage(bitmap: Bitmap, callback: (String) -> Unit) {
        // Convert Bitmap to Base64
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

        val url =
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val body = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "Detecta la planta, sus síntomas, enfermedades o plagas. Sugiere tratamiento si es necesario." },
                    {
                      "inlineData": {
                        "mimeType": "image/png",
                        "data": "$base64Image"
                      }
                    }
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
                        if (!json.has("candidates")) {
                            mainHandler.post {
                                callback("Error: no se encontró 'candidates' en la respuesta.")
                            }
                            return
                        }
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

// Extensión para RequestBody
fun String.toRequestBody(contentType: MediaType) =
    RequestBody.create(contentType, this)
