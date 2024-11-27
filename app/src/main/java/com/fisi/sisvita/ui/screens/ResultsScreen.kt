package com.fisi.sisvita.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.fisi.sisvita.ui.theme.SisvitaTheme
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun ResultsScreen(
    paddingValues: PaddingValues,
    anxietyLevel: Float,
    emotionPercentages: Map<String, Float>,
    navController: NavController,
) {
    val colors = listOf(
        Color(0xFF4CAF50), // Verde
        Color(0xFFF44336), // Rojo
        Color(0xFF2196F3), // Azul
        Color(0xFFFFC107), // Amarillo
        Color(0xFFFF5722), // Naranja
        Color(0xFF9C27B0), // Púrpura
        Color(0xFF00BCD4)  // Cyan
    )

    //val anxietyLevel = remember { Random.nextFloat() * 100 } // Nivel de ansiedad aleatorio

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resultados",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

//        when (val state = uploadState) {
//            is UploadState.Loading -> {
//                CircularProgressIndicator()
//            }
//            is UploadState.Success -> {
//                // Convertir las emociones a porcentajes
//                val emotionPercentages = state.emotions.let {
//                    mapOf(
//                        "Disgustado" to (it.disgusted ?: 0.0) * 100,
//                        "Enojado" to (it.angry ?: 0.0) * 100,
//                        "Feliz" to (it.happy ?: 0.0) * 100,
//                        "Miedo" to (it.scared ?: 0.0) * 100,
//                        "Neutral" to (it.neutral ?: 0.0) * 100,
//                        "Sorpresa" to (it.surprised ?: 0.0) * 100,
//                        "Triste" to (it.sad ?: 0.0) * 100
//                    ).mapValues { entry ->
//                        entry.value.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toFloat()
//                    }
//                }
//
//                ResultCard(emotionPercentages, colors)
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Calculamos el nivel de ansiedad como promedio de emociones negativas
//                val anxietyLevel = calculateAnxietyLevel(emotionPercentages)
//                AnxietyLevelCard(anxietyLevel)
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "Recomendaciones",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                RecommendationCard()
//            }
//            is UploadState.Error -> {
//                Text(
//                    text = "Error al cargar resultados: ${state.message}",
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//            else -> {
//                Text(
//                    text = "Esperando resultados...",
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
        ResultCard(emotionPercentages, colors)
        Spacer(modifier = Modifier.height(16.dp))
        AnxietyLevelCard(anxietyLevel)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Recomendaciones",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("Orientations")
        }) {
            Text(text = "Escuchar las recomendaciones de IA")
        }
        //RecommendationCard()
    }
}

@Composable
fun ResultCard(emotions: Map<String, Float>, colors: List<Color>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Column {
                    emotions.entries.forEachIndexed { index, (emotion, percentage) ->
                        // Mostrar las etiquetas y las barras de progreso
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text(
//                                text = emotion,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                            Text(
//                                text = "${percentage.toInt()}%",
//                                color = colors[index % colors.size],
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Mostrar la barra de progreso
                        DisplayEmotionBar(emotion, percentage, colors[index % colors.size])
                        Spacer(modifier = Modifier.height(8.dp))
                    }

//                    emotions.forEachIndexed { index, emotion ->
//                        if (index < emotionPercentages.size) {
//                            DisplayEmotionBar(emotion, emotionPercentages[index], colors[index])
//                            Spacer(modifier = Modifier.height(8.dp))
//                        }
//                    }
                }
            }
        }
    }
}

//@Composable
//fun ResultCard(emotions: Map<String, Float>, colors: List<Color>) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(16.dp))
//            .background(MaterialTheme.colorScheme.surface)
//            .padding(16.dp)
//    ) {
//        emotions.entries.forEachIndexed { index, (emotion, percentage) ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(text = emotion, color = MaterialTheme.colorScheme.onSurface)
//                Text(text = "${percentage}%", color = colors[index % colors.size])
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//    }
//}

@Composable
fun AnxietyLevelCard(
    level: Float,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            ,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Nivel de ansiedad",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                ) {
                    val strokeWidth = 20.dp.toPx()

                    // Arco de fondo
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        size = Size(size.width, size.height * 2)
                    )

                    // Arco de progreso
                    drawArc(
                        color = Color(0xFF53C4C2),
                        startAngle = 180f,
                        sweepAngle = 180f * (level / 100f),
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        size = Size(size.width, size.height * 2)
                    )

                    // Punto indicador
                    val angle = Math.toRadians((180f + (180f * level / 100f)).toDouble())
                    val radius = (size.width - strokeWidth) / 2 + 30f
                    val center = Offset(size.width / 2, size.height)
                    val x = center.x + (radius * cos(angle)).toFloat()
                    val y = center.y + (radius * sin(angle)).toFloat()

                    drawCircle(
                        color = Color.White,
                        radius = 15f,
                        center = Offset(x, y),
                        style = Stroke(width = 30f)
                    )

                    drawCircle(
                        color = Color.Black,
                        radius = 15f,
                        center = Offset(x, y)
                    )
                }

                Text(
                    text = "${"%.1f".format(level)}%",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.offset(y = 30.dp)
                )
            }
        }
    }
}

@Composable
fun RecommendationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp), // Aumentar el tamaño para que las recomendaciones se vean mejor
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "- Practica la respiración profunda.\n- Realiza ejercicio físico.\n- Escucha música relajante.",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun DisplayEmotionBar(emotion: String, percentage: Float, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emotion,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
            Text(
                text = "${"%.1f".format(percentage)}%",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LinearProgressIndicator(
            progress = percentage / 100,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp),
            color = color,
            trackColor = color.copy(alpha = 0.3f)
        )
    }
}

fun generateRandomEmotionPercentages(): List<Float> {
    val randomValues = List(7) { Random.nextFloat() }
    val total = randomValues.sum()
    return randomValues.map { it / total * 100 }
}



@Preview(showBackground = true)
@Composable
fun PreviewResultsScreen() {
    SisvitaTheme(darkTheme = false) {
        //ResultsScreen(paddingValues = PaddingValues(0.dp))
    }
}
