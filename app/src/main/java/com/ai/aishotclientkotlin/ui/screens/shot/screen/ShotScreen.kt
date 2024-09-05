package com.ai.aishotclientkotlin.ui.screens.shot.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithMenu
import kotlin.math.cos
import kotlin.math.sin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotScreen(
    navController: NavController?,
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
    var isShowCard by remember {
        mutableStateOf(false)
    }
    ExtendedFloatingActionButton(
        onClick = {
            isShowCard = !isShowCard
        },
        modifier = Modifier
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(id = R.string.edit),
        )
        Text(
            text = stringResource(id = R.string.add_entry),
        )
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

      //  if(isShowCard) {
            //  ShotSettingCard()
            AnimatedVisibility(visible = isShowCard) {
                Card(
                    onClick = { /* Do something */ },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.fillMaxSize()) {

                        Text("Clickable", Modifier.align(Alignment.Center))

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        {
                            AppBarWithMenu("Projectile Simulation")
                            SliderInput("Radius (mm)", radius, 0f, 10f) { radius = it }
                            SliderInput("Velocity (m/s)", velocity, 0f, 120f) { velocity = it }
                            SliderInput("Launch Angle (degrees)", angle, 0f, 90f) { angle = it }
                            SliderInput(
                                "Eye to Bow Distance (m)",
                                eyeToBowDistance,
                                0f,
                                1f
                            ) { eyeToBowDistance = it }
                            SliderInput(
                                "Eye to Axis Distance (m)",
                                eyeToAxisDistance,
                                0f,
                                0.1f
                            ) { eyeToAxisDistance = it }
                            SliderInput(
                                "Shot Door Width (m)",
                                shotDoorWidth,
                                0f,
                                0.1f
                            ) { shotDoorWidth = it }
                            SliderInput(
                                "Shot Distance (m)",
                                shotDistance,
                                0f,
                                200f
                            ) { shotDistance = it }

                        }

                    }
                }
            }
      //  }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotSettingCard(
    radius:MutableState<Float>,
    velocity: MutableState<Float>,
    angle:MutableState<Float>,
    eyeToBowDistance:MutableState<Float>,
    eyeToAxisDistance:MutableState<Float>,
    shotDoorWidth:MutableState<Float>,
    shotDistance:MutableState<Float>
){
    Card(
        onClick = { /* Do something */ },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(Modifier.fillMaxSize()) {

            Text("Clickable", Modifier.align(Alignment.Center))

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp))
            {
                AppBarWithMenu("Projectile Simulation")
                SliderInput("Radius (mm)", radius.value, 0f, 10f) { radius.value = it }
                SliderInput("Velocity (m/s)", velocity.value, 0f, 120f) { velocity.value = it }
                SliderInput("Launch Angle (degrees)", angle.value, 0f, 90f) { angle.value = it }
                SliderInput("Eye to Bow Distance (m)", eyeToBowDistance.value, 0f, 1f) { eyeToBowDistance.value = it }
                SliderInput("Eye to Axis Distance (m)", eyeToAxisDistance.value, 0f, 0.1f) { eyeToAxisDistance.value = it }
                SliderInput("Shot Door Width (m)", shotDoorWidth.value, 0f, 0.1f) { shotDoorWidth.value = it }
                SliderInput("Shot Distance (m)", shotDistance.value, 0f, 200f) { shotDistance.value = it }

            }

        }
    }
}

@Composable
fun SliderInput(label: String, value: Float, rangeStart: Float, rangeEnd: Float, onValueChange: (Float) -> Unit) {
    Row {
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
    ShotScreen(navController = null)
}

