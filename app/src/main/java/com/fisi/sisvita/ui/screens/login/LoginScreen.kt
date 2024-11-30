package com.fisi.sisvita.ui.screens.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.R
import com.fisi.sisvita.data.repository.LoginRepository
import com.fisi.sisvita.ui.theme.SisvitaTheme

@Composable
fun LoginScreen(viewModel: LoginViewModel, onSignUp: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (viewModel.showErrorDialog) {
        ErrorDialog { viewModel.showErrorDialog = false }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable._024),
            contentDescription = "Login Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.login(email, password)
                Log.d("LoginScreen", "Login success: $email and $password")},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Don't have an account? Sign up",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSignUp() }
                .padding(8.dp)
        )
    }
}

@Composable
fun ErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text("Usuario o contraseña incorrectas") },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    SisvitaTheme(darkTheme = false) {
        val navController = rememberNavController()
        LoginScreen(viewModel = LoginViewModel(LoginRepository()), onSignUp = {})
    }
}
