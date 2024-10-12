package com.ai.aishotclientkotlin.ui.screens.shot.model.show

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionData
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionSimulator
import com.ai.aishotclientkotlin.engine.shot.ShotCauseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GridFilterViewModel @Inject constructor(shotViewModel:ShotViewModel) : ViewModel() {

    private val _results: State<MutableList<List<String>>> = mutableStateOf(mutableListOf())
    val results = _results
    private val _columns: State<MutableList<String>> = mutableStateOf(mutableListOf())
    val columns = _columns
    private val _isLoading = mutableStateOf(true) // 初始状态为加载中
    val isLoading: State<Boolean> = _isLoading

    // 注意这个位置，不能够在最开始的位置，否则出现null 访问出错。
    init {
        loadData(shotViewModel.)
    }
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
                    data.time.toString(),
                    data.xPosition.toString(),
                    data.yPosition.toString(),
                    data.xVelocity.toString(),
                    data.yVelocity.toString(),
                    data.totalVelocity.toString(),
                    data.distance.toString(),
                    data.yInitial.toString(),
                    data.yDifference.toString(),
                    data.objectAngle.toString(),
                    data.pointsOnShotHead.toString()
                )
                stateList.value.add(entry) // 添加新的数据条目
            }
        } else {
            // Handle potential null case (though this should not happen in normal usage)
            throw IllegalArgumentException("stateList cannot be null")
        }
    }


    fun loadData(shotCause : ShotCauseState ) {
        viewModelScope.launch {
            val position = ProjectileMotionSimulator.calculateTrajectory(shotCause = shotCause)
            val results =
                ProjectileMotionSimulator.transformPostionsToMotion(position,
                    shotCause.shotConfig.eyeToAxisDistance.toDouble(),
                    shotCause.angleTarget.toDouble()
                ) // 替换为实际模拟调用
            check(_results != null) { "State list _results cannot be null!" }

            convertResultsToStateList(results, _results)
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
            _columns.value.clear()
            _columns.value.addAll(columnNames)
            _isLoading.value = false
        }
    }

}