package com.ai.aishotclientkotlin.engine.mlkt
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.ai.aishotclientkotlin.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions


@Composable
fun ObjectDetectionScreen() {
    val context = LocalContext.current

    // Replace with your Bitmap
    val bitmap = getSampleBitmap(context)
  //  detectFaceContours(bitmap)
    // Object detection results state
    var detectedObjects by remember { mutableStateOf<List<DetectedObject>>(emptyList()) }

    // Create object detector
    val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE) // 单张图片检测
        .enableMultipleObjects() // 启用多对象检测
        .enableClassification() // 启用对象分类
        .build()

    val objectDetector: ObjectDetector = ObjectDetection.getClient(options)

    // Run object detection on a bitmap
    LaunchedEffect(bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        objectDetector.process(inputImage)
            .addOnSuccessListener { objects ->
                detectedObjects = objects
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // Display the image and detected objects
    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Input Image",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text("Detected Objects: ${detectedObjects.size}")
        for (detectedObject in detectedObjects) {
            Text("Object ID: ${detectedObject.trackingId ?: "N/A"}")
            for (label in detectedObject.labels) {
                Text("Label: ${label.text}, Confidence: ${label.confidence}")
            }
        }
    }
}

// Sample function to get a bitmap (replace this with your image loading logic)
fun getSampleBitmap(context: Context): Bitmap {
    return BitmapFactory.decodeResource(context.resources, R.drawable.rubber)
}

@Composable
fun detectFaceContours(bitmap: Bitmap) {
    var detectedFaces by remember { mutableStateOf<List<Face>>(emptyList()) }
    var detectedObjects by remember { mutableStateOf<List<PointF>>(emptyList()) }
    val options = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL) // 启用所有轮廓检测
        .build()

    val faceDetector = FaceDetection.getClient(options)
    val inputImage = InputImage.fromBitmap(bitmap, 0)
    LaunchedEffect(bitmap) {
        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                detectedFaces = faces
                for (face in faces) {

                    val contours = face.allContours
                    for (contour in contours) {
                        detectedObjects = contour.points
                        // 处理每个点，绘制轮廓或执行其他操作

                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // 显示检测结果
    Box(modifier = Modifier.fillMaxSize()) {
        // 显示原始图片
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // 绘制检测到的物体边界框和标签
        DrawDetectedFaces(detectedFaces)
    }

}
@Composable
fun DrawDetectedFaces(detectedFaces: List<Face>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        detectedFaces.forEach { detectedFace ->
            // 获取物体的边界框
            val boundingBox = detectedFace.boundingBox

            // 绘制边界框
            drawRect(
                color = androidx.compose.ui.graphics.Color.Red,
                topLeft = Offset(boundingBox.left.toFloat(), boundingBox.top.toFloat()),
                size = androidx.compose.ui.geometry.Size(boundingBox.width().toFloat(), boundingBox.height().toFloat()),
                style = Stroke(width = 4f)
            )




                val contours = detectedFace.allContours
                for (contour in contours) {
                    Log.e("com.ai.aishotclientkotlin",contour.points.toString())
                    drawPoints(contour.points.fastMap { p ->
                        return@fastMap Offset(p.x,p.y)
                    }, PointMode.Lines, color = androidx.compose.ui.graphics.Color.Red)
                }
        }
    }
}

@Composable
fun ObjectDetectionScreen(bitmap: Bitmap) {
    var detectedObjects by remember { mutableStateOf<List<DetectedObject>>(emptyList()) }

    // 创建对象检测器
    val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .build()

    val objectDetector = ObjectDetection.getClient(options)

    // 执行对象检测
    LaunchedEffect(bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        objectDetector.process(image)
            .addOnSuccessListener { objects ->
                detectedObjects = objects
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // 显示检测结果
    Box(modifier = Modifier.fillMaxSize()) {
        // 显示原始图片
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // 绘制检测到的物体边界框和标签
        DrawDetectedObjects(detectedObjects)
    }
}
@Composable
fun DrawDetectedObjects(detectedObjects: List<DetectedObject>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        detectedObjects.forEach { detectedObject ->
            // 获取物体的边界框
            val boundingBox = detectedObject.boundingBox

            // 绘制边界框
            drawRect(
                color = androidx.compose.ui.graphics.Color.Red,
                topLeft = Offset(boundingBox.left.toFloat(), boundingBox.top.toFloat()),
                size = androidx.compose.ui.geometry.Size(boundingBox.width().toFloat(), boundingBox.height().toFloat()),
                style = Stroke(width = 4f)
            )

            // 绘制分类标签（如果有）
            detectedObject.labels.forEach { label ->
                drawContext.canvas.nativeCanvas.drawText(
                    "${label.text} (${(label.confidence * 100).toInt()}%)",
                    boundingBox.left.toFloat(),
                    boundingBox.top.toFloat() - 10,
                    Paint().apply {
                        color = android.graphics.Color.RED
                        textSize = 40f
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}


@Composable
fun ObjectDetectionDemo() {
    // 示例图片（你可以从相机或其他资源中加载）
    val bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.facedetect)

    ObjectDetectionScreen(bitmap = bitmap)
}
