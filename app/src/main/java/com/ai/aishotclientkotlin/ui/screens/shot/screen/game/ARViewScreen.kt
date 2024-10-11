package com.ai.aishotclientkotlin.ui.screens.shot.screen.game

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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ai.aishotclientkotlin.engine.mediapipe.EyesDetected
import com.ai.aishotclientkotlin.engine.mediapipe.HandsDetected
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import java.io.ByteArrayOutputStream

@Composable
fun CameraPreview(modifier: Modifier,
    onFrameAvailable: (imageProxy: ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
   // val lifecycleOwner = remember { CustomLifecycleOwner() }
    val desiredFrameRate = 15 // 期望的帧率，例如每秒处理5帧
    var lastFrameTime = System.currentTimeMillis()

    AndroidView(
        factory = {
            //val previewView = PreviewView(cameraView)
            val emptyView = View(context)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
               //    it.setSurfaceProvider(previewView.surfaceProvider)
                    // 如果需要展示相机预览，可以在此设置 SurfaceProvider
                   it.setSurfaceProvider(null) // 暂时不设置 SurfaceProvider
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->

                            val currentTime = System.currentTimeMillis()

                            // 计算两帧之间的时间差
                            if (currentTime - lastFrameTime >= 1000 / desiredFrameRate) {
                                onFrameAvailable(imageProxy)  // 处理帧
                                lastFrameTime = currentTime  // 更新最后处理的时间
                            }
                            imageProxy.close() // Don't forget to close the image!
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, imageAnalyzer
                )
            }, ContextCompat.getMainExecutor(context))
            //TODO 我想要一个空的View；
            emptyView
          //  previewView
        },
        modifier = modifier,
        update = {
            Log.e("AR","update view called")
        }
    )
}


fun calDistanceTwoMark(left: NormalizedLandmark, right: NormalizedLandmark): Double {
    return with(left) {
        val dx = right.x - x
        val dy = right.y - y
        val dz = right.z - z
        kotlin.math.sqrt(dx * dx + dy * dy + dz * dz).toDouble()
    }
}

fun calXDistanceTwoMark(left: NormalizedLandmark, right: NormalizedLandmark): Double {
    return with(left) {
        val dx = right.x - x
        val dy = right.y - y
        val dz = right.z - z
        kotlin.math.sqrt(dx * dx + dy * dy + dz * dz).toDouble()
    }
}
fun calYDistanceTwoMark(left: NormalizedLandmark, right: NormalizedLandmark): Double {
    return with(left) {
        (right.y - y).toDouble()
    }
}
fun calZDistanceTwoMark(left: NormalizedLandmark, right: NormalizedLandmark): Double {
    return with(left) {
        (right.z - z).toDouble()
    }
}
fun calXYDistanceTwoMark(left: NormalizedLandmark, right: NormalizedLandmark): Double {
    return with(left) {
        val dx = right.x - x
        val dy = right.y - y
        kotlin.math.sqrt(dx * dx + dy * dy ).toDouble()
    }
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



//TODO ，这里应该是所有的hand 处理的入口。
@Composable
fun HandGestureRecognitionUI(
    handsDetected: HandsDetected,
    eyesDetected : EyesDetected,
    modifier: Modifier,
    showDrawLandmark: Boolean = false
    //  landmarks: List<LandmarkProto.Landmark>
) {

    Box(modifier = modifier) {
        CameraPreview(modifier,onFrameAvailable = { imageProxy ->
            // 在这里处理图像帧
            analyzeFrame(imageProxy, hands =handsDetected , eyesDetected = eyesDetected)
        })
    }
    val handmarks by handsDetected.handsmarksState
    val eyesmarks by eyesDetected.eyesmarksState

    // TODO bug cause 2 's reason ; canvas 被覆盖了。
    if(showDrawLandmark)
        DrawLandmarks(modifier,handmarks,eyesmarks)


}

@Composable
fun DrawLandmarks(
    modifier: Modifier,
    landmarks: List<LandmarkProto.NormalizedLandmark>,
    eyesmarks: List<LandmarkProto.NormalizedLandmark>,
    isMirrored: Boolean = true  // 增加一个参数控制是否镜像
) {
    Canvas(modifier = modifier) {

        // 计算屏幕上的位置，加入镜像处理
        fun positionOnScreen(landmark: NormalizedLandmark): Pair<Float, Float> {
            val x = if (isMirrored) {
                size.width - (landmark.y * size.width)  // 水平镜像
            } else {
                landmark.y * size.width  // 正常方向
            }
            val y = size.height - (landmark.x * size.height)  // 翻转 y 轴
            return x to y
        }

        // 绘制手部标记和连接线
        if (landmarks.isNotEmpty()) {
            val fingerConnections = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 4,  // 拇指
                5 to 6, 6 to 7, 7 to 8,         // 食指
                9 to 10, 10 to 11, 11 to 12,    // 中指
                13 to 14, 14 to 15, 15 to 16,   // 无名指
                17 to 18, 18 to 19, 19 to 20    // 小指
            )

            for (connection in fingerConnections) {
                val (startX, startY) = positionOnScreen(landmarks[connection.first])
                val (endX, endY) = positionOnScreen(landmarks[connection.second])

                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }
        }

        // 绘制眼部标记和连接线
        if (eyesmarks.isNotEmpty()) {
            val eyeConnections = listOf(
                // 例如 7 to 8 的连接
                7 to 8
            )

            for (connection in eyeConnections) {
                val (startX, startY) = positionOnScreen(eyesmarks[connection.first])
                val (endX, endY) = positionOnScreen(eyesmarks[connection.second])

                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }

            // 绘制某个特定的眼部标记
            val (px, py) = positionOnScreen(eyesmarks[468])
            drawCircle(color = Color.Green, radius = 20f, center = Offset(px, py))

            // 绘制所有眼部标记的点
            for (landmark in eyesmarks) {
                val (x, y) = positionOnScreen(landmark)
                drawCircle(color = Color.Red, radius = 5f, center = Offset(x, y))
            }
        }
    }
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


