package com.ai.aishotclientkotlin.engine.ar

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult
import kotlin.math.sqrt


// TODO 寻找在哪里初始化，怎么保持：Hands 的生命周期；
class HandsDetected (val context:Context) {

    lateinit var hands: Hands
    var handsmarksState = mutableStateOf<List<LandmarkProto.NormalizedLandmark>>(emptyList())
    var thumbAndIndexCenterState = mutableStateOf<NormalizedLandmark>(NormalizedLandmark.getDefaultInstance())
    var isOpenHandleState = mutableStateOf<Boolean>(false)
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
                if(!isHandOpen(handMarks))
                {
                    thumbAndIndexCenterState.value  =   getMidPointBetweenThumbAndIndex(handMarks)
                   // Log.e("ARR","thumbAndIndexCenterState is : ${thumbAndIndexCenterState.value}")
                    isOpenHandleState.value = false
                }
                else {
                    thumbAndIndexCenterState.value  =   NormalizedLandmark.getDefaultInstance()
                    isOpenHandleState.value = true
                   // Log.e("AR","hand is Opened")
                }
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
    // 计算大拇指指尖和食指指尖之间的中间点
    fun getMidPointBetweenThumbAndIndex(landmarks: List<LandmarkProto.NormalizedLandmark>): LandmarkProto.NormalizedLandmark {
        val thumbTip = landmarks[4]  // 大拇指指尖 landmark
        val indexTip = landmarks[8]  // 食指指尖 landmark

        // 计算 x, y, z 坐标的中点
        val midX = (thumbTip.x + indexTip.x) / 2
        val midY = (thumbTip.y + indexTip.y) / 2
        val midZ = (thumbTip.z + indexTip.z) / 2

        // 返回一个新的 NormalizedLandmark，代表中间点的位置
        return LandmarkProto.NormalizedLandmark.newBuilder()
            .setX(midX)
            .setY(midY)
            .setZ(midZ)
            .build()
    }
    // 判断手是否从握紧到张开
    fun isHandOpen(landmarks: List<LandmarkProto.NormalizedLandmark>): Boolean {
        val palmLandmark = landmarks[0] // 手掌 landmark (wrist)

        // 检查每个手指的尖端与手掌的距离
        val thumbTip = landmarks[4]
        val indexTip = landmarks[8]
        val middleTip = landmarks[12]
        val ringTip = landmarks[16]
        val pinkyTip = landmarks[20]

        // 计算每个手指尖端与手掌中心的距离
        fun distance(landmark1: LandmarkProto.NormalizedLandmark, landmark2: LandmarkProto.NormalizedLandmark): Float {
            val dx = landmark1.x - landmark2.x
            val dy = landmark1.y - landmark2.y
            val dz = landmark1.z - landmark2.z
            return sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
        }

        // 距离阈值，假设如果手指尖端与手掌距离超过某个值，就认为手是张开的
        val threshold = 0.1f

        val isThumbOpen = distance(thumbTip, palmLandmark) > threshold
        val isIndexOpen = distance(indexTip, palmLandmark) > threshold
        val isMiddleOpen = distance(middleTip, palmLandmark) > threshold
        val isRingOpen = distance(ringTip, palmLandmark) > threshold
        val isPinkyOpen = distance(pinkyTip, palmLandmark) > threshold

        // 如果所有手指都张开了，就认为手是张开的
        return isThumbOpen && isIndexOpen && isMiddleOpen && isRingOpen && isPinkyOpen
    }


}