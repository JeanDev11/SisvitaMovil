package com.fisi.sisvita.data.repository

import android.util.Log
import com.fisi.sisvita.data.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterRepository {
    private val client = OkHttpClient()

    suspend fun register(documenttype: String, documentcharacter: String, birthdate: String,email: String,name: String,lastname: String,
                         secondlastname: String,gender: String, phone: String, department: String,province: String,district: String, username: String, password: String, role : String) : Boolean {
        return withContext(Dispatchers.IO) {
            val json = JSONObject().apply {
                put("document_type", documenttype)
                put("document_character", documentcharacter)
                put("birth_date", birthdate)
                put("email", email)
                put("name", name)
                put("last_name", lastname)
                put("second_last_name", secondlastname)
                put("gender", gender)
                put("phone", phone)
                put("department", department)
                put("province", province)
                put("district", district)
                put("username", username)
                put("password", password)
                put("role", role)
            }

            val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://dsm-backend.onrender.com/register")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                if(response.isSuccessful){
                    val responseBody = response.body?.string()
                    val json1 = JSONObject(responseBody ?: "")
                    val status = json1.optInt("status")
                    if(status == 200){
                        return@withContext true
                    }
                }
                return@withContext false
            } catch (e: IOException) {
                return@withContext false
            }
        }
    }

    suspend fun getDocumentTypes(): List<String> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://dsm-backend.onrender.com/document_type")
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
                        val documentTypes = mutableListOf<String>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val documentType = item.optString("document_type")
                                documentTypes.add(documentType)
                            }
                        }
                        return@withContext documentTypes
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }


    suspend fun getGenders(): List<String> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://dsm-backend.onrender.com/gender")
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
                        val genders = mutableListOf<String>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val gender = item.optString("gender_character")
                                genders.add(gender)
                            }
                            return@withContext genders
                        }
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }

    suspend fun getDepartments(): List<String> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://dsm-backend.onrender.com/departamentos")
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
                        val departments = mutableListOf<String>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val departament = item.optString("departament")
                                departments.add(departament)
                            }
                            return@withContext departments
                        }
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }

    suspend fun getProvinces(department: String): List<String> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://dsm-backend.onrender.com/departamentos/$department")
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
                        val provinces = mutableListOf<String>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val province = item.optString("province")
                                provinces.add(province)
                            }
                            return@withContext provinces
                        }
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }

    suspend fun getDistricts(department: String, province: String): List<String> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://dsm-backend.onrender.com/departamentos/$department/provincias/$province")
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
                        val districts = mutableListOf<String>()
                        if (data != null) {
                            for (i in 0 until data.length()) {
                                val item = data.optJSONObject(i)
                                val district = item.optString("district")
                                districts.add(district)
                            }
                            return@withContext districts
                        }
                    }
                }
                return@withContext emptyList()
            } catch (e: IOException) {
                return@withContext emptyList()
            }
        }
    }


}