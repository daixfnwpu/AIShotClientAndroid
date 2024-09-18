package com.ai.aishotclientkotlin.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import kotlin.math.cos
import kotlin.math.sin

val EXTENDFORDISTANCE: Float = 5.0f
class ShotCauseState {

    var radius :Float = (0.005f) // 米；
    var velocity :Float = (60f)  //米/秒；
    var velocityAngle: Float = (45f)
    var angle : Float =(45f)    //度数；
    var density : Float = 2.5f   // 千克/升
    var eyeToBowDistance : Float =(0.7f) // 米；
    var eyeToAxisDistance : Float =(0.06f)  // 米；
    var shotDoorWidth : Float =(0.04f)  //米；
    var shotHeadWidth: Float = 0.020f
    var shotDistance : Float =(20f)  //米
    var shotDiffDistance: Float = Float.NaN
    var angleTarget : Float =angle

    lateinit var targetPosOnTrajectory :    Position
    fun  targetPosReal():  Pair<Float,Float> {
        val rad = Math.toRadians(angleTarget.toDouble())
        return Pair(cos(rad).toFloat() * shotDistance, sin(rad).toFloat() * shotDistance)
    }
}