package com.ai.aishotclientkotlin.engine.ar

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult


// TODO 寻找在哪里初始化，怎么保持：Hands 的生命周期；
class HandsDetected (val context:Context) {

    lateinit var hands: Hands
    var landmarksState = mutableStateOf<List<LandmarkProto.NormalizedLandmark>>(emptyList())
    fun init( ) {
        hands = Hands(
            context, HandsOptions.builder()
                .setMaxNumHands(1)
                .setRunOnGpu(true)
                .build()
        )

        hands.setResultListener { handsResult: HandsResult ->
            if (handsResult.multiHandLandmarks().isNotEmpty()) {
                val handMarks = handsResult.multiHandLandmarks()[0].landmarkList
                landmarksState.value = handMarks
            }
        }
    }

    // 处理图像帧
    fun sendFrame(bitmap: Bitmap) {
        if (this::hands.isInitialized) {
            hands.send(bitmap)
        }
    }

    // 释放 Hands 资源
    fun release() {
        if (this::hands.isInitialized) {
            hands.close()
        }
    }


}