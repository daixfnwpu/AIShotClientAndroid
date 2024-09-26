package com.ai.aishotclientkotlin.ui.screens.shot.screen

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult
import com.google.mlkit.vision.common.InputImage

@Composable
fun CameraPreview(
    onFrameAvailable: (imageProxy: ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current



    AndroidView(
        factory = { cameraView ->
            val previewView = PreviewView(context)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            onFrameAvailable(imageProxy)
                            imageProxy.close() // Don't forget to close the image!
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalGetImage::class)
fun analyzeFrame(imageProxy: ImageProxy, hands: Hands) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // 将图像传递给 MediaPipe
        hands.send(inputImage)
    }
}

@Composable
fun DrawHandLandmarks(landmarks: List<LandmarkProto.Landmark>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        for (landmark in landmarks) {
            val x = landmark.x * size.width
            val y = landmark.y * size.height
            drawCircle(color = Color.Red, radius = 5f, center = Offset(x, y))
        }
    }
}


@Composable
fun HandGestureRecognitionUI(
    landmarks: List<LandmarkProto.Landmark>
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(onFrameAvailable = { imageProxy ->
            // 在这里处理图像帧

        })
        DrawHandLandmarks(landmarks = landmarks)
    }
}


