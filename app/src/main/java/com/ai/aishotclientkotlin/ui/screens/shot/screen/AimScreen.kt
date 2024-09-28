package com.ai.aishotclientkotlin.ui.screens.shot.screen
import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(modifier: Modifier) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIdList = cameraManager.cameraIdList

    cameraIdList.forEach { cameraId ->
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
        if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
            Log.e("AR", "Back camera is available")
        } else if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            Log.e("AR", "Front camera is available")
        }
        val cameraCapabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
        if (cameraCapabilities?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) == true) {
            Log.e("AR", "Camera supports backward compatibility")
        } else {
            Log.e("AR", "Camera does not support running multiple streams simultaneously")
        }
    }
    // 创建不同的 LifecycleOwner
    val backCameraLifecycleOwner = remember { CustomLifecycleOwner() }
    val frontCameraLifecycleOwner = remember { CustomLifecycleOwner() }

    LaunchedEffect(Unit) {
        if (permissionState.status != PermissionStatus.Granted) {
            permissionState.launchPermissionRequest()
        } else {
            cameraProvider = cameraProviderFuture.get()
        }
        backCameraLifecycleOwner.start() // 启动后置摄像头的生命周期
        frontCameraLifecycleOwner.start() // 启动前置摄像头的生命周期
    }

   // CameraSelector.Builder().requireLensFacing()requireLensFacing

    if (permissionState.status == PermissionStatus.Granted && cameraProvider != null) {
        Column(modifier = modifier.fillMaxSize()) {
            CameraPreview(Modifier.weight(1f), CameraSelector.DEFAULT_BACK_CAMERA, cameraProvider!!,backCameraLifecycleOwner)
            CameraPreview(Modifier.weight(1f), CameraSelector.DEFAULT_FRONT_CAMERA, cameraProvider!!,frontCameraLifecycleOwner)
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier, cameraSelector: CameraSelector, cameraProvider: ProcessCameraProvider,lifecycleOwner: LifecycleOwner) {
    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            startCamera(previewView, context, cameraSelector, cameraProvider,lifecycleOwner)
            Log.e("AR","CameraPreview")
            previewView
        },
        modifier = modifier
    )
}

private fun startCamera(  previewView: PreviewView,
                          context: Context,
                          cameraSelector: CameraSelector,
                          cameraProvider: ProcessCameraProvider, lifecycleOwner: LifecycleOwner) {



    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    try {
        // 仅绑定所需的摄像头，不再解绑所有摄像头
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview
        )
        Log.e("AR", "Camera bound successfully for $cameraSelector")
    } catch (exc: Exception) {
        Log.e("AR", "Failed to bind camera: ${exc.message}")
    }
}


class CustomLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        // 设置初始状态为 CREATED，生命周期开始
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }



    // 启动生命周期，将状态变为 STARTED
    fun start() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    // 停止生命周期，将状态变为 DESTROYED
    fun stop() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}


