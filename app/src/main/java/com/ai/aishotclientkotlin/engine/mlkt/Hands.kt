package com.ai.aishotclientkotlin.engine.mlkt


import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*

// TODO: 手势检测的另外实现方式；
fun detectHands(inputImage :InputImage, function: (thumbTip: PoseLandmark?,indexTip:PoseLandmark?)-> Unit ) {
    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)  // 实时检测
        .build()

    val poseDetector = PoseDetection.getClient(options)
    poseDetector.process(inputImage)
        .addOnSuccessListener { pose ->
            val landmarks = pose.allLandmarks  // 获取所有关键点

            // 获取大拇指指尖位置
            val thumbTip = landmarks.find { it.landmarkType == PoseLandmark.LEFT_THUMB_TIP }
            // 获取食指指尖位置
            val indexTip = landmarks.find { it.landmarkType == PoseLandmark.LEFT_INDEX_FINGER_TIP }

            // 检查指尖是否存在，并输出它们的位置
            if (thumbTip != null) {
                val thumbPosition = thumbTip.position
                println("大拇指指尖位置: (${thumbPosition.x}, ${thumbPosition.y})")
            }

            if (indexTip != null) {
                val indexPosition = indexTip.position
                println("食指指尖位置: (${indexPosition.x}, ${indexPosition.y})")
            }
            function(thumbTip,indexTip)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()  // 处理错误
        }


}