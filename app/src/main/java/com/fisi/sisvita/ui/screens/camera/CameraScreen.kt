package com.fisi.sisvita.ui.screens.camera

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.fisi.sisvita.R
import com.fisi.sisvita.ui.theme.SisvitaTheme
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Variables globales para el uso en las funciones
private lateinit var cameraExecutor: ExecutorService
private var faceCascade: CascadeClassifier? = null
private var eyeCascade: CascadeClassifier? = null
private var isUsingFrontCamera = true
private lateinit var emotionModel: Interpreter
private var facialExpressionRecognition: FacialExpressionRecognition? = null

fun initializeCameraScreen(context: Context) {
    cameraExecutor = Executors.newSingleThreadExecutor()
    if (!OpenCVLoader.initDebug()) {
        Log.e("OpenCV", "Unable to load OpenCV!")
    } else {
        Log.d("OpenCV", "OpenCV loaded Successfully!")
        loadCascadeClassifier(context)
        loadEyeCascade(context)
    }

    try {
        val inputSize = 48
        facialExpressionRecognition = FacialExpressionRecognition(
            context.assets,
            context,
            "model.tflite",
            inputSize
        )
        Log.e("CameraScreen", "Modelo cargado correctamente")
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("CameraScreen", "Error al cargar el modelo")
    }
}

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
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder()
                            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                            .build()
                            .also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor) { imageProxy ->
                                    processFrame(imageProxy) { bitmap ->
                                        processedBitmap = bitmap
                                    }
                                }
                            }

                        val cameraSelector = if (isUsingFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                ctx as LifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraPreview", "Error al iniciar la cámara", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
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

//@Preview
//@Composable
//fun CameraScreenPreview() {
//    SisvitaTheme(darkTheme = false) {
//        CameraScreen()
//    }
//}

private fun processFrame(imageProxy: ImageProxy, onFrameCaptured: (Bitmap) -> Unit) {
    try {
        val bitmap = imageProxyToBitmap(imageProxy)
        if (bitmap != null) {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            if (isUsingFrontCamera) {
                Core.flip(mat, mat, 1)
            }

            val processedMat = facialExpressionRecognition?.recognizeImage(mat)

            if (processedMat != null) {
                val processedBitmap = Bitmap.createBitmap(processedMat.cols(), processedMat.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(processedMat, processedBitmap)
                onFrameCaptured(processedBitmap)
            } else {
                onFrameCaptured(bitmap)
            }
        }
    } finally {
        imageProxy.close()
    }
}

private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    val format = imageProxy.format
    if (format == ImageFormat.YUV_420_888) {
        val nv21 = yuv420ToNv21(imageProxy)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, imageProxy.width, imageProxy.height), 75, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } else {
        Log.e("imageProxyToBitmap", "Formato de imagen no compatible: $format")
        return null
    }
}

private fun yuv420ToNv21(image: ImageProxy): ByteArray {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}

private fun loadCascadeClassifier(context: Context) {
    try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.haarcascade_frontalface_default)
        val cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE)
        val cascadeFile = File(cascadeDir, "haarcascade_frontalface_default.xml")
        val outputStream = FileOutputStream(cascadeFile)

        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()

        faceCascade = CascadeClassifier(cascadeFile.absolutePath)
        if (faceCascade?.empty() == true) {
            faceCascade = null
            Log.e("OpenCV", "Failed to load cascade classifier")
        } else {
            Log.d("OpenCV", "Cascade classifier loaded")
        }

        cascadeDir.delete()
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("OpenCV", "Error al inicializar el clasificador de rostros")
    }
}

private fun loadEyeCascade(context: Context) {
    try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.haarcascade_eye)
        val cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE)
        val cascadeFile = File(cascadeDir, "haarcascade_eye.xml")
        val outputStream = FileOutputStream(cascadeFile)

        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()

        eyeCascade = CascadeClassifier(cascadeFile.absolutePath)
        if (eyeCascade?.empty() == true) {
            eyeCascade = null
            Log.e("OpenCV", "Failed to load eye cascade classifier")
        } else {
            Log.d("OpenCV", "Eye cascade classifier loaded")
        }

        cascadeDir.delete()
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("OpenCV", "Error al inicializar el clasificador de ojos")
    }
}

