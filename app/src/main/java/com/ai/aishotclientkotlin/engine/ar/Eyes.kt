package com.ai.aishotclientkotlin.engine.ar

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ai.aishotclientkotlin.ui.screens.shot.screen.calDistanceTwoMark
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import com.google.mediapipe.solutions.facemesh.FaceMesh
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions

class EyesDetected(val context: Context) {
/*

以下是 MediaPipe 中与右眼相关的关键点索引：
# Crop the right eye region
def getRightEye(image, landmarks):
    eye_top = int(landmarks[263].y * image.shape[0])
    eye_left = int(landmarks[362].x * image.shape[1])
    eye_bottom = int(landmarks[374].y * image.shape[0])
    eye_right = int(landmarks[263].x * image.shape[1])
    right_eye = image[eye_top:eye_bottom, eye_left:eye_right]
    return right_eye

右眼内角：133
右眼外角：263
右眼上方：159
右眼下方：145
右眼瞳孔：468（可选）

 */

    private val RIGHT_EYE_INNER_INDEX = 133  // 右眼内角
    private val RIGHT_EYE_OUTER_INDEX = 263  // 右眼外角
    private val RIGHT_EYE_UP_INDEX = 263  // 右眼上方
    private val RIGHT_EYE_DOWN_INDEX = 263  // 右眼下方
    private val RIGHT_EYE_PUPIL_INDEX = 468  // 右眼瞳孔


    var eyesmarksState = mutableStateOf<List<LandmarkProto.NormalizedLandmark>>(emptyList())

    var rigthEyeCenterState = mutableStateOf<NormalizedLandmark>(NormalizedLandmark.getDefaultInstance())
    var distanceBetweenTwoEye = mutableStateOf(0.0)
    lateinit var  faceMesh:FaceMesh
    fun init() {
        faceMesh = FaceMesh(
            context, FaceMeshOptions.builder()
                .setRunOnGpu(true)// 是否在 GPU 上运行
                .setRefineLandmarks(true) // 启用瞳孔等精细关键点检测
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
        if(landmarks.value.size >= 469) {
            rigthEyeCenterState.value = landmarks.value[RIGHT_EYE_PUPIL_INDEX];
        }else{
            processEyesLandmarks(landmarks)
            val landmarks = eyesmarksState.value
            // 获取右眼的关键点
            val rightEyeInnerMark = landmarks[RIGHT_EYE_INNER_INDEX]  // 右眼内角
            val rightEyeOuterMark = landmarks[RIGHT_EYE_OUTER_INDEX]  // 右眼外角
            val rightEyeUpMark = landmarks[RIGHT_EYE_UP_INDEX]        // 右眼上方
            val rightEyeDownMark = landmarks[RIGHT_EYE_DOWN_INDEX]    // 右眼下方

            val centerX = (rightEyeInnerMark.x + rightEyeOuterMark.x + rightEyeUpMark.x + rightEyeDownMark.x) / 4.0f
            val centerY = (rightEyeInnerMark.y + rightEyeOuterMark.y + rightEyeUpMark.y + rightEyeDownMark.y) / 4.0f
            val centerZ = (rightEyeInnerMark.z + rightEyeOuterMark.z + rightEyeUpMark.z + rightEyeDownMark.z) / 4.0f

            // 使用中心点坐标构造一个 NormalizedLandmark 对象
            val rightEyeCenterMark = NormalizedLandmark.newBuilder()
                .setX(centerX)
                .setY(centerY)
                .setZ(centerZ)
                .build()
            rigthEyeCenterState.value = rightEyeCenterMark ;
        }
        if (landmarks.value.size <= 468) {

            // 使用其他标记点来估算眼睛中心位置，比如鼻子的旁边
            val leftEyeApproximation  =  if  (landmarks.value.size > 2)    landmarks.value[2] else null // 假设索引 2 接近左眼
            val rightEyeApproximation =  if  (landmarks.value.size > 5)    landmarks.value[5] else null // 假设索引 5 接近右眼

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
            val leftEyeZ = leftEye.z
            val rightEyeX = rightEye.x
            val rightEyeY = rightEye.y
            val rightEyeZ = rightEye.z


            // 计算眼睛之间的距离
//            Math.sqrt(
//                Math.pow((rightEyeX - leftEyeX).toDouble(), 2.0) +
//                        Math.pow((rightEyeY - leftEyeY).toDouble(), 2.0) +Math.pow((rightEyeZ - leftEyeZ).toDouble(),2.0)  )
            val eyeDistance = calDistanceTwoMark(rightEye,leftEye)
            Log.e("AR","eyeDistance is : ${eyeDistance}")
            distanceBetweenTwoEye.value = eyeDistance
        }
    }

    fun isRightEyeSquinting(landmarks: List<NormalizedLandmark>): Boolean {
        // 获取右眼上下眼睑的关键点（上：159，下：145）
        val rightEyeTop = landmarks[159]
        val rightEyeBottom = landmarks[145]

        // 计算上下眼睑的垂直距离
        val eyeOpenDistance = Math.abs(rightEyeTop.y - rightEyeBottom.y)

        // 根据阈值判断是否眼睛眯着（阈值可以根据实际情况调整）
        return eyeOpenDistance < 0.02 // 阈值根据经验调整
    }

    fun processEyesLandmarks(eyesmarksState: MutableState<List<NormalizedLandmark>>) {
        val landmarks = eyesmarksState.value

        // 判断右眼是否眯着
        val isSquinting = isRightEyeSquinting(landmarks)
        if (isSquinting) {
            Log.d("FaceMesh", "右眼可能是眯着的")
        } else {
            Log.d("FaceMesh", "右眼睁开")
        }

        // 继续处理其他关键点，如瞳孔
    }

}