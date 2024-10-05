package com.ai.aishotclientkotlin.ui.screens.shot.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import com.ai.aishotclientkotlin.domain.model.bi.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.screens.shot.util.ShotConfigRow
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/*            thinofrubber (皮筋厚度)
4.     crossofrubber (皮筋的夹角):
5.     initlengthofrubber (皮筋的初始长度);
6.     widthofrubber (皮筋的宽度);

humidity (空气湿度);
Cd （圆球的阻力系数，常数）；
rho(空气密度)*/

@HiltViewModel
class ShotConfigBaseViewModel @Inject constructor(val shotConfigRespository : ShotConfigRespository) : ViewModel() {
    var configUI_id by mutableStateOf(0)//只用于界面；
    var isalreadyDown by mutableStateOf(0)//0 ,表示没有下发，1表示已经下发；
    private lateinit var  shotConfig: ShotConfig

    var radius_mm by mutableStateOf(10f)  //毫米，在计算的时候被除以了1000
    var thinofrubber_mm by mutableStateOf(0.45f)//毫米，在计算的时候被除以了1000
    var initlengthofrubber_m by mutableStateOf(0.20f) //米
    var widthofrubber_mm by mutableStateOf(25)  // mm,在计算的时候被除以了1000
    var humidity by mutableStateOf(70)
    var crossofrubber by mutableStateOf(3f) // 度；
    var Cd by mutableStateOf(0.47f)
    var airrho by mutableStateOf(1.225f)
    var velocity by mutableStateOf(60f)
    var pellet by mutableStateOf(PelletClass.MUD)
    var eyeToBowDistance by mutableStateOf(0.7f)
    var eyeToAxisDistance by mutableStateOf(0.06f)
    var shotDoorWidth by mutableStateOf(0.04f)
    var shotHeadWidth by mutableStateOf(0.025f)
    var altitude by mutableStateOf( 0)    /*海拔高度*/

    fun bind(config: ShotConfig) {
        this.radius_mm = config.radius_mm
        this.thinofrubber_mm = config.thinofrubber_mm
        this.configUI_id = config.configUI_id// :Int,// by mutableStateOf(0f)//只用于界面；
        this.initlengthofrubber_m =config.initlengthofrubber_m//: Float,//by mutableStateOf(0.20f) //米
        this. widthofrubber_mm = config.widthofrubber_mm// : Int,//by mutableStateOf(25f)  // mm,在计算的时候被除以了1000
        this.humidity  = config.humidity//: Int,// by mutableStateOf(70f)
        this.crossofrubber  = config.crossofrubber//: Float,//by mutableStateOf(3f) // 度；
        this.Cd = config.Cd//: Float,// by mutableStateOf(0.47f)
        this.airrho = config.airrho//: Float,//by mutableStateOf(1.225f)
        this.velocity = config.velocity// : Float,//by mutableStateOf(60f)
        if(config.pellet == 0 )
        {
            this.pellet = PelletClass.MUD
        }else
        {
            this.pellet = PelletClass.STEEL
        }
        this.eyeToBowDistance = config.eyeToBowDistance//:Float,// by mutableStateOf(0.7f)
        this.eyeToAxisDistance = config.eyeToAxisDistance// : Float,//by mutableStateOf(0.06f)
        this.shotDoorWidth = config.shotDoorWidth//: Float,//by mutableStateOf(0.04f)
        this.shotHeadWidth =config.shotHeadWidth//: Float,//by mutableStateOf(0.025f)
        this.altitude = config.altitude//: Int,//by mutableStateOf( 0f)    /*海拔高度*/
        this. isalreadyDown = config.isalreadyDown//: Int
        this.shotConfig = config
    }
    // 更新某个配置
    fun updateConfig() {
        viewModelScope.launch(Dispatchers.IO){
            shotConfigRespository.updateConfig(getConfig())
        }
    }

    fun getConfig(): ShotConfig {
        val config = this
        val _pellet: Int
        if(config.pellet == PelletClass.MUD)
        {
            _pellet = 0
        }else
        {
            _pellet = 1
        }
        return    ShotConfig(

            radius_mm = config.radius_mm,
            thinofrubber_mm = config.thinofrubber_mm,
            configUI_id = config.configUI_id,// :Int,// by mutableStateOf(0f)//只用于界面；
            initlengthofrubber_m =config.initlengthofrubber_m,//: Float,//by mutableStateOf(0.20f) //米
            widthofrubber_mm = config.widthofrubber_mm,// : Int,//by mutableStateOf(25f)  // mm,在计算的时候被除以了1000
            humidity  = config.humidity,//: Int,// by mutableStateOf(70f)
            crossofrubber  = config.crossofrubber,//: Float,//by mutableStateOf(3f) // 度；
            Cd = config.Cd,//: Float,// by mutableStateOf(0.47f)
            airrho = config.airrho,//: Float,//by mutableStateOf(1.225f)
            velocity = config.velocity,// : Float,//by mutableStateOf(60f)
            pellet = _pellet,
            eyeToBowDistance = config.eyeToBowDistance,//:Float,// by mutableStateOf(0.7f)
            eyeToAxisDistance = config.eyeToAxisDistance,// : Float,//by mutableStateOf(0.06f)
            shotDoorWidth = config.shotDoorWidth,//: Float,//by mutableStateOf(0.04f)
            shotHeadWidth =config.shotHeadWidth,//: Float,//by mutableStateOf(0.025f) altitude = config.altitude,//: Int,//by mutableStateOf( 0f)    /*海拔高度*/
            isalreadyDown = config.isalreadyDown,//: Int
            altitude = config.altitude
        )

    }


}
