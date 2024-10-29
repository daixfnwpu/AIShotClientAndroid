package com.ai.aishotclientkotlin.ui.screens.shot.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.repository.ShotConfigRespository
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionData
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionSimulator
import com.ai.aishotclientkotlin.engine.shot.ShotCauseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

@HiltViewModel
class GridFilterViewModel @Inject constructor(shotConfigRespository: ShotConfigRespository) :
    ViewModel() {

    private val _distance = mutableFloatStateOf(25f)
    val distance: State<Float> = _distance
    val columnNames = listOf(
        "时间",
        "X",
        "Y",
        "XV",
        "YV",
        "V_ALL",
        "距离",
        "延长线",
        "落差",
        "目标角度",
        "瞄点位置"
    )
    private val _results: State<MutableList<List<String>>> = mutableStateOf(mutableListOf())
    var results = _results
    private val _columns: MutableState<List<String>> = mutableStateOf(columnNames)
    private val _isLoading = mutableStateOf(true) // 初始状态为加载中
    val isLoading: State<Boolean> = _isLoading

    //val shotCauseState = mutableStateOf<ShotCauseState>(ShotCauseState())
    // 注意这个位置，不能够在最开始的位置，否则出现null 访问出错。
    init {
        _columns.value=columnNames
        shotConfigRespository.getCurrentShotCauseShate()?.let { loadData(it) }
    }
    val columns = _columns
    // 将结果转换为 StateList
    fun convertResultsToStateList(
        results: List<ProjectileMotionData>,
        stateList: State<MutableList<List<String>>>
    ) {
        requireNotNull(stateList) { "stateList cannot be null" }
        if (stateList != null) {
            //  stateList.value.clear() // 清空之前的数据
            for (data in results) {
                val entry = listOf(
                    roundToDecimalPlaces(data.time, 2).toString(),
                    roundToDecimalPlaces(data.xPosition, 2).toString(),
                    roundToDecimalPlaces(data.yPosition, 2).toString(),
                    roundToDecimalPlaces(data.xVelocity, 1).toString(),
                    roundToDecimalPlaces(data.yVelocity, 1).toString(),
                    roundToDecimalPlaces(data.totalVelocity, 1).toString(),
                    roundToDecimalPlaces(data.distance, 2).toString(),
                    roundToDecimalPlaces(data.yInitial, 2).toString(),
                    roundToDecimalPlaces(data.yDifference, 2).toString(),
                    roundToDecimalPlaces(data.objectAngle, 1).toString(),
                    roundToDecimalPlaces(data.pointsOnShotHead, 2).toString()
                )
                stateList.value.add(entry) // 添加新的数据条目
            }
        } else {
            // Handle potential null case (though this should not happen in normal usage)
            throw IllegalArgumentException("stateList cannot be null")
        }
    }


    fun loadData(shotCause: ShotCauseState) {
        viewModelScope.launch {
            val position = ProjectileMotionSimulator.calculateTrajectory(shotCause = shotCause)
            val results =
                ProjectileMotionSimulator.transformPostionsToMotion(
                    position,
                    shotCause.shotConfig.eyeToAxisDistance.toDouble(),
                    shotCause.angleTarget.toDouble()
                ) // 替换为实际模拟调用
            check(_results != null) { "State list _results cannot be null!" }
            _distance.value =
                sqrt(shotCause.targetPosReal().first.pow(2) + shotCause.targetPosReal().second.pow(2)) * 1.3f
            convertResultsToStateList(results, _results)

          //  _columns.value.clear()
            _columns.value=columnNames
            _isLoading.value = false
        }
    }

}

fun roundToDecimalPlaces(value: Float, decimalPlaces: Int): Float {
    val factor = 10.0.pow(decimalPlaces).toFloat()
    return round(value * factor) / factor
}

fun roundToDecimalPlaces(value: Double, decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces)
    return round(value * factor) / factor
}