package com.fisi.sisvita.data.repository

import com.fisi.sisvita.data.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginRepository {
    private val client = OkHttpClient()

    suspend fun login(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val json = JSONObject().apply {
                put("correo", email)
                put("contrasena", password)
                put("tipousuarioid", 1)
            }

            val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://sysvita-dswg13-1.onrender.com/login")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json1 = JSONObject(responseBody ?: "")
                    val status = json1.optInt("status")
                    if (status == 200) {
                        val data = json1.optJSONObject("data")
                        val personId = data?.optString("person_id")
                        val username = data?.optString("username")
                        UserSession.personId.value = personId
                        UserSession.userName.value = username
                        return@withContext true
                    }
                }
                return@withContext false
            } catch (e: IOException) {
                return@withContext false
            }
        }
    }
}