package com.ai.aishotclientkotlin.ui.screens.shot.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ai.aishotclientkotlin.engine.Position
import com.ai.aishotclientkotlin.engine.ShotCauseState
import com.ai.aishotclientkotlin.engine.calculateTrajectory
import com.ai.aishotclientkotlin.engine.findPosByShotDistance
import com.ai.aishotclientkotlin.engine.optimizeTrajectory
import com.ai.aishotclientkotlin.engine.optimizeTrajectoryByAngle
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShotViewModel @Inject(
) constructor() : ViewModel() {

    // Define state variables
    var radius by mutableStateOf(5f)

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
            angleTarget = angle
        )
        val optimize = optimizeTrajectoryByAngle(shotCauseState)
        positions = optimize.first
        objectPosition = (optimize.second?.x ?: 0.0f) to (optimize.second?.y ?: 0.0f)
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
}
