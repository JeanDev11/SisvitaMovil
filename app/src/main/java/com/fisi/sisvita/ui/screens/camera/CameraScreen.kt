package com.fisi.sisvita.ui.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
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
import com.fisi.sisvita.R


@Composable
fun CameraScreen(viewModel: CameraScreenViewModel = viewModel()) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Check camera permission
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        hasCameraPermission = true
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        // Camera preview with buttons
        CameraContent(viewModel)
    } else {
        // Permission request message
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Se requiere el permiso de la cámara para continuar", color = Color.Red)
        }
    }
}

@Composable
fun CameraContent(viewModel: CameraScreenViewModel) {
    val isFlashOn by viewModel.isFlashOn.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isUsingFrontCamera by viewModel.isUsingFrontCamera.collectAsState()
    val context = LocalContext.current

    val recorder = Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HD)).build()
    val videoCapture = VideoCapture.withOutput(recorder)

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
                onClick = { if (isRecording) {
                    Log.e("Camera", "stopRecording va entrar")
                    viewModel.stopRecording()
                    viewModel.toggleRecording()

                } else {
                    Log.e("Camera", "startRecording va entrar")
                    viewModel.startRecording(context, videoCapture)
                    viewModel.toggleRecording()
                } },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isRecording) R.drawable.ic_stop else R.drawable.ic_record
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
            val cameraSelector = if (isUsingFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
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
