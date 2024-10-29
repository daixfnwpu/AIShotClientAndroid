package com.ai.aishotclientkotlin.ui.screens.shot.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.node.Node
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class AiShotScreenModel @Inject constructor(shotConfigRespository: ShotConfigRespository): ViewModel(){

    var shotConfigState = shotConfigRespository.getCurrentShotCauseShate()

    var bulletposition by mutableStateOf(Float3(0f,0f,0f))

    var slingshotNodeRotate by mutableStateOf(Float3(0f,0f,0f))

    fun bulletmoveToByMs(velocity: Float3,duration: Float = 10f) {
        bulletposition.x += bulletposition.x * velocity.x * duration
        bulletposition.y += bulletposition.y * velocity.y * duration
        bulletposition.z += bulletposition.z * velocity.z * duration
    }
    //初始化一系列的皮筋的点位；
    var rubberAnnimatePostions = mutableListOf<Float3>()
    init {
        TODO("listOf() 不能够再这里用于初始化，只是用于compile的编译测试")
        rubberAnnimatePostions.addAll(listOf())
    }
    var slingposition by mutableStateOf(Float3(0f,0f,0f))

    fun slingmoveToByMs(velocity: Float3,duration: Float = 10f) {
        slingposition.x += slingposition.x * velocity.x * duration
        slingposition.y += slingposition.y * velocity.y * duration
        slingposition.z += slingposition.z * velocity.z * duration
    }


    var targetPosition by mutableStateOf(Float3(0f,0f,0f))

    // Return the index of the play animate model;
    fun boommByMs(duration: Float) : Int {
        return  0
    }

    // Game Main Loop :
    fun mainLoop() {
        TODO("Not yet implemented")
    }
    // Game Init Loop:
    suspend fun initLoop(slingshotNode : Node,rotate: (Node,Float3) -> Unit) {
        for( i in 0 .. 7)
        {
            slingmoveToByMs(Float3(0f, 0f, 0.1f))
            delay(100L)
            rotate(slingshotNode,slingshotNodeRotate)
        }
    }

}