package com.ai.aishotclientkotlin.ui.screens.shot.screen.show

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
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
import com.ai.aishotclientkotlin.engine.shot.ShotCauseState
import com.ai.aishotclientkotlin.ui.screens.shot.model.show.ShotViewModel
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithArrow
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch



//TODO : 还没有找到合适的调用位置；
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectileMotionScreen(viewModel: ShotViewModel = hiltViewModel(),pressOnBack: () -> Unit, modifier :Modifier =Modifier) {
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
                value = v0,
                onValueChange = { v0 = it },
                label = { Text("初速度 (m/s)") }
            )
            OutlinedTextField(
                value = theta,
                onValueChange = { theta = it },
                label = { Text("发射角度 (°)") }
            )
            Spacer(modifier = Modifier.height(2.dp))
            Button(onClick = {
                coroutineScope.launch {

                    val position =
                        ProjectileMotionSimulator.calculateTrajectory(shotCause = shotCause)
                    val results =
                        ProjectileMotionSimulator.transformPostionsToMotion(
                            position,
                            shotCause.shotConfig.eyeToAxisDistance.toDouble(),
                            shotCause.angleTarget.toDouble()
                        )
                    // val results = simulator.simulateProjectileMotion(v0.toDouble(), theta.toDouble(), 1.0)
                    motionData = results
                }
            }) {
                Text("开始模拟")
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        if (motionData.isNotEmpty()) {
            ProjectileChart(motionData)
        }
    }
}

@Composable
fun ProjectileChart(data: List<ProjectileMotionData>) {
    val context = LocalContext.current
    val entries = data.map { Entry(it.xPosition.toFloat(), it.yPosition.toFloat()) }
    val dataSet = LineDataSet(entries, "轨迹").apply {
        color = android.graphics.Color.BLUE
        valueTextColor = android.graphics.Color.BLACK
    }

    val chart: LineChart = remember { LineChart(context) } // TODO: Replace with appropriate context

    // Update the chart data
    chart.data = LineData(dataSet)
    chart.invalidate()  // 刷新图表

    // Display the chart
    AndroidView({ chart }, modifier = Modifier.fillMaxSize())
}


@Composable
fun LineChartComponent(entries: List<Entry>) {
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
            val lineDataSet = LineDataSet(entries, "Sample Data").apply {
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
