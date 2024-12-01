package com.fisi.sisvita.ui.screens.testForm

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fisi.sisvita.data.model.RespuestaSubmission
import com.fisi.sisvita.data.model.TestSubmission
import com.fisi.sisvita.data.model.UserSession
import com.fisi.sisvita.ui.screens.test.TestViewModel

data class Question(val preguntaid: Int, val text: String, val answers: List<Answer>)
data class Answer(val respuestaid: Int, val text: String)

@Composable
fun TestFormScreen(paddingValues: PaddingValues, navController: NavController, viewModel: TestViewModel) {
    val preguntas by viewModel.preguntas.collectAsState()
    val respuestas by viewModel.respuestas.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val questions = preguntas.map { pregunta ->
        Question(
            preguntaid = pregunta.preguntaid,
            text = pregunta.textopregunta,
            answers = respuestas.filter { it.testid == pregunta.testid }
                .map { Answer(respuestaid = it.respuestaid, text = it.textorespuesta) }
        )
    }.filter { it.answers.isNotEmpty() }

    if (questions.isEmpty()) {
        Text(
            text = "Test no disponible",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
        )
        return
    }

    if (showErrorDialog) {
        ErrorDialog(errorMessage = errorMessage) {
            showErrorDialog = false
        }
    }

    if (showResultDialog) {
        ResultDialog(
            diagnostico = UserSession.diagnostico.value ?: "",
            puntaje = UserSession.puntaje.value ?: 0,
            navController = navController,
        )
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(36.dp))
    }

    TestFormContent(paddingValues = paddingValues,questions = questions) { respuestasSeleccionadas ->
        val testId = UserSession.testId.value?.toIntOrNull() ?: 0
        val personaid = UserSession.personId.value?.toIntOrNull() ?: 0
        Log.d("Test", personaid.toString())
        val testSubmission = TestSubmission(
            personaid = personaid,
            testid = testId,
            respuestas = respuestasSeleccionadas
        )
        Log.d("TestSubmission", testSubmission.toString())
        isLoading = true
        viewModel.submitTest(testSubmission) { success ->
            isLoading = false
            if (success) {
                showResultDialog = true
            } else {
                errorMessage = "Failed to submit the test. Please try again."
                showErrorDialog = true
            }
        }
    }
}

@Composable
fun TestFormContent(paddingValues: PaddingValues, questions: List<Question>, onSubmit: (List<RespuestaSubmission>) -> Unit) {
    val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(questions) { index, question ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pregunta ${index + 1}: ${question.text}",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                question.answers.forEach { answer ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedAnswers[question.preguntaid] == answer.respuestaid,
                            onClick = { selectedAnswers[question.preguntaid] = answer.respuestaid }
                        )
                        Text(
                            text = answer.text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val respuestas = selectedAnswers.map { (preguntaid, respuestaid) ->
                        RespuestaSubmission(preguntaid = preguntaid, respuestaid = respuestaid)
                    }
                    onSubmit(respuestas)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = selectedAnswers.size == questions.size
            ) {
                Text("Enviar respuestas")
            }
        }
    }
}

@Composable
fun ResultDialog(diagnostico: String, puntaje: Int, navController: NavController) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = diagnostico,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$puntaje/80",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        navController.navigate("Test") {
                            popUpTo("DoTest") { inclusive = true }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("OK")
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Error") },
        text = { Text(text = errorMessage) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}