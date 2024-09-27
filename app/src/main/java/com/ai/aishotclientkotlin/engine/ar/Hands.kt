package com.ai.aishotclientkotlin.engine.ar

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult


// TODO 寻找在哪里初始化，怎么保持：Hands 的生命周期；
class HandsDetected (val context:Context) {

    lateinit var hands: Hands
    var handsmarksState = mutableStateOf<List<LandmarkProto.NormalizedLandmark>>(emptyList())
    fun init( ) {
        Log.e("AR","init called")
        hands = Hands(
            context, HandsOptions.builder()
                .setMaxNumHands(1)
                .setRunOnGpu(true)
                .build()
        )
        Log.e("AR","setResultListener called")
        hands.setResultListener { handsResult: HandsResult ->
            if (handsResult.multiHandLandmarks().isNotEmpty()) {
                val handMarks = handsResult.multiHandLandmarks()[0].landmarkList
                handsmarksState.value = handMarks
            }
        }
    }

    // 处理图像帧
    fun sendFrame(bitmap: Bitmap, timestamp: Long) {
        if (this::hands.isInitialized) {
            hands.send(bitmap,timestamp)
        }
    }

    // 释放 Hands 资源
    fun release() {
        if (this::hands.isInitialized) {
            hands.close()
        }
    }


}