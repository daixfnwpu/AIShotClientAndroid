package com.ai.aishotclientkotlin.ui.screens.shot.model.show

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Float3
import javax.inject.Inject

@HiltViewModel
class AiShotScreenModel @Inject constructor(shotConfigRespository: ShotConfigRespository): ViewModel(){

    var shotConfigState = shotConfigRespository.getCurrentShotCauseShate()

    

    var bulletposition by mutableStateOf(Float3(0f,0f,0f))

    fun bulletmoveToByMs(velocity: Float3,duration: Float = 10f) {
        bulletposition.x += bulletposition.x * velocity.x * duration
        bulletposition.y += bulletposition.y * velocity.y * duration
        bulletposition.z += bulletposition.z * velocity.z * duration
    }



    var slingposition by mutableStateOf(Float3(0f,0f,0f))

    fun slingmoveToByMs(velocity: Float3,duration: Float = 10f) {
        slingposition.x += slingposition.x * velocity.x * duration
        slingposition.y += slingposition.y * velocity.y * duration
        slingposition.z += slingposition.z * velocity.z * duration
    }

    var targetPosition by mutableStateOf(Float3(0f,0f,0f))


    // Game Main Loop :
    fun mainLoop() {

        TODO("Not yet implemented")
    }
    // Game Init Loop:
    fun initLoop() {
        TODO("Not yet implemented")
    }

}