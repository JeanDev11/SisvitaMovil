package com.fisi.sisvita.ui.screens.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fisi.sisvita.R

@Composable
fun CameraScreen(

) {
    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (processedBitmap == null) {
//            AndroidView(
//                modifier = Modifier
//                    .fillMaxSize(),
//                factory = { ctx ->
//                    val previewView = PreviewView(ctx).apply {
//                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
//                        scaleType = PreviewView.ScaleType.FILL_CENTER
//                    }
//
//                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
//                    cameraProviderFuture.addListener({
//                        val cameraProvider = cameraProviderFuture.get()
//
//                        val preview = Preview.Builder()
//                            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                            .build()
//                            .also {
//                                it.setSurfaceProvider(previewView.surfaceProvider)
//                            }
//
//                        val imageAnalysis = ImageAnalysis.Builder()
//                            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                            .build()
//                            .also {
//                                it.setAnalyzer(cameraExecutor) { imageProxy ->
//                                    processFrame(imageProxy) { bitmap ->
//                                        processedBitmap = bitmap
//                                    }
//                                }
//                            }
//
//                        val cameraSelector = if (isUsingFrontCamera) {
//                            CameraSelector.DEFAULT_FRONT_CAMERA
//                        } else {
//                            CameraSelector.DEFAULT_BACK_CAMERA
//                        }
//
//                        try {
//                            cameraProvider.unbindAll()
//                            cameraProvider.bindToLifecycle(
//                                ctx as LifecycleOwner,
//                                cameraSelector,
//                                preview,
//                                imageAnalysis
//                            )
//                        } catch (exc: Exception) {
//                            Log.e("CameraPreview", "Error al iniciar la cámara", exc)
//                        }
//                    }, ContextCompat.getMainExecutor(ctx))
//
//                    previewView
//                }
//            )
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Gray)
//            ) {
//                // Placeholder para representar el espacio de la cámara.
//            }
        } else {
            Image(
                bitmap = processedBitmap!!.asImageBitmap(),
                contentDescription = "Fotograma procesado",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {

                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flash_off),
                    contentDescription = "flash",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {

                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_stop),
                    contentDescription = "flash",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {

                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flip_camera),
                    contentDescription = "flash",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
