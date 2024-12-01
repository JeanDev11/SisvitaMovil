package com.fisi.sisvita.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fisi.sisvita.R
import com.fisi.sisvita.data.model.Test
import com.fisi.sisvita.data.model.UserSession
import com.fisi.sisvita.data.repository.TestRepository
import com.fisi.sisvita.ui.screens.test.TestViewModel
import com.fisi.sisvita.ui.theme.SisvitaTheme

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: TestViewModel
) {
    val tests by viewModel.tests.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Welcome()
        TestAdd(tests, navController, viewModel) // Pasa la lista de tests al componente
        HelpMeAdd(navController = navController)
    }
}

@Composable
fun Welcome(){
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
    ){
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            Text(
                text = "Hola, ${ UserSession.userName.value}!",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = "\"Recuerda, cada paso que das te acerca un poco más a tus metas. ¡Sigue adelante con determinación!\"",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TestAdd(tests: List<Test>, navController: NavController, viewModel: TestViewModel) {
    if (tests.isEmpty()) {
        // Mostrar un indicador de carga si no hay datos
        CircularProgressIndicator(
            modifier = Modifier
                .size(32.dp)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Realiza tu Test",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(
                onClick = { navController.navigate("Test") },
            ) {
                Text(text = "Ver todo")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TestCarousel(tests, navController, viewModel)
    }
}

@Composable
fun TestCarousel(tests: List<Test>, navController: NavController, viewModel: TestViewModel) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tests) { test ->
            TestCard(test, navController, viewModel)
        }
    }
}

@Composable
fun TestCard(test: Test, navController: NavController, viewModel: TestViewModel) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = test.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = test.descripcion,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    try {
                        // Selecciona el test en el ViewModel
                        viewModel.selectTest(test.testid)
                        Log.e("NavigationError", test.testid.toString())
                        // Navega a TestFormScreen
                        navController.navigate("DoTest")
                    } catch (e: Exception) {
                        Log.e("NavigationError", "Error navigating to Test: ${e.message}")
                    }
//                    UserSession.testId.value = test.testid.toString()
//                    try {
//                    navController.navigate("DoTest")
//                    Log.d("NavigationError", test.testid.toString())
//                } catch (e: Exception) {
//                    Log.e("NavigationError", "Error navigating to Test: ${e.message}")
//                }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Comenzar")
            }
        }
    }
}

@Composable
fun HelpMeAdd(
    navController: NavController
){
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "¿Sientes ansiedad?",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ){
            Image(
                painter = painterResource(id = R.drawable.ic_psychologisthelp),
                contentDescription = "User profile",
            )
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Obtén ayuda aquí",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Si te sientes abrumado o ansioso, usa esta opción para hablar con nosotros y recibir orientación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { navController.navigate("Necesito ayuda") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Ir")
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    SisvitaTheme(darkTheme = false) {
        val navController = rememberNavController()
        HomeScreen(paddingValues = PaddingValues(0.dp), navController = navController, viewModel = TestViewModel(TestRepository()))
    }
}