package com.ai.aishotclientkotlin.ui.screens.shot.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import android.view.View
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
import com.ai.aishotclientkotlin.engine.ar.EyesDetected
import com.ai.aishotclientkotlin.engine.ar.HandsDetected
import com.google.mediapipe.formats.proto.LandmarkProto
import java.io.ByteArrayOutputStream

@Composable
fun CameraPreview(modifier: Modifier,
    onFrameAvailable: (imageProxy: ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { cameraView ->
            val previewView = PreviewView(context)
            val emptyView = View(context)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                  //  it.setSurfaceProvider(previewView.surfaceProvider)
                    // 如果需要展示相机预览，可以在此设置 SurfaceProvider
                    it.setSurfaceProvider(null) // 暂时不设置 SurfaceProvider

                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            onFrameAvailable(imageProxy)
                            imageProxy.close() // Don't forget to close the image!
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            }, ContextCompat.getMainExecutor(context))
            //TODO 我想要一个空的View；
            emptyView
        },
        modifier = modifier.fillMaxSize(),
        update = {
            Log.e("AR","update view called")
        }
    )
}

@OptIn(ExperimentalGetImage::class)
fun analyzeFrame(imageProxy: ImageProxy, hands: HandsDetected,eyesDetected: EyesDetected) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {

        //TODO，这里有可能有BUG；
     //   var inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        //    inputImage = InputImage.fromBitmap(mediaImage.toBitmap(), imageProxy.imageInfo.rotationDegrees)
        // 将图像传递给 MediaPipe
        val bitmap = mediaImageToBitmap(mediaImage, imageProxy.imageInfo.rotationDegrees)
      //  val timestamp = System.currentTimeMillis() * 1000L
        val timestamp = imageProxy.imageInfo.timestamp
        if (bitmap != null) {

            //TODO: bug cause 1 is : bitmap is sended ,maybe release;
            hands.sendFrame(bitmap,timestamp)
            eyesDetected.sendFrame(bitmap,timestamp)
        }
    }
}

@Composable
fun DrawLandmarks(
    landmarks: List<LandmarkProto.NormalizedLandmark>,
    eyesmarks: List<LandmarkProto.NormalizedLandmark>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (landmarks.isNotEmpty()) {
            val fingerConnections = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 4,  // 拇指
                5 to 6, 6 to 7, 7 to 8,         // 食指
                9 to 10, 10 to 11, 11 to 12,    // 中指
                13 to 14, 14 to 15, 15 to 16,   // 无名指
                17 to 18, 18 to 19, 19 to 20    // 小指
            )

            // 绘制每个手指的骨骼连接线
            for (connection in fingerConnections) {
                val start = landmarks[connection.first]
                val end = landmarks[connection.second]

                val startX = start.x * size.width
                val startY = size.height - (start.y * size.height)  // 修正 y 坐标
                val endX = end.x * size.width
                val endY = size.height - (end.y * size.height)      // 修正 y 坐标

                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }

            // 绘制标记点
            for (landmark in landmarks) {
                val x = landmark.x * size.width
                val y = size.height - (landmark.y * size.height)  // 修正 y 坐标
                drawCircle(color = Color.Red, radius = 5f, center = Offset(x, y))
            }
        }

        if (eyesmarks.isNotEmpty()) {
            val eyeConnections = listOf(
                // 左眼标记连接
                0 to 1, 1 to 2, 2 to 3, 3 to 4,  // 左眼上部
                0 to 4,  // 左眼外侧连接
                5 to 6, 6 to 7, 7 to 8, 8 to 9,  // 左眼下部
                5 to 9,  // 左眼内侧连接

                // 右眼标记连接
                10 to 11, 11 to 12, 12 to 13, 13 to 14, // 右眼上部
                10 to 14, // 右眼外侧连接
                15 to 16, 16 to 17, 17 to 18, 18 to 19, // 右眼下部
                15 to 19  // 右眼内侧连接
            )

            // 绘制眼睛标记的连接线
            for (connection in eyeConnections) {
                val start = eyesmarks[connection.first]
                val end = eyesmarks[connection.second]

                // 交换 x 和 y 坐标并翻转 y 轴
                val startX = start.y * size.width
                val startY = size.height - (start.x * size.height)  // 翻转 y 轴
                val endX = end.y * size.width
                val endY = size.height - (end.x * size.height)      // 翻转 y 轴

                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }

            // 绘制标记点
            for (landmark in eyesmarks) {
                // 交换 x 和 y 坐标并翻转 y 轴
                val x = landmark.y * size.width
                val y = size.height - (landmark.x * size.height)  // 翻转 y 轴
                drawCircle(color = Color.Red, radius = 5f, center = Offset(x, y))
            }
        }
    }
}


//TODO ，这里应该是所有的hand 处理的入口。
@Composable
fun HandGestureRecognitionUI(
    handsDetected: HandsDetected,
    eyesDetected : EyesDetected,
    modifier: Modifier
    //  landmarks: List<LandmarkProto.Landmark>
) {

    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(modifier,onFrameAvailable = { imageProxy ->
            // 在这里处理图像帧
            analyzeFrame(imageProxy, hands =handsDetected , eyesDetected = eyesDetected)
        })
    }
    val handmarks by handsDetected.handsmarksState
    val eyesmarks by eyesDetected.eyesmarksState

    // TODO bug cause 2 's reason ; canvas 被覆盖了。

    DrawLandmarks(handmarks,eyesmarks)


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


