package com.fisi.sisvita.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.R
import com.fisi.sisvita.ui.theme.SisvitaTheme

@Composable
fun HelpMeScreen(navController: NavController){
    val pagerState = rememberPagerState(pageCount = { 4 })

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center

    ) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            when (page) {
                0 -> StepCard(
                    imageResource = R.drawable.ic_step1,
                    description = "Asegúrate de estar en un lugar bien iluminado y tranquilo."
                )

                1 -> StepCard(
                    imageResource = R.drawable.ic_step2,
                    description = "Ajusta tu posición para que tu rostro esté centrado en la pantalla."
                )

                2 -> StepCard(
                    imageResource = R.drawable.ic_step3,
                    description = "Tómate un momento para respirar profundamente y calmarte."
                )

                3 -> StepCardWithCamera(
                    imageResource = R.drawable.ic_step4,
                    description = "Recuerda que es un espacio seguro para compartir cómo te sientes.",
                    navController = navController
                )
            }
        }

        StepIndicator(currentStep = pagerState.currentPage + 1, totalSteps = 4)
    }
}

@Composable
fun StepCard(imageResource: Int, description: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(imageResource),
                contentDescription = null,
                modifier = Modifier.size(400.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = description,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun StepCardWithCamera(imageResource: Int, description: String, navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(imageResource),
                    contentDescription = null,
                    modifier = Modifier.size(400.dp)
                )
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = description,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(48.dp))
            }

            IconButton(
                onClick = {
                    navController.navigate("Rec")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Abrir cámara",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        for (i in 1..totalSteps) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (i == currentStep) MaterialTheme.colorScheme.primaryContainer else Color(
                            0xFFD9D9D9
                        )
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpMePreview() {
    SisvitaTheme(darkTheme = false) {
        val navController = rememberNavController()
        HelpMeScreen(navController)
    }
}