package com.ai.aishotclientkotlin.engine.shot

import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import kotlin.math.cos
import kotlin.math.sin

val EXTENDFORDISTANCE: Float = 5.0f
class ShotCauseState(
    val shotConfig: ShotConfig,
    val angleTarget: Float = 45f,
    val shotDistance: Float =45f,
) {
    var positionShotHead: Float? = null
    var shotDiffDistance: Float? = null
    var positions: List<Position> = listOf()
    lateinit var targetPosOnTrajectory: Position
    var velocityAngle : Float = angleTarget
    private var velocity : Float = shotConfig.initvelocity
    private var crossofrubber: Float = shotConfig.crossofrubber
    /**
     * Calculates the real target position based on the angle and distance.
     * @return A Pair representing the x and y coordinates of the target.
     */
    fun getVelocity(): Float =  velocity
    fun setVelocity(velocity0: Float)  {
        shotConfig.initvelocity = velocity0
        velocity = velocity0
    }

    fun getCrossFrubber() : Float = crossofrubber

    fun setCrossFrubber(cFrubber: Float) {
        shotConfig.crossofrubber = cFrubber
        crossofrubber= cFrubber
    }

    fun targetPosReal(): Pair<Float, Float> {
        val radianRadians = Math.toRadians(angleTarget.toDouble())
        val x = cos(radianRadians).toFloat() * shotDistance
        val y = sin(radianRadians).toFloat() * shotDistance
        return Pair(x, y)
    }
    val destiny: Float
        get() = when (shotConfig.pellet) {
            PelletClass.MUD.value -> 2500f
            PelletClass.STEEL.value -> 7600f
            else -> 2500f
        }
}