// Clase para el reconocimiento facial y de emociones
class FacialExpressionRecognition(
    assetManager: AssetManager,
    context: Context,
    modelPath: String,
    inputSize: Int
) {
    private var interpreter: Interpreter
    private val inputSize: Int = inputSize
    private var height = 0
    private var width = 0
    private var gpuDelegate: GpuDelegate? = null
    private var cascadeClassifier: CascadeClassifier? = null

    init {
        // Set GPU for the interpreter
        val options = Interpreter.Options()
        //gpuDelegate = GpuDelegate()AAAAA
        //options.addDelegate(gpuDelegate)AAAAAA
        // Set number of threads
        options.setNumThreads(4) // Adjust according to your phone

        // Load model weights to interpreter
        interpreter = Interpreter(loadModelFile(assetManager, modelPath), options)
        Log.d("FacialExpression", "Model is loaded")

        // Load Haar Cascade classifier
        try {
            val `is`: InputStream = context.resources.openRawResource(R.raw.haarcascade_frontalface_alt)
            val cascadeDir: File = context.getDir("cascade", Context.MODE_PRIVATE)
            val mCascadeFile = File(cascadeDir, "haarcascade_frontalface_alt")
            val os = FileOutputStream(mCascadeFile)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (`is`.read(buffer).also { bytesRead = it } != -1) {
                os.write(buffer, 0, bytesRead)
            }

            `is`.close()
            os.close()

            cascadeClassifier = CascadeClassifier(mCascadeFile.absolutePath)
            Log.d("FacialExpression", "Classifier is loaded")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun recognizeImage(matImage: Mat): Mat {
        // Antes de la predicción, la imagen no está alineada correctamente.
        // Rotamos 90 grados para obtener una predicción adecuada.
        Core.flip(matImage.t(), matImage, 1) // rotar matImage 90 grados

        // Comenzar con el proceso
        // Convertir matImage a imagen en escala de grises
        val grayscaleImage = Mat()
        Imgproc.cvtColor(matImage, grayscaleImage, Imgproc.COLOR_RGBA2GRAY)

        // Definir altura y anchura
        height = grayscaleImage.height()
        width = grayscaleImage.width()

        // Definir la altura mínima del rostro en la imagen original
        val absoluteFaceSize = (height * 0.1).toInt()

        // Crear MatOfRect para almacenar el rostro
        val faces = MatOfRect()
        // Verificar si el clasificador cascadeClassifier está cargado
        if (cascadeClassifier != null) {
            // Detectar rostros en el fotograma
            cascadeClassifier!!.detectMultiScale(
                grayscaleImage, faces, 1.1, 2, 2,
                Size(absoluteFaceSize.toDouble(), absoluteFaceSize.toDouble()), Size()
            )
        }

        // Almacenar el reporte de emociones para cada rostro
        val emotionReports = mutableListOf<String>()

        // Convertir a array
        val faceArray = faces.toArray()
        // Iterar sobre cada rostro detectado
        for ((i,face) in faceArray.withIndex()) {
            // Dibujar un rectángulo alrededor del rostro
            Imgproc.rectangle(matImage, faceArray[i].tl(), faceArray[i].br(), Scalar(0.0, 255.0, 0.0, 255.0), 4)

            //NUEVO
            // Mostrar el número de cara sobre el cuadro
            Imgproc.putText(
                matImage, "Cara ${i + 1}",
                Point(face.tl().x, face.tl().y - 40),
                Core.FONT_HERSHEY_SIMPLEX, 0.8,
                Scalar(255.0, 255.0, 0.0, 255.0), 2
            )

            // Recortar el rostro de la imagen original y de grayscaleImage
            val roi = Rect(
                faceArray[i].tl().x.toInt(),
                faceArray[i].tl().y.toInt(),
                (faceArray[i].br().x - faceArray[i].tl().x).toInt(),
                (faceArray[i].br().y - faceArray[i].tl().y).toInt()
            )
            val croppedRgba = Mat(matImage, roi)

            // Convertir croppedRgba a Bitmap
            val bitmap = Bitmap.createBitmap(croppedRgba.cols(), croppedRgba.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(croppedRgba, bitmap)

            // Redimensionar el bitmap a (48,48)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, false)

            // Convertir scaledBitmap a ByteBuffer
            val byteBuffer = convertBitmapToByteBuffer(scaledBitmap)

            ///GAAAAAAAAAAAAAAAAAAAAAAAAAAA
//            // Crear un objeto para almacenar la salida
//            val emotion = Array(1) { FloatArray(1) }
//            // Realizar la predicción usando el byteBuffer como entrada
//            interpreter.run(byteBuffer, emotion)
//
//            // Imprimir el valor de la emoción reconocida
//            Log.d("facial_expression", "Output: ${emotion[0][0]}")
//
//            // Obtener el valor de emoción en tipo Float
//            val emotionValue = emotion[0][0]
//            Log.d("facial_expression", "Output: $emotionValue")
//
//            // Llamar a la función que devuelve el texto de la emoción
//            val emotionText = getEmotionText(emotionValue)
//
//            // Colocar el texto junto a la cara detectada
//            Imgproc.putText(
//                matImage, "$emotionText ($emotionValue)",
//                Point(face.tl().x, face.tl().y - 10), // Posición del texto, ligeramente encima de la cara
//                Core.FONT_HERSHEY_SIMPLEX,
//                0.8, // Tamaño de la fuente (puedes ajustar esto)
//                Scalar(0.0, 0.0, 255.0, 255.0), // Color del texto (rojo)
//                2
//            )



            ///GAAAAAAAAAAAAAAAAAAAAAAAAAAA
            // Crear un objeto para almacenar la salida con probabilidades para cada emoción
            val emotionProbabilities = Array(1) { FloatArray(7) } // Asume 7 emociones, ajusta según tu modelo

            // Realizar la predicción usando el byteBuffer como entrada
            interpreter.run(byteBuffer, emotionProbabilities)

            // Obtener el índice de la emoción con la probabilidad más alta y su valor
            val maxIndex = emotionProbabilities[0].indices.maxByOrNull { emotionProbabilities[0][it] } ?: -1
            Log.e("a","maxindex: "+maxIndex)
            val emotionValue = emotionProbabilities[0][maxIndex]
            Log.e("a","emotionValue: "+maxIndex)
            val emotionText = getEmotionText(maxIndex)
            Log.e("a","emotionText: "+maxIndex)

            // Colocar el texto junto a la cara detectada con la probabilidad de confianza
            Imgproc.putText(
                matImage, "$emotionText (${(emotionValue * 100).toInt()}%)",
                Point(face.tl().x, face.tl().y - 10), // Posición del texto, ligeramente encima de la cara
                Core.FONT_HERSHEY_SIMPLEX,
                0.8, // Tamaño de la fuente (puedes ajustar esto)
                Scalar(255.0, 255.0, 0.0, 255.0), // Color del texto (rojo)
                2
            )

            //NUEVO
            // Dibujar el "reporte emocional" al lado de cada rostro detectado
            val startX = face.br().x + 10  // Posición de inicio del reporte en X (a la derecha de la cara)
            var startY = face.tl().y + 20  // Posición de inicio del reporte en Y

            // Mostrar el porcentaje de cada emoción junto al rostro
            for (i in emotionProbabilities[0].indices) {
                val emotionReportText = "${getEmotionText(i)}: ${String.format("%.2f", emotionProbabilities[0][i] * 100)}%"
                Imgproc.putText(
                    matImage, emotionReportText,
                    Point(startX, startY),
                    Core.FONT_HERSHEY_SIMPLEX,
                    0.8,  // Tamaño de fuente más pequeño
                    Scalar(0.0, 255.0, 0.0, 255.0), 2 // Color blanco para el texto del reporte
                )
                startY += 20  // Mover la siguiente línea un poco más abajo
            }
        }
        return matImage
    }

    private fun getEmotionText(index:Int): String {
        return when (index) {
            0 -> "Sorpresa"
            1 -> "Miedo"
            2 -> "Enojado"
            3 -> "Neutral"
            4 -> "Triste"
            5 -> "Disgustado"
            6 -> "Feliz"
            else -> "Desconocido"
        }
    }

//    private fun getEmotionText(emotionValue: Float): String {
//        return when {
//            emotionValue in 0.0..0.5 -> "Sorpresa"
//            emotionValue in 0.5..1.8 -> "Miedo"
//            emotionValue in 1.8..2.5 -> "Enojado"
//            emotionValue in 2.5..3.5 -> "Neutral"
//            emotionValue in 3.5..4.5 -> "Triste"
//            emotionValue in 4.5..5.5 -> "Disgustado"
//            else -> "Feliz"
//        }
//    }


    private fun convertBitmapToByteBuffer(scaledBitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 1 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)
        var pixel = 0

        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                // Escalar cada componente de color a un rango de 0 a 1
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }

        return byteBuffer
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}


