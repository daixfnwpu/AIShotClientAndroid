package com.ai.aishotclientkotlin.ui.screens.shot.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ai.aishotclientkotlin.engine.Position
import com.ai.aishotclientkotlin.engine.ShotCauseState
import com.ai.aishotclientkotlin.engine.calculateShotPointWithArgs
import com.ai.aishotclientkotlin.engine.calculateTrajectory
import com.ai.aishotclientkotlin.engine.findPosByShotDistance
import com.ai.aishotclientkotlin.engine.optimizeTrajectory
import com.ai.aishotclientkotlin.engine.optimizeTrajectoryByAngle
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Math.pow
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class ShotViewModel @Inject(
) constructor() : ViewModel() {

    // Define state variables
    var radius by mutableStateOf(10f)  //在计算的时候被除以了1000

    var velocity by mutableStateOf(60f)

    var angle by mutableStateOf(45f)

    var pellet by mutableStateOf(PelletClass.MUD)

    var eyeToBowDistance by mutableStateOf(0.7f)

    var eyeToAxisDistance by mutableStateOf(0.06f)

    var shotDoorWidth by mutableStateOf(0.04f)

    var shotDistance by mutableStateOf(20f)

    var shotHeadWidth by mutableStateOf(0.025f)
    // Show or hide card
    var isShowCard by mutableStateOf(false)

    var showMoreSettings by mutableStateOf(false)

    // 新增加的字段 // TODO : 这两个字段，应该在两个地方初始化： 自动随着各个参数的变化而变化。或者简单的通过“配置”button进行响应；
    var positions by mutableStateOf(emptyList<Position>())


    var objectPosition by mutableStateOf(Pair(0f, 0f))

    var positionShotHead by mutableStateOf( 0f)
    // 函数用于更新位置列表 // TODO: 在什么时候调用？
    fun updatePositionsAndObjectPosition() {
        var shotCauseState = ShotCauseState(
            radius = radius/1000, // 米；
            velocity = velocity, //米/秒；
            velocityAngle =velocity ,//度
            density  = destiny ,  // 千克/升
            eyeToBowDistance  = eyeToBowDistance ,// 米；
            eyeToAxisDistance = eyeToAxisDistance , // 米；
            shotDoorWidth  = shotDoorWidth  ,//米；
            shotHeadWidth= shotHeadWidth,
            shotDistance  = shotDistance , //米
            shotDiffDistance = Float.NaN,
            angleTarget = angle,
            positions = this.positions
        )
        val optimize = optimizeTrajectoryByAngle(shotCauseState)
        positions = optimize.first
        objectPosition = (optimize.second?.x ?: 0.0f) to (optimize.second?.y ?: 0.0f)

        val targetPosOnTrajectory = optimize.second!!
        val targetPos : Pair<Float,Float> = targetPosOnTrajectory.x to targetPosOnTrajectory.y
        val positionShotHead = calculateShotPointWithArgs(shotCauseState.velocityAngle,
            targetPos = targetPos,
            shotCauseState.eyeToBowDistance,
            shotCauseState.eyeToAxisDistance,
            shotCauseState.shotDistance,
            shotCauseState.shotDoorWidth)
        shotCauseState.positionShotHead = positionShotHead
        this.positionShotHead =positionShotHead
    }


    // Computed value
    val destiny: Float
        get() = when (pellet) {
            PelletClass.MUD -> 2.5f
            PelletClass.STEEL -> 7.6f
            else -> 2.5f
        }


    fun toggleCardVisibility() {
        isShowCard = !isShowCard
    }

    fun toggleMoreSettings() {
        showMoreSettings = !showMoreSettings
    }
    fun getPointOfPosition(x:Float,y:Float): Pair<Int, Int> {
        return findClosestTwoIndices(positions.map { it -> it.x },x)

    }
    fun getVelocityOfTargetObject() : Pair<Float,Float>{
       var p =  getPointOfPosition(objectPosition.first,objectPosition.second)
       var v =  (sqrt(
            positions[p.first].vx.toDouble().pow(2.0) + positions[p.first].vy.toDouble()
            .pow(2.0)
        ) + sqrt(
            positions[p.second].vx.toDouble().pow(2.0) + positions[p.second].vy.toDouble().pow(2.0)
        )).toFloat()/2
        val t = (positions[p.second].t+ positions[p.second].t)/2
        return v to t
    }


    fun findClosestTwoIndices(sortedList: List<Float>, target: Float): Pair<Int, Int> {
        if (sortedList.isEmpty()) throw IllegalArgumentException("List cannot be empty")
        if (sortedList.size == 1) throw IllegalArgumentException("List must have at least two elements")

        var left = 0
        var right = sortedList.size - 1

        // 二分查找
        while (left < right) {
            val mid = (left + right) / 2
            if (sortedList[mid] == target) {
                // 如果找到目标值，直接返回左右相邻两个点的位置
                val leftIndex = (mid - 1).takeIf { it >= 0 } ?: mid
                val rightIndex = (mid + 1).takeIf { it < sortedList.size } ?: mid
                return Pair(leftIndex, rightIndex)
            } else if (sortedList[mid] < target) {
                left = mid + 1
            } else {
                right = mid
            }
        }

        // 比较 left 和 left-1 的位置
        val leftIndex = (left - 1).takeIf { it >= 0 } ?: left
        val rightIndex = left.takeIf { it < sortedList.size } ?: (left - 1)

        return Pair(leftIndex, rightIndex)
    }

}
