package com.fisi.sisvita.ui.screens.camera


import com.fisi.sisvita.data.repository.CameraManager
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class CameraScreenViewModel(
    private val cameraManager: CameraManager,
    private val navController: NavController
) : ViewModel() {
//    private var isCameraFlashEnabled = false
//
//    fun toggleCameraFlash() {
//        isCameraFlashEnabled = !isCameraFlashEnabled
//        cameraManager.toggleFlash(isCameraFlashEnabled)
//    }
//
//    fun stopRecording() {
//        cameraManager.stopRecording()
//        navController.navigate("Loading")
//    }
//
//    fun switchCamera() {
//        cameraManager.switchCamera()
//    }
}