package com.ai.aishotclientkotlin.engine.mediapipe

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.ai.aishotclientkotlin.engine.shot.getOutputRubber
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

    //皮筋的拉长长度；
    var powerSlubber = mutableStateOf(0.01)
    //判断是否是等腰三角形；
    var isoscelesTriangle  = mutableSetOf(thumbAndIndexCenterState.value.x - 0)
    //发射角度
    var shotAngle  = mutableSetOf(thumbAndIndexCenterState.value.y / thumbAndIndexCenterState.value.z)


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
            Log.e("AR","handsResult")
            if (handsResult.multiHandLandmarks().isNotEmpty()) {
                val handMarks = handsResult.multiHandLandmarks()[0].landmarkList
                handsmarksState.value = handMarks

                if(!isHandOpen(handMarks))
                {
                    thumbAndIndexCenterState.value  =   getMidPointBetweenThumbAndIndex(handMarks)
                    powerSlubber.value = calShotVelocity(thumbAndIndexCenterState.value.z.toDouble())
                    Log.e("AR","thumbAndIndexCenterState is : ${thumbAndIndexCenterState.value}")
                    isOpenHandleState.value = false
                }
                else {
                    isOpenHandleState.value = true
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

    // TODO: 应该有一个对应的表格关系，或者是公式，来计算，皮筋的拉伸长度和子弹的重量之间的关系；
    fun calShotVelocity(power:Double,subber: Double = 1.0) :Double {
        return power * getOutputRubber(subber,1,1,1,)
    }
}