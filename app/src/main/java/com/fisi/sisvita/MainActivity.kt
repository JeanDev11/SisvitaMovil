package com.fisi.sisvita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.fisi.sisvita.ui.components.AppScaffoldComponent
import com.fisi.sisvita.ui.screens.HelpMeScreen
import com.fisi.sisvita.ui.screens.initializeCameraScreen
import com.fisi.sisvita.ui.theme.SisvitaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SisvitaTheme {
                //HelpMeScreen()
                AppScaffoldComponent(
                    userName = "Linna Jimenez",
                    onLogout = {}
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
        HelpMeScreen()
    }
}