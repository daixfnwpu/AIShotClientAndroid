package com.ai.aishotclientkotlin.ui.screens.shot.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionData
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionSimulator
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithArrow
import com.ai.aishotclientkotlin.util.ui.custom.MyMarkerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch



//TODO : 还没有找到合适的调用位置；
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectileMotionScreen(viewModel: ShotViewModel = hiltViewModel(), pressOnBack: () -> Unit, modifier :Modifier =Modifier) {
    var v0 by remember { mutableStateOf("60") }
    var theta by remember { mutableStateOf("45") }
    var motionData by remember { mutableStateOf<List<ProjectileMotionData>>(emptyList()) }
    val simulator = remember { ProjectileMotionSimulator }
    val coroutineScope = rememberCoroutineScope()
    val scope = rememberCoroutineScope()
    val shotCause = viewModel.shotCauseState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AppBarWithArrow("弹道模拟", showMenu = true, pressOnBack, menuClick = {
            Log.e("EVENT","menuClick is clicked!!!")
            scope.launch {
            }
        })
        Row(modifier = Modifier.fillMaxWidth())
        {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = v0,
                onValueChange = { v0 = it },
                label = { Text("初速度 (m/s)") }
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = theta,
                onValueChange = { theta = it },
                label = { Text("发射角度 (°)") }
            )
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                modifier = Modifier.weight(0.5f),
                onClick = {
                coroutineScope.launch {

                    val position =
                        ProjectileMotionSimulator.calculateTrajectory(shotCause = shotCause)
                    val results =
                        ProjectileMotionSimulator.transformPostionsToMotion(
                            position,
                            shotCause.shotConfig.eyeToAxisDistance.toDouble(),
                            shotCause.angleTarget.toDouble()
                        )
                    motionData = results
                }
            }) {
                Text("开始模拟")
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        if (motionData.isNotEmpty()) {
            ProjectileChart(motionData,viewModel)
        }
    }
}

@Composable
fun ProjectileChart(data: List<ProjectileMotionData>,viewModel: ShotViewModel) {


    val (line1Slope,line1Intercept)  =viewModel.shotLineSlop_Adj()

    val (line2Slope, line2Intercept) = viewModel.velocityLineSlop_Adj()
    val xRange: ClosedFloatingPointRange<Float> =   0f .. 100f            // x值的范围，用于生成直线的数据

    val context = LocalContext.current

    val entries = data.map { Entry(it.xPosition.toFloat(), it.yPosition.toFloat()) }
    val motionDataSet = LineDataSet(entries, "抛物线轨迹").apply {
        color = ColorTemplate.COLORFUL_COLORS[0]
        valueTextColor = android.graphics.Color.BLACK
    }

    val xRangeList = List((viewModel.sEndX* 1.1).toInt()) { i -> xRange.start + i * (xRange.endInclusive - xRange.start) / 100 }
    val line1Entries = xRangeList.map { x: Float ->
        Entry(x, line1Slope * x + line1Intercept)
    }

    val line1DataSet = LineDataSet(line1Entries, "直线 1").apply {
        color = android.graphics.Color.BLUE
        valueTextColor = android.graphics.Color.BLACK
    }

    val xRangeList2 = List((viewModel.vEndX * 1.1).toInt()) { i -> xRange.start + i * (xRange.endInclusive - xRange.start) / 100 }
    // 生成第二条直线的点
    val line2Entries = xRangeList2.map { x ->
        Entry(x, line2Slope * x + line2Intercept)
    }

    val line2DataSet = LineDataSet(line2Entries, "直线 2").apply {
        color = android.graphics.Color.GREEN
        valueTextColor = android.graphics.Color.BLACK
    }


    val chart: LineChart = remember { LineChart(context) } // TODO: Replace with appropriate context


    // Display the chart
    // 使用 AndroidView 显示 LineChart
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = LineData(listOf(motionDataSet, line1DataSet, line2DataSet) as List<ILineDataSet>)
                this.invalidate()                   // 刷新图表
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                // 动画
                animateX(4000)
                val markerView = MyMarkerView(context)
                marker = markerView
            }
        },
        update = { lineChart ->
            // 为每个 LineDataSet 设置属性
            val motionDataSetWithStyle = motionDataSet.apply {
                setDrawCircles(true)
                circleRadius = 1f
                setCircleColor(android.graphics.Color.RED)
                lineWidth = 0.2f
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER  // 使用贝塞尔曲线模式
            }

            val line1DataSetWithStyle = line1DataSet.apply {
                setDrawCircles(false)  // 直线不需要圆圈
                lineWidth = 0.2f
                valueTextSize = 10f
            }

            val line2DataSetWithStyle = line2DataSet.apply {
                setDrawCircles(false)  // 直线不需要圆圈
                lineWidth = 0.2f
                valueTextSize = 10f
            }

            val lineDataSets = listOf(motionDataSetWithStyle,
                line1DataSetWithStyle, line2DataSetWithStyle) as List<ILineDataSet>

            val data = LineData(lineDataSets).apply {
                setValueTextSize(10f)
                setDrawValues(true)
            }
            lineChart.data = data
            lineChart.invalidate() // 刷新图表
        },

        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun LineChartComponent(data: List<ProjectileMotionData>) {
    val context = LocalContext.current
    val entries = data.map { Entry(it.xPosition.toFloat(), it.yPosition.toFloat()) }
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                // 自定义图表
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                // 动画
                animateX(1000)
            }
        },
        update = { lineChart ->
            // 每次 Composable 重组时更新数据
            val lineDataSet = LineDataSet(entries, "轨迹").apply {
                color = ColorTemplate.getHoloBlue()
                setDrawValues(true)
                setDrawCircles(true)
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(Color.Red.value.toInt())
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            val data = LineData(lineDataSet)
            lineChart.data = data
            lineChart.invalidate() // 刷新图表
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // 设置图表的高度和宽度
    )
}
