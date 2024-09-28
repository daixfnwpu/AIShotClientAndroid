package com.ai.aishotclientkotlin.engine.mlkt


import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

// TODO: 手势检测的另外实现方式； 其也可以同时检测眼睛的位置。？eyeiner？？？
fun detectHands(inputImage :InputImage, functionhand: (thumbTip: PoseLandmark?, indexTip:PoseLandmark?)-> Unit
                , functioneye: (righteye: PoseLandmark?, lefteye:PoseLandmark?)-> Unit) {
    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)  // 实时检测
        .build()

    val poseDetector = PoseDetection.getClient(options)
    poseDetector.process(inputImage)
        .addOnSuccessListener { pose ->
            val landmarks = pose.allPoseLandmarks  // 获取所有关键点

            // 获取大拇指指尖位置
            val thumbTip = landmarks.find { it.landmarkType == PoseLandmark.RIGHT_THUMB }
            // 获取食指指尖位置
            val indexTip = landmarks.find { it.landmarkType == PoseLandmark.RIGHT_INDEX }
            val rightEye = landmarks.find { it.landmarkType == PoseLandmark.RIGHT_EYE_INNER }
            // 获取食指指尖位置
            val leftEye = landmarks.find { it.landmarkType == PoseLandmark.LEFT_EYE_INNER }

            // 检查指尖是否存在，并输出它们的位置
            if (thumbTip != null) {
                val thumbPosition = thumbTip.position
                println("大拇指指尖位置: (${thumbPosition.x}, ${thumbPosition.y})")
            }

            if (indexTip != null) {
                val indexPosition = indexTip.position
                println("食指指尖位置: (${indexPosition.x}, ${indexPosition.y})")
            }
            functionhand(thumbTip,indexTip)
            functioneye(leftEye,rightEye)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()  // 处理错误
        }


}