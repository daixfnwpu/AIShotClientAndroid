package com.ai.aishotclientkotlin.ui.screens.shot.model.show

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import com.ai.aishotclientkotlin.engine.shot.Position
import com.ai.aishotclientkotlin.engine.shot.ShotCauseState
import com.ai.aishotclientkotlin.engine.shot.calculateShotPointWithArgs
import com.ai.aishotclientkotlin.engine.shot.optimizeTrajectoryByAngle
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class ShotViewModel @Inject constructor( private val shotConfigRespository : ShotConfigRespository) : ViewModel() {
    // Show or hide card
    var isShowCard by mutableStateOf(false)
    var finishedCalPath  by mutableStateOf(false)
   // objecttheta目标的角度；
   // theta0 (发射角度
    var configUI_id by mutableStateOf(0)//只用于界面；
    var isalreadyDown by mutableStateOf(0)//0 ,表示没有下发，1表示已经下发；

    //TODO，该shotConfig因为为下发的shotConfig；
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


   //TODO : 该发射角度目标角度不一样，是最终计算出来的结果；
    var shotAngle by mutableStateOf(45f)
    var shotDistance by mutableStateOf(20f)
    var objectAngle by mutableStateOf(45f)
    var showMoreSettings by mutableStateOf(false)
    var positionShotHead by mutableStateOf( 0f)
    // 弹道路径 // TODO : 这两个字段，应该在两个地方初始化： 自动随着各个参数的变化而变化。或者简单的通过“配置”button进行响应；
    var positions by mutableStateOf(emptyList<Position>())
    var objectPosition by mutableStateOf(Pair(0f, 0f))

    var is_alread_loadConfig_Already by mutableStateOf(false)

    // TODO; 这里引用了他（lateinit var  shotConfig）会不会出问题？
    var shotCauseState by mutableStateOf(ShotCauseState(shotConfig=ShotConfig()))

    var vEndX by mutableStateOf(100f)
    var vEndY by mutableStateOf(100f)
    var shotHeadX  by mutableStateOf(0.04f)
    var shotHeadY  by mutableStateOf(0.04f)
    var sEndX by mutableStateOf(100f)
    var sEndY by mutableStateOf(100f)




    init {
        viewModelScope.launch(Dispatchers.IO) {
            shotConfigRespository.loadShotConfigAlready(success = {
               // is_alread_loadConfig_Already = true
            }, error = {

                viewModelScope.launch(Dispatchers.Main) {
                    is_alread_loadConfig_Already = true
                }
              //  is_alread_loadConfig_Already = false
            }).collectLatest { configs ->
                if (configs.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        if(configs.isNotEmpty())
                        {
                            shotConfig = configs[0]
                            updatePositionsAndObjectPosition()
                        }else
                        {
                            shotConfig = ShotConfig()
                            Log.e("Http","没有默认的下发配置")
                        }
                        Log.e("Config", " loadShotConfigAlready,${configs.size}")
                        is_alread_loadConfig_Already = true
                    }
                }
                shotConfig = ShotConfig()
                viewModelScope.launch(Dispatchers.Main) {
                    is_alread_loadConfig_Already = true
                }
            }
        }

    }
    // 函数用于更新位置列表 // TODO: 在什么时候调用？
    fun updatePositionsAndObjectPosition() {
        if (!::shotConfig.isInitialized){
            shotConfig = ShotConfig()
            Log.e("ShotConfig","shotConfig is not been init ,so ,init default")
        }
        shotCauseState = ShotCauseState(shotConfig = shotConfig,
            shotDistance  = shotDistance , //米
            angleTarget = objectAngle
        )
        shotCauseState.positions = this.positions
        viewModelScope.launch(Dispatchers.Main) {
            Log.e("Dispatchers", "viewModelScope.launch start :optimizeTrajectoryByAngle ")
            val optimize = optimizeTrajectoryByAngle(shotCauseState)

            Log.e("Dispatchers", "viewModelScope.launch end :optimizeTrajectoryByAngle ")
            positions = optimize.first
            objectPosition = (optimize.second?.x ?: 0.0f) to (optimize.second?.y ?: 0.0f)

            val targetPosOnTrajectory = optimize.second!!
            val targetPos: Pair<Float, Float> = targetPosOnTrajectory.x to targetPosOnTrajectory.y
            val positionShotHead_ = calculateShotPointWithArgs(
                shotCauseState.velocityAngle,
                targetPos = targetPos,
                shotCauseState.shotConfig.eyeToBowDistance,
                shotCauseState.shotConfig.eyeToAxisDistance,
                shotCauseState.shotDistance,
                shotCauseState.shotConfig.shotDoorWidth
            )
            shotCauseState.positionShotHead = positionShotHead_
            positionShotHead = positionShotHead_
            // 这是最终的发射角度； 与目标角度不一样；
            shotAngle = shotCauseState.velocityAngle
            finishedCalPath = true

            shotConfigRespository.setCurrentShotCauseShate(shotCauseState)

            vEndX           =  (sin(Math.toRadians(shotCauseState.velocityAngle.toDouble())) * shotDistance).toFloat()
            vEndY           =  (cos(Math.toRadians(shotCauseState.velocityAngle.toDouble())) * shotDistance).toFloat()
           // val (vEndX,vEndY)   =  velocityLine(shotDistance)
            val shotheadPos = shotConfig.shotHeadWidth + shotConfig.shotDoorWidth - shotCauseState.positionShotHead!!
            shotHeadX       = (-1 * sin(Math.toRadians(shotCauseState.velocityAngle.toDouble()) ) * shotheadPos).toFloat()
            shotHeadY       = (cos(Math.toRadians(shotCauseState.velocityAngle.toDouble()) ) * shotheadPos).toFloat()
            sEndX           = (atan(Math.toRadians(shotAngle.toDouble())) * shotDistance).toFloat()
            sEndY           = (tan(Math.toRadians(shotAngle.toDouble())) * shotDistance).toFloat()
        }

    }

    fun velocityLine(x: Float,startPoint: Pair<Float,Float> = Pair(0.0f,0.0f)) : Float {
        return   (tan(Math.toRadians(shotCauseState.velocityAngle.toDouble())) * x).toFloat()
    }

    fun velocityLineSlop_Adj() : Pair<Float,Float> {
        val slop = vEndY / vEndX
        return Pair<Float,Float>(slop,0f)
    }
    fun shotLineSlop_Adj() : Pair<Float,Float> {
        var tanx = (sEndY - shotHeadY) / (sEndX - shotHeadX)
        val intercept = shotHeadY - tanx * shotHeadX
        return Pair<Float,Float>(tanx,intercept)
    }

    fun shotLine(x: Float) : Float {

       val (tanx,intercept) = shotLineSlop_Adj()
      //  return Pair(slope, intercept)
        return x * tanx + intercept
    }

    // Computed value
    val destiny: Float
        get() = when (pellet) {
            PelletClass.MUD -> 2500f
            PelletClass.STEEL -> 7600f
            else -> 2500f
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
