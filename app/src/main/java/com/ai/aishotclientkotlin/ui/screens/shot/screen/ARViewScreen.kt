package com.ai.aishotclientkotlin.ui.screens.shot.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ai.aishotclientkotlin.engine.ar.HandsDetected
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream

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
fun analyzeFrame(imageProxy: ImageProxy, hands: HandsDetected) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {

        //TODO，这里有可能有BUG；
        var inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        //    inputImage = InputImage.fromBitmap(mediaImage.toBitmap(), imageProxy.imageInfo.rotationDegrees)
        // 将图像传递给 MediaPipe
        val bitmap = mediaImageToBitmap(mediaImage, imageProxy.imageInfo.rotationDegrees)

        hands.hands.send(bitmap)
    }
}

@Composable
fun DrawHandLandmarks(landmarks: List<LandmarkProto.NormalizedLandmark>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        for (landmark in landmarks) {
            val x = landmark.x * size.width
            val y = landmark.y * size.height
            drawCircle(color = Color.Red, radius = 5f, center = Offset(x, y))
        }
    }
}

//TODO ，这里应该是所有的hand 处理的入口。
@Composable
fun HandGestureRecognitionUI(modifier: Modifier
  //  landmarks: List<LandmarkProto.Landmark>
) {
    val context = LocalContext.current
    val handsDetected = HandsDetected(context)
    handsDetected.init()
    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(onFrameAvailable = { imageProxy ->
            // 在这里处理图像帧
            analyzeFrame(imageProxy, hands =handsDetected )
        })

    }
    val landmarks by handsDetected.handsmarksState
    DrawHandLandmarks(landmarks)
}
fun mediaImageToBitmap(mediaImage: Image, rotationDegrees: Int): Bitmap? {
    val planes = mediaImage.planes
    val yBuffer = planes[0].buffer // Y plane
    val uBuffer = planes[1].buffer // U plane
    val vBuffer = planes[2].buffer // V plane

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copy Y, U, and V buffers into nv21 byte array
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, mediaImage.width, mediaImage.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, mediaImage.width, mediaImage.height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}



