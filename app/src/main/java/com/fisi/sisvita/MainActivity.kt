package com.fisi.sisvita

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.data.model.UserSession
import com.fisi.sisvita.data.model.UserSession.userName
import com.fisi.sisvita.ui.components.AppScaffoldComponent
import com.fisi.sisvita.ui.screens.ErrorDialog
import com.fisi.sisvita.ui.screens.LoginScreen
import com.fisi.sisvita.ui.screens.initializeCameraScreen
import com.fisi.sisvita.ui.theme.SisvitaTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = if (isGranted) {
            true // El permiso fue concedido
        } else {
            false // El permiso fue denegado
        }
    }

    private var hasCameraPermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            SisvitaTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                val navController = rememberNavController()
                Log.d("MainActivity", "After login PersonId: ${UserSession.personId.value}, UserName: ${UserSession.userName.value}")
                if (isLoggedIn) {
                    AppScaffoldComponent(
                            userName = UserSession.userName.value ?: "Unknown User",
                            onLogout = { isLoggedIn = false },
                            navController = navController
                        )
                    Log.d("MainActivity", "PersonId: ${UserSession.personId.value}, UserName: ${UserSession.userName.value}")
                    initializeCameraScreen(this)
                } else {
                    var showErrorDialog by remember { mutableStateOf(false) }

                    if (showErrorDialog) {
                        ErrorDialog { showErrorDialog = false }
                    }

                    LoginScreen(
                        onLogin = { username, password ->
                            login(username, password) { success ->
                                Handler(Looper.getMainLooper()).post {
                                    if (success) {
                                        isLoggedIn = true // Esto forzará una recomposición
                                    } else {
                                        showErrorDialog = true
                                    }
                                }
                            }
                            isLoggedIn = true
                        },
                        onSignUp = {
                            // RegisterScreen
                        }
                    )
                }
            }
        }
    }
}

private fun login(username: String, password: String, callback: (Boolean) -> Unit) {
    val client = OkHttpClient()
    val json = JSONObject().apply {
        put("username", username)
        put("password", password)
        put("role", 2)
    }

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
        .url("https://dsm-backend.onrender.com/login")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(false)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!it.isSuccessful) {
                    callback(false)
                    return
                }

                val responseBody = it.body?.string()
                if (responseBody != null) {
                    val json1 = JSONObject(responseBody)
                    val message = json1.optString("message")
                    val status = json1.optInt("status")
                    val data = json1.optJSONObject("data")
                    val personId = data?.optString("id_person")
                    val userName = data?.optString("userName")
                    Log.d("Login", "Status: $status, Message: $message, PersonId: $personId, UserName: $userName")
                    if (status == 200 && message == "Login successful") {
                        Handler(Looper.getMainLooper()).post {
                            UserSession.personId.value = personId
                            UserSession.userName.value = userName
                            Log.d("Login", "PersonId: ${UserSession.personId.value}, UserName: ${UserSession.userName.value}")
                            callback(true)
                        }
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }
        }
    })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SisvitaTheme {
        //HelpMeScreen()
    }
}