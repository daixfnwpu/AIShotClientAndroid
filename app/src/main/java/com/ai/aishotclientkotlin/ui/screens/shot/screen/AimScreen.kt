package com.ai.aishotclientkotlin.ui.screens.shot.screen
import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
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

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    if (permissionState.status == PermissionStatus.Granted) {
        Box(modifier = modifier) {
            CameraPreview(Modifier.matchParentSize(), CameraSelector.DEFAULT_BACK_CAMERA)
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
            previewView
        },
        modifier = modifier
    )
}

private fun startCamera(previewView: PreviewView, context: Context, cameraSelector: CameraSelector) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

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
        }
    }, ContextCompat.getMainExecutor(context))
}