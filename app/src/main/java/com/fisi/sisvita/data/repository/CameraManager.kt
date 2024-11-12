package com.fisi.sisvita.data.repository

import androidx.camera.core.Camera
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LifecycleOwner

class CameraManager(
    private val cameraProvider: CameraProvider,
    private var cameraSelector: CameraSelector,
    private val lifecycleOwner: LifecycleOwner
) {
//    private var camera: Camera? = null
//    private var isFrontCamera = false
//    private var isFlashEnabled = false
//
//    init {
//        bindCamera()
//    }
//
//    private fun bindCamera() {
//        // Usa el lifecycleOwner para el método bindToLifecycle
//        camera = cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelector
//        )
//    }
//
//    fun toggleFlash(enable: Boolean) {
//        isFlashEnabled = enable
//        camera?.cameraControl?.enableTorch(isFlashEnabled)
//    }
//
//    fun stopRecording() {
//        camera?.cameraControl?.stopRecording()
//    }
//
//    fun switchCamera() {
//        isFrontCamera = !isFrontCamera
//        cameraSelector = if (isFrontCamera) {
//            CameraSelector.DEFAULT_FRONT_CAMERA
//        } else {
//            CameraSelector.DEFAULT_BACK_CAMERA
//        }
//        bindCamera()
//    }
}