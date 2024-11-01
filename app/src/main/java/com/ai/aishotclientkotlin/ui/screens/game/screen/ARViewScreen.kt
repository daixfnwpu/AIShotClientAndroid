package com.ai.aishotclientkotlin.ui.screens.game.screen

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.imagecapture.RgbaImageProxy
import androidx.camera.core.internal.utils.ImageUtil
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
import com.google.common.collect.BiMap
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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

/*
@OptIn(ExperimentalGetImage::class)
fun analyzeFrame(context: Context,bitMap: Bitmap, hands: HandsDetected,eyesDetected: EyesDetected,timestamp: Long) {
    Log.e("AR"," image format is : Bitmap")
   // Log.e("AR"," image info  is : ${imageProxy.imageInfo}")

   // val mediaImage = imageProxy.image
    if (bitMap != null) {

        //TODO，这里有可能有BUG；
        // 将图像传递给 MediaPipe
       // val bitmap = mediaImageToBitmap(mediaImage, imageProxy.imageInfo.rotationDegrees)
      //  val timestamp = imageProxy.imageInfo.timestamp

            Log.e("AR","analyzeFrame")
            //TODO: bug cause 1 is : bitmap is sended ,maybe release;
            hands.sendFrame(bitMap,timestamp)
            eyesDetected.sendFrame(bitMap,timestamp)
            saveImageToPublicDirectory(context = context,bitmap = bitMap, filename = "ip",timestamp = timestamp)

    }
}*/



@OptIn(ExperimentalGetImage::class)
fun analyzeFrame(context: Context,imageProxy: ImageProxy, hands: HandsDetected,eyesDetected: EyesDetected) {
    Log.e("AR"," image format is : ${imageProxy.format}")
    Log.e("AR"," image info  is : ${imageProxy.imageInfo}")

    val  imageRotationDegrees = imageProxy.imageInfo.rotationDegrees
    val bitmapBuffer = Bitmap.createBitmap(
        imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
    imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)  }

    if (bitmapBuffer != null) {
        //TODO，这里有可能有BUG；
        // 将图像传递给 MediaPipe
        val timestamp = imageProxy.imageInfo.timestamp
        if (bitmapBuffer != null) {
            Log.e("AR","analyzeFrame")
            //TODO: bug cause 1 is : bitmap is sended ,maybe release;
            hands.sendFrame(bitmapBuffer,timestamp)
            eyesDetected.sendFrame(bitmapBuffer,timestamp)
            saveImageToPublicDirectory(context = context,bitmap = bitmapBuffer, filename = "ip",timestamp = timestamp)
        }
    }
}

fun saveImageToPublicDirectory(context: Context, bitmap: Bitmap, filename: String,timestamp: Long) {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename$timestamp.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        resolver.openOutputStream(it).use { outputStream ->
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    }
}



//TODO ，这里应该是所有的hand 处理的入口。
@Composable
fun StartVRGame(
    handsDetected: HandsDetected,
    eyesDetected : EyesDetected,
    modifier: Modifier,
    showDrawLandmark: Boolean = false
    //  landmarks: List<LandmarkProto.Landmark>
) {

    Box(modifier = modifier) {
        DualCameraScreen(handsDetected, eyesDetected )
    }

    // TODO bug cause 2 's reason ; canvas 被覆盖了。
    if(showDrawLandmark)
    {
        val handmarks by handsDetected.handsmarksState
        val eyesmarks by eyesDetected.eyesmarksState
        DrawLandmarks(modifier,handmarks,eyesmarks)
    }
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


