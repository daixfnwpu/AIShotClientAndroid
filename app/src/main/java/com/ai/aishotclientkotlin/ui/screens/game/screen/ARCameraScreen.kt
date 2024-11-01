package com.ai.aishotclientkotlin.ui.screens.game.screen

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.ai.aishotclientkotlin.engine.mediapipe.EyesDetected
import com.ai.aishotclientkotlin.engine.mediapipe.HandsDetected
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

//bug solove:
/*
    1,出现的报错，与开启前置摄像头没有关系，特别是丢帧问题Saw buffer lost event for frame
    和Unable to advance filter. INVALID_ARGUMENT: [Ralph Srif:: Advance] Unexpected image features timestamp in the past.
    INVALID_ARGUMENT: Passed timestamp is too old.

 */


@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DualCameraScreenNoAR(
    handsDetected: HandsDetected,
    eyesDetected: EyesDetected
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasCameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var isConcurrentSupported by remember { mutableStateOf(false) }

    // 检查设备是否支持多摄像头并发流
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val concurrentCameraIds = cameraManager.concurrentCameraIds

            if (concurrentCameraIds != null) {
                Log.e("Camera", "Concurrent Camera ID Sets: $concurrentCameraIds")

                // 检查是否有至少一个大小为 2 或以上的 Set，表示支持并发摄像头
                isConcurrentSupported = concurrentCameraIds.any { it.size >= 2 }
            } else {
                Log.e("Camera", "Concurrent camera not supported on this device.")
                isConcurrentSupported = false
            }

            Log.e("Camera", "Is Concurrent Supported: $isConcurrentSupported")
        } else {
            Log.e("Camera", "Concurrent camera support requires Android 12 or higher.")
            isConcurrentSupported = false
        }
    }

    // 异步获取 cameraProvider 实例
    LaunchedEffect(Unit) {
        cameraProvider = awaitCameraProvider(context)
    }

    if (hasCameraPermission.status.isGranted && cameraProvider != null) {
        DualCameraPreview(
            cameraProvider = cameraProvider!!,
            lifecycleOwner = lifecycleOwner,
            isConcurrentSupported = isConcurrentSupported, onFrameAvailable = { imageProxy ->
                // 在这里处理图像帧
                analyzeFrame(
                    context,
                    imageProxy,
                    hands = handsDetected,
                    eyesDetected = eyesDetected
                )
            }
        )
    } else {
        // 确保 ActivityResultLauncher 已经初始化后才请求权限
        if (!hasCameraPermission.status.isGranted && hasCameraPermission.status.shouldShowRationale.not()) {
            hasCameraPermission.launchPermissionRequest()
        } else {
            Text("Camera permission required")
        }
    }
}

@Composable
fun DualCameraPreview(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    isConcurrentSupported: Boolean,
    onFrameAvailable: (proxy: ImageProxy) -> Unit
) {


    val context = LocalContext.current
    // var localScope = LocalLifecycleOwner.current
    val desiredFrameRate = 2 // 期望的帧率，例如每秒处理5帧
    var lastFrameTime = System.currentTimeMillis()

    // Set up primary and secondary camera selectors if supported on device.

    // val primaryCameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    var primaryCameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()
    val previewAnalysis = remember {
        Preview.Builder()
            .setTargetResolution(Size(320, 240))
            .build()
    }
    val analysis = remember {
        ImageAnalysis.Builder()
            .setTargetResolution(Size(320, 240)) // 设置图像分辨率
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // 丢弃旧帧
            .setBackgroundExecutor(ContextCompat.getMainExecutor(context))
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    val currentTime = System.currentTimeMillis()
                    //   Log.e("camera"," ImageAnalysis process")
                    // 计算两帧之间的时间差
                    if (currentTime - lastFrameTime >= (1000 / desiredFrameRate)) {
                        onFrameAvailable(imageProxy)  // 处理帧
                        lastFrameTime = currentTime  // 更新最后处理的时间
                    }
                    imageProxy.close() // Don't forget to close the image!
                }
            }
    }
    DisposableEffect(Unit) {


        onDispose {
            // 界面退出时调用清理函数
            analysis.clearAnalyzer()
            println("MyScreen Disposed")
        }
    }
    LaunchedEffect(Unit) {
        cameraProvider.unbindAll()
        var cameraAnaly = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            primaryCameraSelector,
            previewAnalysis,
            analysis
        )
    }


    // 使用两个独立的 PreviewView 来显示两个摄像头
    Box(Modifier.fillMaxSize()) {
        //  ARTempView(modifier = Modifier.fillMaxSize(),)
        AndroidView(
            modifier = Modifier
                .height(64.dp)
                .width(48.dp)
                .align(Alignment.BottomEnd),
            factory = { context ->
                val previewViewAnalysis = PreviewView(context)
                previewAnalysis.surfaceProvider =
                    previewViewAnalysis.surfaceProvider  // 设置第一个摄像头的 SurfaceProvider
                previewViewAnalysis
            }
        )

    }
}


