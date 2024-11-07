package com.fisi.sisvita

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.ui.components.AppScaffoldComponent
import com.fisi.sisvita.ui.screens.initializeCameraScreen
import com.fisi.sisvita.ui.theme.SisvitaTheme

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
                AppScaffoldComponent(
                    userName = "Linna Jimenez",
                    onLogout = {},
                    navController = navController
                )
                initializeCameraScreen(this) // Pasamos 'this' como contexto
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