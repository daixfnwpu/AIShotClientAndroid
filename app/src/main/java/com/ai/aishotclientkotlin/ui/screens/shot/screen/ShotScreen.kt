package com.ai.aishotclientkotlin.ui.screens.shot.screen


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import kotlin.math.cos
import kotlin.math.sin
@Composable
fun ShotScreen(
    navController: NavController,
    viewModel: ShotViewModel = hiltViewModel(),
    // selectPoster: (MainScreenHomeTab, Long) -> Unit,
    //lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    // UI state variables
    var radius by remember { mutableStateOf(5f) }
    var velocity by remember { mutableStateOf(60f) }
    var angle by remember { mutableStateOf(45f) }
    var density by remember { mutableStateOf(7.6f) }
    var eyeToBowDistance by remember { mutableStateOf(0.7f) }
    var eyeToAxisDistance by remember { mutableStateOf(0.06f) }
    var shotDoorWidth by remember { mutableStateOf(0.04f) }
    var shotDistance by remember { mutableStateOf(20f) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Projectile Simulation")

        SliderInput("Radius (mm)", radius, 0f, 10f) { radius = it }
        SliderInput("Velocity (m/s)", velocity, 0f, 120f) { velocity = it }
        SliderInput("Launch Angle (degrees)", angle, 0f, 90f) { angle = it }
        SliderInput("Eye to Bow Distance (m)", eyeToBowDistance, 0f, 1f) { eyeToBowDistance = it }
        SliderInput("Eye to Axis Distance (m)", eyeToAxisDistance, 0f, 0.1f) { eyeToAxisDistance = it }
        SliderInput("Shot Door Width (m)", shotDoorWidth, 0f, 0.1f) { shotDoorWidth = it }
        SliderInput("Shot Distance (m)", shotDistance, 0f, 200f) { shotDistance = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Canvas to plot the graph
        PlotTrajectory(
            radius = radius * 0.001f,
            velocity = velocity,
            angle = angle,
            shotDistance = shotDistance
        )
    }
}

@Composable
fun SliderInput(label: String, value: Float, rangeStart: Float, rangeEnd: Float, onValueChange: (Float) -> Unit) {
    Column {
        Text(text = "$label: ${"%.2f".format(value)}")
        Slider(value = value, onValueChange = onValueChange, valueRange = rangeStart..rangeEnd)
    }
}

@Composable
fun PlotTrajectory(radius: Float, velocity: Float, angle: Float, shotDistance: Float) {
    val g = 9.81f // Gravitational constant
    val theta = Math.toRadians(angle.toDouble()).toFloat()

    // Example projectile motion calculation
    val timeOfFlight = (2 * velocity * sin(theta)) / g
    val range = (velocity * cos(theta) * timeOfFlight)

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Drawing grid
        for (i in 0..size.width.toInt() step 20) {
            drawLine(
                color = Color.LightGray,
                start = Offset(i.toFloat(), 0f),
                end = Offset(i.toFloat(), size.height)
            )
        }

        // Drawing projectile path (simplified parabolic motion)
        val steps = 100
        for (i in 0..steps) {
            val t = i * timeOfFlight / steps
            val x = velocity * cos(theta) * t
            val y = (velocity * sin(theta) * t) - (0.5f * g * t * t)
            drawCircle(Color.Blue, radius = 5f, center = Offset(x * 50, size.height - y * 50))
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
   // MainScreen()
}

