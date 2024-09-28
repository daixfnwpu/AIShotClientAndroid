package com.ai.aishotclientkotlin.engine.mlkt

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*

//
fun detectEyes(inputImage :InputImage, function: (reye: FaceLandmark?,leye:FaceLandmark?)-> Unit ) {
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .build()

    val detector = FaceDetection.getClient(options)
  //  val bitmap = BitmapFactory.decodeResource(resources, R)
    detector.process(inputImage)
        .addOnSuccessListener { faces ->
            for (face in faces) {
                val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
                val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
                function(leftEye,rightEye)
            }
        }
        .addOnFailureListener { e ->
            e.printStackTrace()  // 处理错误
        }
}