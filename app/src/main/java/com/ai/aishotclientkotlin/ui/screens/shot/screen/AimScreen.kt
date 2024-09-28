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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(modifier: Modifier) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    Log.e("AR","CameraScreen")
    LaunchedEffect(Unit) {
        if (permissionState.status != PermissionStatus.Granted)
            permissionState.launchPermissionRequest()
    }
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
    }
    if (permissionState.status == PermissionStatus.Granted) {
        Column(modifier = modifier.fillMaxSize()) {
            Log.e("AR","CameraPreview")
           // CameraPreview(Modifier.weight(1.0f), CameraSelector.DEFAULT_FRONT_CAMERA)
            CameraPreview(Modifier.weight(1.0f), CameraSelector.DEFAULT_BACK_CAMERA)

            // 这里可以放置前摄像头的预览，或者其他内容
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier, cameraSelector: CameraSelector) {
    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            startCamera(previewView, context, cameraSelector)
            Log.e("AR","CameraPreview")
            previewView
        },
        modifier = modifier
    )
}

private fun startCamera(previewView: PreviewView, context: Context, cameraSelector: CameraSelector) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    Log.e("AR","startCamera")
    Debug.waitForDebugger()
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (exc: Exception) {
            // 处理错误
            Log.e("AR",exc.toString())
        }
    }, ContextCompat.getMainExecutor(context))
}