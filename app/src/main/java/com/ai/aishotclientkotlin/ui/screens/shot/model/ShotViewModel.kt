package com.ai.aishotclientkotlin.ui.screens.shot.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ai.aishotclientkotlin.data.repository.MovieRepository
import com.ai.aishotclientkotlin.engine.Position
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class ShotViewModel @Inject (
) constructor() : ViewModel() {

    // Define state variables
    var radius by mutableStateOf(5f)
        private set
    var velocity by mutableStateOf(60f)
        private set
    var angle by mutableStateOf(45f)
        private set
    var pellet by mutableStateOf(PelletClass.MUD)
        private set
    var eyeToBowDistance by mutableStateOf(0.7f)
        private set
    var eyeToAxisDistance by mutableStateOf(0.06f)
        private set
    var shotDoorWidth by mutableStateOf(0.04f)
        private set
    var shotDistance by mutableStateOf(20f)
        private set

    // Show or hide card
    var isShowCard by mutableStateOf(false)
        private set
    var showMoreSettings by mutableStateOf(false)
        private set
    // 新增加的字段 // TODO : 这两个字段，应该在两个地方初始化： 自动随着各个参数的变化而变化。或者简单的通过“配置”button进行响应；
    var positions by mutableStateOf(emptyList<Position>())
        private set


    var objectPosition by mutableStateOf(Pair(0f, 0f))
        private set

    // 函数用于更新位置列表
    fun updatePositions(newPositions: List<Position>) {
        positions = newPositions
    }

    // 函数用于更新 objectPosition
    fun updateObjectPosition(x: Float, y: Float) {
        objectPosition = Pair(x, y)
    }

    // Computed value
    val destiny: Float
        get() = when (pellet) {
            PelletClass.MUD -> 2.5f
            PelletClass.STEEL -> 7.6f
            else -> 2.5f
        }

    // Expose functions to update the state
    fun setRadius(value: Float) {
        radius = value
    }

    fun setVelocity(value: Float) {
        velocity = value
    }

    fun setAngle(value: Float) {
        angle = value
    }

    fun setPellet(pelletClass: PelletClass) {
        pellet = pelletClass
    }

    fun toggleCardVisibility() {
        isShowCard = !isShowCard
    }

    fun toggleMoreSettings() {
        showMoreSettings = !showMoreSettings
    }
}
