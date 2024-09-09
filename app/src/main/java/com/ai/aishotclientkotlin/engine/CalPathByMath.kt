package com.ai.aishotclientkotlin.engine

import kotlin.math.*

fun calShotPointWithArgsByMath(
    theta0: Double,  //角度
    eyeToAxisInPixels: Double, //眼睛到皮筋中心线的距离（该距离不是绝对距离，只是像素距离）；
    controlOnAxisInPixels: Double,//皮筋在中心线上的投影距离 像素距离；
    targetX: Double, //目标x
    targetY: Double, //m目标y
    cameraWithDistance: Double, //摄像头离（眼睛的距离,像素距离）；
    cannotSeeDistance: Double,  //看不见的长度（从端头到眼睛，有多长的皮筋无法看到）；像素距离
    slingToEye: Double, // 弹弓的弓门到眼睛的位置。
    shotDoorWidth: Double = 0.04,// 弓门的宽度；
    powerScala: Double = 1.0,  // 力量；
    shotHeadWidth: Double = 0.025 // 端口的宽度；
): Double {

    // Convert angle to radians
    val thetaRad = theta0 * Math.PI / 180.0
    val tanTheta = tan(thetaRad)
    val cosTheta = cos(thetaRad)

    var targetToAxis = (targetY - targetX * tanTheta) * cosTheta
    var down = 1

    if (targetToAxis < 0) {
        down = -1
        targetToAxis = -targetToAxis
    }

    val targetOnAxisToZeroPoint = targetX / cosTheta + targetToAxis * tanTheta

    val cfx = if (cannotSeeDistance != 0.0) {
        controlOnAxisInPixels / cameraWithDistance + cannotSeeDistance
    } else {
        slingToEye
    }

    val eyeX = eyeToAxisInPixels / cameraWithDistance
    var y = (cfx * targetToAxis + eyeX * targetOnAxisToZeroPoint) / (cfx + targetOnAxisToZeroPoint)

    if (down == -1) {
        val targetOnAxisToZeroPointAlt = targetX / cosTheta - targetToAxis * tanTheta
        y = (targetToAxis + eyeX) * targetOnAxisToZeroPointAlt / (targetOnAxisToZeroPointAlt + cfx) - targetToAxis
    }

    // Return the final result
    return y - (shotHeadWidth + shotDoorWidth / 2)
}


