package com.fisi.sisvita.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisi.sisvita.R

@Composable
fun IAScreenContent() {
    // Diseño de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen
        Image(
            painter = painterResource(id = R.drawable.person_greeting), // Asegúrate de que esta imagen esté disponible en tu proyecto
            contentDescription = "Persona saludando",
            modifier = Modifier.size(300.dp) // Ajusta el tamaño de la imagen
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espacio entre la imagen y el texto

        // Texto debajo de la imagen
        Text(
            text = "Intenta oir las recomendaciones que te brinda nuestra IA",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el texto y la siguiente pregunta

        // Texto para la pregunta
        Text(
            text = "¿Está siendo de ayuda?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre la pregunta y los botones

        // Botones
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { /* Acción para "Sí" */ }) {
                Text(text = "Sí", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = { /* Acción para "No" */ }) {
                Text(text = "No", fontSize = 16.sp)
            }
        }
    }
}
