package com.fisi.sisvita.data.repository

import android.util.Log
import com.fisi.sisvita.data.model.Pregunta
import com.fisi.sisvita.data.model.Respuesta
import com.fisi.sisvita.data.model.Test
import com.fisi.sisvita.data.model.TestSubmission
import com.fisi.sisvita.data.model.UserSession
import com.fisi.sisvita.data.model.UserSession.testId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class TestRepository {
    private val client = OkHttpClient()
    // Cachés en memoria
    private var cachedTests: List<Test>? = null
    private val cachedPreguntas = mutableMapOf<Int, List<Pregunta>>()
    private val cachedRespuestas = mutableMapOf<Int, List<Respuesta>>()

    suspend fun getTests(): List<Test> {
        if (cachedTests != null) {
            return cachedTests!! // Si los tests ya están en caché, se devuelven directamente
        }

        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://sysvita-dswg13-1.onrender.com/tests")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val status = json.optInt("status")
                    if (status == 200) {
                        val data = json.optJSONArray("data")
                        val tests = mutableListOf<Test>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val test = Test(
                                    testid = item.optInt("testid"),
                                    nombre = item.optString("nombre"),
                                    descripcion = item.optString("descripcion")
                                )
                                tests.add(test)
                            }
                            cachedTests = tests
                            return@withContext tests
                        }
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }

    suspend fun getPreguntas(testId: Int): List<Pregunta> {
        if (cachedPreguntas.containsKey(testId)) {
            return cachedPreguntas[testId]!! // Si ya están en caché, devolver directamente
        }
        return withContext(Dispatchers.IO) {
//            val testId = UserSession.testId.value ?: return@withContext emptyList()
            val request = Request.Builder()
                .url("https://sysvita-dswg13-1.onrender.com/preguntas/$testId")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val status = json.optInt("status")
                    if (status == 200) {
                        val data = json.optJSONArray("data")
                        val preguntas = mutableListOf<Pregunta>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val pregunta = Pregunta(
                                    preguntaid = item.optInt("preguntaid"),
                                    testid = item.optInt("testid"),
                                    textopregunta = item.optString("textopregunta")
                                )
                                preguntas.add(pregunta)
                            }
                            cachedPreguntas[testId] = preguntas
                            return@withContext preguntas
                        }
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }

    suspend fun getRespuestas(testId: Int): List<Respuesta> {
        if (cachedRespuestas.containsKey(testId)) {
            return cachedRespuestas[testId]!! // Si ya están en caché, devolver directamente
        }
        return withContext(Dispatchers.IO) {
//            val testId = UserSession.testId.value ?: return@withContext emptyList()
            val request = Request.Builder()
                .url("https://sysvita-dswg13-1.onrender.com/respuestas/$testId")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val status = json.optInt("status")
                    if (status == 200) {
                        val data = json.optJSONArray("data")
                        val respuestas = mutableListOf<Respuesta>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val respuesta = Respuesta(
                                    respuestaid = item.optInt("respuestaid"),
                                    testid = item.optInt("testid"),
                                    textorespuesta = item.optString("textorespuesta")
                                )
                                respuestas.add(respuesta)
                            }
                            cachedRespuestas[testId] = respuestas
                            return@withContext respuestas
                        }
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }

    suspend fun submitTest(testSubmission: TestSubmission): Boolean {
        return withContext(Dispatchers.IO) {
            val json = JSONObject().apply {
                put("personaid", testSubmission.personaid)
                put("testid", testSubmission.testid)
                put("respuestas", testSubmission.respuestas.map {
                    JSONObject().apply {
                        put("preguntaid", it.preguntaid)
                        put("respuestaid", it.respuestaid)
                    }
                })
            }

            Log.d("SubmitTest", "JSON enviado: $json")

            val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("https://sysvita-dswg13-1.onrender.com/realizartest")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val status = jsonResponse.optInt("status")
                    if (status == 201) {
                        UserSession.diagnostico.value = jsonResponse.optString("diagnostico")
                        UserSession.puntaje.value = jsonResponse.optInt("puntaje")
                        Log.d("SubmitTest", "Diagnostico: ${UserSession.diagnostico}, Puntaje: ${UserSession.puntaje}")
                    } else {
                        Log.e("SubmitTest", "Error en la respuesta del servidor: ${response.code}, ${response.message}")
                    }
                    response.isSuccessful
                } else {
                    Log.e("SubmitTest", "Error en la respuesta del servidor: ${response.code}, ${response.message}")
                    false
                }
            } catch (e: IOException) {
                Log.e("SubmitTest", "Error de conexión: ${e.message}", e)
                false
            }
        }
    }
}