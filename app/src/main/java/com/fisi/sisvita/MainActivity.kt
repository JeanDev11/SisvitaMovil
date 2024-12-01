// MainActivity.kt
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.data.model.UserSession
import com.fisi.sisvita.data.repository.LoginRepository
import com.fisi.sisvita.ui.components.AppScaffoldComponent
import com.fisi.sisvita.ui.screens.login.LoginScreen
import com.fisi.sisvita.ui.screens.login.LoginViewModel
import com.fisi.sisvita.ui.theme.SisvitaTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
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
                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = koinViewModel()
                val isLoggedIn by loginViewModel.loginState.collectAsState(initial = false)

//isLoggedIn
                if (isLoggedIn) {
                    AppScaffoldComponent(
                        userName = UserSession.userName.value ?: "Unknown User",
                        onLogout = { loginViewModel.logout() },
                        navController = navController
                    )
                } else {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onSignUp = { navController.navigate("register") }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SisvitaTheme {
        //HelpMeScreen()
    }
}