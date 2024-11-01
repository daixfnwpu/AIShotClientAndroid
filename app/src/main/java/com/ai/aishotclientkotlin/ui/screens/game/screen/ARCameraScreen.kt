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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
    // 异步获取 cameraProvider 实例
    LaunchedEffect(Unit) {
        cameraProvider = awaitCameraProvider_(context)
    }

    if (hasCameraPermission.status.isGranted && cameraProvider != null) {
        DualCameraPreview(
            pcameraProvider = cameraProvider!!,
            lifecycleOwner = lifecycleOwner,
            isConcurrentSupported = true, onFrameAvailable = { imageProxy ->
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
// 将 ListenableFuture 转换为挂起函数
suspend fun awaitCameraProvider_(context: Context): ProcessCameraProvider {
    return suspendCancellableCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                continuation.resume(cameraProviderFuture.get())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
@Composable
fun DualCameraPreview(
    pcameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    isConcurrentSupported: Boolean,
    onFrameAvailable: (proxy: ImageProxy) -> Unit
) {


    val context = LocalContext.current
    val desiredFrameRate = 5 // 期望的帧率，例如每秒处理5帧
    var lastFrameTime = System.currentTimeMillis()

    // Set up primary and secondary camera selectors if supported on device.

    // val primaryCameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    var primaryCameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    /*
        val previewView = PreviewView(context)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        Log.e("AR","AndroidView created")
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
                Log.e("AR","setSurfaceProvider")
            }
            Log.e("AR","addListener")
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        val currentTime = System.currentTimeMillis()
                        Log.e("camera"," ImageAnalysis process")
                        // 计算两帧之间的时间差
                        if (currentTime - lastFrameTime >= (1000 / desiredFrameRate)) {
                            onFrameAvailable(imageProxy)  // 处理帧
                            lastFrameTime = currentTime  // 更新最后处理的时间
                        }
                        imageProxy.close() // Don't forget to close the image!
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalyzer
            )
        }, ContextCompat.getMainExecutor(context))
    */



    val previewAnalysis = remember {
        Preview.Builder()
            .setTargetResolution(Size(320, 240))
            .build()
    }

    val imageAnalysis = remember {

        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setBackgroundExecutor(ContextCompat.getMainExecutor(context))
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->

                    val currentTime = System.currentTimeMillis()
                    Log.e("camera"," ImageAnalysis process")
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
            imageAnalysis.clearAnalyzer()
            println("MyScreen Disposed")
        }
    }
    LaunchedEffect(Unit) {
        println("This will only print once.")
        Log.e("Camera","only run once time")
        pcameraProvider.unbindAll()
        var cameraAnaly = pcameraProvider.bindToLifecycle(
            lifecycleOwner,
            primaryCameraSelector,
            imageAnalysis,
            previewAnalysis
        )
    }


    // 使用两个独立的 PreviewView 来显示两个摄像头
    Box(Modifier.fillMaxSize()) {
          ARTempView(modifier = Modifier.fillMaxSize(),)
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
        /*AndroidView(
            factory = { cameraView ->
                val previewView = PreviewView(context)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                Log.e("AR","AndroidView created")
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().setTargetResolution(Size(320, 240)).build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                        Log.e("AR","setSurfaceProvider")
                    }
                    Log.e("AR","addListener")
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build().also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                                val currentTime = System.currentTimeMillis()
                                Log.e("camera"," ImageAnalysis process")
                                // 计算两帧之间的时间差
                                if (currentTime - lastFrameTime >= (1000 / desiredFrameRate)) {
                                    onFrameAvailable(imageProxy)  // 处理帧
                                    lastFrameTime = currentTime  // 更新最后处理的时间
                                }
                                imageProxy.close() // Don't forget to close the image!
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageAnalyzer
                    )
                }, ContextCompat.getMainExecutor(context))

                previewView
            },
            modifier = Modifier
                .height(64.dp)
                .width(48.dp)
                .align(Alignment.BottomEnd),
            update = {
                Log.e("AR","update view called")
            }
        )*/

    }
}


