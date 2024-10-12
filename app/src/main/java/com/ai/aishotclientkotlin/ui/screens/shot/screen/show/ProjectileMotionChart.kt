package com.ai.aishotclientkotlin.ui.screens.shot.screen.show

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionData
import com.ai.aishotclientkotlin.engine.shot.ProjectileMotionSimulator
import com.ai.aishotclientkotlin.engine.shot.ShotCauseState
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch



//TODO : 还没有找到合适的调用位置；
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectileMotionScreen(shotCause:ShotCauseState) {
    var v0 by remember { mutableStateOf("60") }
    var theta by remember { mutableStateOf("45") }
    var motionData by remember { mutableStateOf<List<ProjectileMotionData>>(emptyList()) }
    val simulator = remember { ProjectileMotionSimulator }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("抛体运动模拟", style = MaterialTheme.typography.headlineMedium)

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {

                val position = ProjectileMotionSimulator.calculateTrajectory(shotCause = shotCause)
                val results =
                    ProjectileMotionSimulator.transformPostionsToMotion(position,
                        shotCause.shotConfig.eyeToAxisDistance.toDouble(),
                        shotCause.angleTarget.toDouble()
                    )
               // val results = simulator.simulateProjectileMotion(v0.toDouble(), theta.toDouble(), 1.0)
                motionData = results
            }
        }) {
            Text("开始模拟")
        }

        Spacer(modifier = Modifier.height(16.dp))

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