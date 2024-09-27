package com.ai.aishotclientkotlin.engine.ar

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions

class EyesDetected(val context: Context) {

    var eyesmarksState = mutableStateOf<List<LandmarkProto.NormalizedLandmark>>(emptyList())
    lateinit var  faceMesh:FaceMesh
    fun init() {
        faceMesh = FaceMesh(
            context, FaceMeshOptions.builder()
                .setRunOnGpu(true) // 是否在 GPU 上运行
                .build()
        )

// 设置结果回调来获取 faceMeshResult
        faceMesh.setResultListener { faceMeshResult ->
            if (faceMeshResult.multiFaceLandmarks().isNotEmpty()) {
                eyesmarksState.value = faceMeshResult.multiFaceLandmarks()[0].landmarkList
                processFaceLandmarks(eyesmarksState) // 处理面部关键点
            }
        }
    }


    // 处理图像帧
    fun sendFrame(bitmap: Bitmap, timestamp: Long) {
        if (this::faceMesh.isInitialized) {
            faceMesh.send(bitmap,timestamp)
        }
    }

    // 释放 Hands 资源
    fun release() {
        if (this::faceMesh.isInitialized) {
            faceMesh.close()
        }
    }

    fun processFaceLandmarks(landmarks: MutableState<List<LandmarkProto.NormalizedLandmark>>) {
        // 如果 landmarks 数据量不足 468 个标记点
        if (landmarks.value.size <= 468) {

            // 使用其他标记点来估算眼睛中心位置，比如鼻子的旁边
            val leftEyeApproximation = if (landmarks.value.size > 2) landmarks.value[2] else null // 假设索引 2 接近左眼
            val rightEyeApproximation = if (landmarks.value.size > 5) landmarks.value[5] else null // 假设索引 5 接近右眼

            leftEyeApproximation?.let {
                Log.e("FaceMesh", "Approximated Left Eye: x=${it.x}, y=${it.y}")
            }

            rightEyeApproximation?.let {
                Log.e("FaceMesh", "Approximated Right Eye: x=${it.x}, y=${it.y}")
            }

        } else if(landmarks.value.size > 473) {
            // 当 landmarks 数据量足够时，直接使用索引 468 和 473 的点
            val leftEye = landmarks.value[468]
            val rightEye = landmarks.value[473]

            val leftEyeX = leftEye.x
            val leftEyeY = leftEye.y
            val rightEyeX = rightEye.x
            val rightEyeY = rightEye.y


            // 计算眼睛之间的距离
            val eyeDistance = Math.sqrt(
                Math.pow((rightEyeX - leftEyeX).toDouble(), 2.0) +
                        Math.pow((rightEyeY - leftEyeY).toDouble(), 2.0)
            )
        }
    }


}