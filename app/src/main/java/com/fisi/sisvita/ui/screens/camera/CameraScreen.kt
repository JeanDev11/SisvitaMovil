package com.fisi.sisvita.ui.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.navigation.NavController
import com.fisi.sisvita.R
import com.fisi.sisvita.ui.screens.loading.LoadingScreen
import com.fisi.sisvita.util.toJson
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.math.RoundingMode

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CameraScreenViewModel = viewModel()

//    val uploadState by viewModel.uploadState.collectAsState()
//    Log.d("CameraScreen", "Estado recibido: $uploadState")
    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        hasCameraPermission = true
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraContent(navController, viewModel)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Se requiere el permiso de la cámara para continuar", color = Color.Red)
        }
    }

    // Manejar el estado de carga
    /**when (val state = uploadState) {
        is UploadState.Idle -> {
            // Mostrar el contenido de la cámara
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                hasCameraPermission = true
            } else {
                LaunchedEffect(Unit) {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            if (hasCameraPermission) {
                CameraContent(viewModel)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Se requiere el permiso de la cámara para continuar", color = Color.Red)
                }
            }
        }
        is UploadState.Loading -> {
            Log.d("CameraScreen", "si entro en loading")
            LoadingScreen()
        }
        is UploadState.Success -> {
            Log.d("CameraScreen", "si entro en success")
            // Convertir emociones a porcentajes
            val emotionPercentages = state.emotions.let {
                mapOf(
                    "Disgustado" to (it.disgusted ?: 0.0) * 100,
                    "Enojado" to (it.angry ?: 0.0) * 100,
                    "Feliz" to (it.happy ?: 0.0) * 100,
                    "Miedo" to (it.scared ?: 0.0) * 100,
                    "Neutral" to (it.neutral ?: 0.0) * 100,
                    "Sorpresa" to (it.surprised ?: 0.0) * 100,
                    "Triste" to (it.sad ?: 0.0) * 100
                ).mapValues { entry ->
                    entry.value.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toFloat()
                }
            }

            // Calcular nivel de ansiedad
            val anxietyLevel = calculateAnxietyLevel(emotionPercentages)

            // Navegar a ResultsScreen
            navController.navigate("results?anxietyLevel=$anxietyLevel&emotions=${emotionPercentages.toJson()}"){
                popUpTo("Rec") { inclusive = true }
            }

            // Resetear el estado después de navegar
            viewModel.resetState()

//            val emotions = (uploadState as UploadState.Success).emotions
//            LaunchedEffect(Unit) {
//                Log.d("CameraScreen", "Navegando a Result")
//                navController.navigate("Result") {
//                    popUpTo("Rec") { inclusive = true }
//                }
//            }
        }
        is UploadState.Error -> {
            Log.d("CameraScreen", "si entro en error")
            val message = (uploadState as UploadState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }*/
}

@Composable
fun CameraContent(navController: NavController, viewModel: CameraScreenViewModel) {
    val isFlashOn by viewModel.isFlashOn.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isUsingFrontCamera by viewModel.isUsingFrontCamera.collectAsState()
    val context = LocalContext.current

    val recorder = Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HD)).build()
    val videoCapture = VideoCapture.withOutput(recorder)

    // Estado para controlar la navegación después de la grabación
    var shouldNavigate by remember { mutableStateOf(false) }

    // Navegación programada después de 1.5 segundos
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            delay(1500)
            navController.navigate("Loading")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        CameraPreview(isUsingFrontCamera, videoCapture, viewModel)

        // Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 64.dp), // Adjust this to position the buttons
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Flash Button
            IconButton(
                onClick = {
                    Log.e("Camera", "Flash start")
                    viewModel.toggleFlash() },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isFlashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off
                    ),
                    contentDescription = "Flash",
                    tint = Color.White
                )
            }

            // Record Button
            IconButton(
                onClick = {
                    if (isRecording) {
                        viewModel.stopRecording()
                        viewModel.toggleRecording()
                        shouldNavigate = true
                        //viewModel.uploadVideo(context)
                    } else {
                        val videoUri = viewModel.startRecording(context, videoCapture)
                        if (videoUri != null) {
                            Log.d("Main", "Grabación iniciada. Archivo: $videoUri")
                            viewModel.toggleRecording()
                        } else {
                            Log.e("Main", "Error al iniciar la grabación.")
                        }
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isRecording) R.drawable.ic_stop_rec else R.drawable.ic_play_rec
                    ),
                    contentDescription = "Record",
                    tint = Color.White
                )
            }

            // Switch Camera Button
            IconButton(
                onClick = { viewModel.toggleCamera() },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flip_camera),
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun CameraPreview(isUsingFrontCamera: Boolean, videoCapture: VideoCapture<Recorder>, viewModel: CameraScreenViewModel) {
    val context = LocalContext.current // Obtener el contexto actual de la aplicación
    val lifecycleOwner = LocalContext.current as LifecycleOwner // Obtenr el ciclo de vida actual
    val previewView = remember { PreviewView(context) } // Usar una referencia para el PreviewView
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context) // Usar un estado para manejar la cámara

    LaunchedEffect(cameraProviderFuture) {
        // Esperar a que la cameraProvider esté listo
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()
            val cameraSelector = if (isUsingFrontCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            val preview = Preview.Builder().build()

            try {
                // Desvincular cualquier cámara previamente configurada
                provider.unbindAll()

                // Vincula la cámara actual
                val camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture
                )

                // Asociar el preview a la vista
                preview.setSurfaceProvider(previewView.surfaceProvider)

                // Almacenar la camara en el viewModel para su uso posterior
                viewModel.setCamera(camera)

            } catch (e: Exception) {
                Log.e("CameraPreview", "Error al configurar la cámara", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView }
    )
}