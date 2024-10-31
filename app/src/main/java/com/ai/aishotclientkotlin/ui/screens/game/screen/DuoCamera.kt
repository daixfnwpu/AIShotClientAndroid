package com.ai.aishotclientkotlin.ui.screens.game.screen

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.CompositionSettings
import androidx.camera.core.ConcurrentCamera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DualCameraScreen(handsDetected: HandsDetected,
                     eyesDetected : EyesDetected) {
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
            isConcurrentSupported = isConcurrentSupported
            ,onFrameAvailable = { imageProxy ->
                // 在这里处理图像帧
                analyzeFrame(imageProxy, hands =handsDetected , eyesDetected = eyesDetected)
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
suspend fun awaitCameraProvider(context: Context): ProcessCameraProvider {
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
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    isConcurrentSupported: Boolean,
    onFrameAvailable: (imageProxy: ImageProxy) -> Unit
) {
    val context = LocalContext.current
    var localScope = LocalLifecycleOwner.current
    val desiredFrameRate = 15 // 期望的帧率，例如每秒处理5帧
    var lastFrameTime = System.currentTimeMillis()

    // Set up primary and secondary camera selectors if supported on device.
    var primaryCameraSelector: CameraSelector? = null
    //var secondaryCameraSelector: CameraSelector? = null

    for (cameraInfos in cameraProvider.availableConcurrentCameraInfos) {
        primaryCameraSelector = cameraInfos.first {
            it.lensFacing == CameraSelector.LENS_FACING_FRONT
        }.cameraSelector
       /* secondaryCameraSelector = cameraInfos.first {
            it.lensFacing == CameraSelector.LENS_FACING_BACK
        }.cameraSelector*/

       /* if (primaryCameraSelector == null || secondaryCameraSelector == null) {
            primaryCameraSelector = null
            secondaryCameraSelector = null
        } else {
            break
        }*/
    }

    /*if (primaryCameraSelector == null || secondaryCameraSelector == null) {
        Toast.makeText(context, "Device does not support concurrent camera", Toast.LENGTH_SHORT).show()
        return
    }*/

   /* val preview = remember {
        Preview.Builder()
            .setTargetResolution(Size(640, 480))
            .build()
    }*/
    val preview2 = remember {
        Preview.Builder()
            .setTargetResolution(Size(320, 240))
            .build()
    }

    LaunchedEffect(Unit) {


        /*  ImageAnalysis.Builder().setTargetResolution(Size(320, 240))
              .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).setBackgroundExecutor(ContextCompat.getMainExecutor(context))
              .build().also {
                  it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->

                      val currentTime = System.currentTimeMillis()
                      Log.e("camera"," ImageAnalysis process")
                      // 计算两帧之间的时间差
                      if (currentTime - lastFrameTime >= 1000 / desiredFrameRate) {
                          onFrameAvailable(imageProxy)  // 处理帧
                          lastFrameTime = currentTime  // 更新最后处理的时间
                      }
                      imageProxy.close() // Don't forget to close the image!
                  }
              }*/
     //   val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
       // cameraProvider.addListener({
           // val cameraProvider = cameraProviderFuture.get()

            // 配置 CameraX 前置摄像头图像分析
            val analysisConfig = ImageAnalysis.Builder()
                .setTargetResolution(Size(320, 240)) // 设置图像分辨率
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // 丢弃旧帧
                .setBackgroundExecutor(ContextCompat.getMainExecutor(context))
                .build().also{
                    it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        // 在此处处理前置摄像头的图像
                        onFrameAvailable(imageProxy)
                        imageProxy.close() // 确保处理完成后关闭图像
                    }
                }

            // 选择前置摄像头
         //   val cameraSelector = CameraSelector.LENS_FACING_FRONT
        if (primaryCameraSelector != null) {
            cameraProvider.bindToLifecycle(localScope, primaryCameraSelector,preview2, analysisConfig)
        }

      //  }, ContextCompat.getMainExecutor(context))
       /* val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()



         //   val preview = Preview(preview2)
          //  preview2.setSurfaceProvider(frontPreviewView.surfaceProvider)

            // 绑定前置摄像头
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            cameraProvider.bindToLifecycle(this, cameraSelector, preview)

        }, ContextCompat.getMainExecutor(this))

        val primaryConfig = primaryCameraSelector?.let {
            ConcurrentCamera.SingleCameraConfig(
                it,
                UseCaseGroup.Builder().addUseCase(imageAnalysis).addUseCase(preview2).build(),
                CompositionSettings.Builder()
                    .build(),
                lifecycleOwner
            )
        };*/
       /* val secondaryConfig = secondaryCameraSelector?.let {
            ConcurrentCamera.SingleCameraConfig(
                it,
                UseCaseGroup.Builder().addUseCase(preview).build(),
                CompositionSettings.Builder()
                    .setAlpha(1.0f)
                    .setOffset(2 / 3f - 0.1f, -2 / 3f + 0.1f)
                    .setScale(1 / 3f, 1 / 3f)
                    .build(),
                lifecycleOwner
            )
        };*/

      //  var concurrentCamera: ConcurrentCamera =
          //  cameraProvider.bindToLifecycle(listOf(primaryConfig/*, secondaryConfig*/));

    }


    // 使用两个独立的 PreviewView 来显示两个摄像头
    Box(Modifier.fillMaxSize()) {
        ARTempView(modifier = Modifier.fillMaxSize(),)
        AndroidView(
            modifier = Modifier.height(64.dp).width(48.dp) .align(Alignment.BottomEnd),
            factory = { context ->
                val previewView2 = PreviewView(context)
                preview2.surfaceProvider = previewView2.surfaceProvider  // 设置第一个摄像头的 SurfaceProvider
                previewView2
            }
        )
       /* AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                val previewView = PreviewView(context)
                preview.surfaceProvider = previewView.surfaceProvider  // 设置第二个摄像头的 SurfaceProvider
                previewView
            }
        )*/

    }
}

private fun startFrontCamera() {

}

