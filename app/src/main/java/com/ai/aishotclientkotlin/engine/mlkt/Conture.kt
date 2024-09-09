package com.ai.aishotclientkotlin.engine.mlkt
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ai.aishotclientkotlin.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions


@Composable
fun ObjectDetectionScreen() {
    val context = LocalContext.current

    // Replace with your Bitmap
    val bitmap = getSampleBitmap(context)

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
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Detected Objects:")
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