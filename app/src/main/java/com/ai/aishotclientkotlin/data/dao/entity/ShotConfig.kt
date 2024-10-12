package com.ai.aishotclientkotlin.data.dao.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity()
data class ShotConfig(
    @PrimaryKey()
    var configUI_id :Long? =null,// by mutableStateOf(0f)//只用于界面；
    var radius_mm :  Float =5f,//by mutableStateOf(10f)  //毫米，在计算的时候被除以了1000
    var thinofrubber_mm : Float = 0.45f,//by mutableStateOf(0.45f)//毫米，在计算的时候被除以了1000
    var initlengthofrubber_m : Float = 0.20f,//by mutableStateOf(0.20f) //米
    var widthofrubber_mm : Int = 25,//by mutableStateOf(25f)  // mm,在计算的时候被除以了1000
    var humidity : Int = 70,// by mutableStateOf(70f)
    var crossofrubber : Float = 3f,//by mutableStateOf(3f) // 度；
    var Cd : Float = 0.47f,// by mutableStateOf(0.47f)
    var airrho : Float = 1.225f,//by mutableStateOf(1.225f)
    var initvelocity : Float = 60f,//by mutableStateOf(60f)
    var pellet : Int = 0,//by mutableStateOf(PelletClass.MUD)
    var eyeToBowDistance :Float = 0.7f,// by mutableStateOf(0.7f)
    var eyeToAxisDistance : Float = 0.06f,//by mutableStateOf(0.06f)
    var shotDoorWidth : Float = 0.04f,//by mutableStateOf(0.04f)
    var shotHeadWidth : Float = 0.025f,//by mutableStateOf(0.025f)
    var altitude : Int = 0,//by mutableStateOf( 0f)    /*海拔高度*/
    val isalreadyDown: Int = 0// 1 is true ,0 is false;
)
