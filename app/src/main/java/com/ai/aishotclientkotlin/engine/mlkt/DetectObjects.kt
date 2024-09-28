package com.ai.aishotclientkotlin.engine.mlkt

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions


fun detectObjects(bitMap: Bitmap,function:(boundingBox: Rect,label:String) -> Unit) {
    // 从 Bitmap 创建 InputImage 对象
    val inputImage = InputImage.fromBitmap(bitMap, 0)  // Bitmap 和旋转角度

    val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
        .enableMultipleObjects()  // 检测多个物体
        .enableClassification()   // 分类识别
        .build()

    val detector = ObjectDetection.getClient(options)
    detector.process(inputImage)
        .addOnSuccessListener { detectedObjects ->
            for (obj in detectedObjects) {
                val boundingBox = obj.boundingBox  // 获取物体的边界框
                val label = obj.labels.firstOrNull()?.text ?: "Unknown"
                function(boundingBox,label)
            }
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }



}