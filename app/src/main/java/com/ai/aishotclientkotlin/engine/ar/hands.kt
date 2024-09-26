package com.ai.aishotclientkotlin.engine.ar

import android.content.Context
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult


// TODO 寻找在哪里初始化，怎么保持：Hands 的生命周期；
class HandsDetected constructor(val context:Context) {

    private lateinit var hands: Hands
    fun init() {
        hands = Hands(
            context, HandsOptions.builder()
                .setMaxNumHands(1)
                .setRunOnGpu(true)
                .build()
        )

        hands.setResultListener { handsResult: HandsResult ->
            if (handsResult.multiHandLandmarks().isNotEmpty()) {
                val landmarks = handsResult.multiHandLandmarks()[0].landmarkList
                // 处理手部关键点信息
                processHandLandmarks(landmarks)
            }
        }


    }
    private fun processHandLandmarks(landmarks: MutableList<LandmarkProto.NormalizedLandmark>) {
        // 在这里根据手部关键点执行 3D 手势识别或其他操作
    }
}