package com.ai.aishotclientkotlin.data.dao.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity()
data class ShotConfig(
    @PrimaryKey(autoGenerate = true)
    var configUI_id :Long? =null,// by mutableStateOf(0f)//只用于界面；
    var radius_mm :  Float,//by mutableStateOf(10f)  //毫米，在计算的时候被除以了1000
    var thinofrubber_mm : Float,//by mutableStateOf(0.45f)//毫米，在计算的时候被除以了1000
    var initlengthofrubber_m : Float,//by mutableStateOf(0.20f) //米
    var widthofrubber_mm : Int,//by mutableStateOf(25f)  // mm,在计算的时候被除以了1000
    var humidity : Int,// by mutableStateOf(70f)
    var crossofrubber : Float,//by mutableStateOf(3f) // 度；
    var Cd : Float,// by mutableStateOf(0.47f)
    var airrho : Float,//by mutableStateOf(1.225f)
    var velocity : Float,//by mutableStateOf(60f)
    var pellet : Int,//by mutableStateOf(PelletClass.MUD)
    var eyeToBowDistance :Float,// by mutableStateOf(0.7f)
    var eyeToAxisDistance : Float,//by mutableStateOf(0.06f)
    var shotDoorWidth : Float,//by mutableStateOf(0.04f)
    var shotHeadWidth : Float,//by mutableStateOf(0.025f)
    var altitude : Int,//by mutableStateOf( 0f)    /*海拔高度*/
    val isalreadyDown: Int // 1 is true ,0 is false;
)
