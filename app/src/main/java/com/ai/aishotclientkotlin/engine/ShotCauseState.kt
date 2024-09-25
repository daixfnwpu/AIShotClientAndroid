package com.ai.aishotclientkotlin.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import kotlin.math.cos
import kotlin.math.sin

val EXTENDFORDISTANCE: Float = 5.0f
class ShotCauseState(
    var radius: Float = 0.005f, // in meters
    var velocity: Float = 60f,  // in meters per second
    var velocityAngle: Float = Float.NaN,// in degrees
    var density: Float = 2.5f,  // in kg/L
    var eyeToBowDistance: Float = 0.7f, // in meters
    var eyeToAxisDistance: Float = 0.06f,  // in meters
    var shotDoorWidth: Float = 0.04f,  // in meters
    var shotHeadWidth: Float = 0.020f,
    var shotDistance: Float = 20f,  // in meters
    var shotDiffDistance: Float = Float.NaN,
    var angleTarget: Float = 45f, // Default to velocityAngle
    var positionShotHead :Float = 0.0f,
    var positions: List<Position> = emptyList<Position>()
) {
    lateinit var targetPosOnTrajectory: Position

    /**
     * Calculates the real target position based on the angle and distance.
     * @return A Pair representing the x and y coordinates of the target.
     */
    fun targetPosReal(): Pair<Float, Float> {
        val radianAngle = Math.toRadians(angleTarget.toDouble())
        val x = cos(radianAngle).toFloat() * shotDistance
        val y = sin(radianAngle).toFloat() * shotDistance
        return Pair(x, y)
    }
}