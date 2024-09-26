package com.ai.aishotclientkotlin.engine.ar

import android.content.Context
import android.util.Log
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions

class EyesDetected(val context: Context) {
    fun init() {
        val faceMesh = FaceMesh(
            context, FaceMeshOptions.builder()
                .setRunOnGpu(true) // 是否在 GPU 上运行
                .build()
        )

// 设置结果回调来获取 faceMeshResult
        faceMesh.setResultListener { faceMeshResult ->
            if (faceMeshResult.multiFaceLandmarks().isNotEmpty()) {
                val landmarks = faceMeshResult.multiFaceLandmarks()[0].landmarkList
                processFaceLandmarks(landmarks) // 处理面部关键点
            }
        }
    }


    fun processFaceLandmarks(landmarks: MutableList<LandmarkProto.NormalizedLandmark>) {
        // 遍历所有面部关键点
        landmarks.forEachIndexed { index, landmark ->
            val x = landmark.x // x 坐标 (0.0 - 1.0, 相对于图像宽度)
            val y = landmark.y // y 坐标 (0.0 - 1.0, 相对于图像高度)
            val z = landmark.z // z 坐标，表示深度
            Log.e("FaceMesh", "Landmark $index: x=$x, y=$y, z=$z")
        }

        // 获取眼睛的关键点
        val leftEye = landmarks[468] // 左眼中心
        val rightEye = landmarks[473] // 右眼中心

        val leftEyeX = leftEye.x
        val leftEyeY = leftEye.y
        val rightEyeX = rightEye.x
        val rightEyeY = rightEye.y

        Log.e("FaceMesh", "Left Eye: x=$leftEyeX, y=$leftEyeY")
        Log.e("FaceMesh", "Right Eye: x=$rightEyeX, y=$rightEyeY")
    }


}