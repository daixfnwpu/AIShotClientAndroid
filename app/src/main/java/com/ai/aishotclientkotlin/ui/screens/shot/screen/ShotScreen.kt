package com.ai.aishotclientkotlin.ui.screens.shot.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.engine.ShotCauseState
import com.ai.aishotclientkotlin.engine.calculateTrajectory
import com.ai.aishotclientkotlin.engine.findPosByShotDistance
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import com.ai.aishotclientkotlin.util.ui.custom.PelletClass
import com.ai.aishotclientkotlin.util.ui.custom.PelletClassOption
import com.ai.aishotclientkotlin.util.ui.custom.RadiusComboBox
import com.ai.aishotclientkotlin.util.ui.custom.SliderWithTextField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotScreen(
    navController: NavController?,
    viewModel: ShotViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = {
             //   isShowCard = !isShowCard
                if(viewModel.isShowCard) {
                    viewModel.updatePositionsAndObjectPosition()
                }
                viewModel.toggleCardVisibility()

            },
            shape = FloatingActionButtonDefaults.smallShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)  // Aligns it to the bottom-right
                // TODO ,adjust the right padding.
                .padding(16.dp)  // Adds padding from the edges
                .clip(CircleShape)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit),
            )
            Text(
                text = stringResource(id = R.string.add_entry), fontSize = 16.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                //  .height(24.dp)
                .padding(16.dp)
        ) {
            AnimatedVisibility(visible = viewModel.isShowCard) {
                Card(
                    onClick = { /* Do something */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .height(600.dp)
                        .verticalScroll(scrollState), // 垂直滚动
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()

                                .padding(16.dp)
                        )
                        {
                            SliderWithTextField(
                                stringResource(R.string.shot_distance),
                                remember {
                                    mutableStateOf(viewModel.shotDistance)
                                },
                                0f,
                                100f,
                                steps = 100
                            ) { viewModel.shotDistance= it }
                            SliderWithTextField(
                                stringResource(R.string.launch_angle),

                                remember {
                                    mutableStateOf(viewModel.angle)
                                },
                                -90f,
                                90f,
                                steps = 180
                            ) { viewModel.angle = (it) }

                            //!!TODO: change to ,need then show and modify it;
                            PelletClassOption(selectedOption = remember {
                                mutableStateOf(viewModel.pellet)
                            })

                            // More Settings

                            TextButton(onClick = { viewModel.toggleMoreSettings() }) {
                                Row {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "more",
                                        tint = Color.Red,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        if (viewModel.showMoreSettings) "隐藏更多设置" else "更多设置",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            AnimatedVisibility(visible = viewModel.showMoreSettings) {
                                Column {

                                    RadiusComboBox(
                                        radius = remember { mutableStateOf(viewModel.radius) },
                                        label = stringResource(R.string.radius),
                                        radiusOptions = listOf(6f, 7f, 8f, 9f, 10f, 11f, 12f)
                                    );
                                    // SliderInput(stringResource(R.string.radius), radius, 6f, 12f, step = 6) { velocity = it }

                                    SliderWithTextField(
                                        stringResource(R.string.velocity),

                                        remember {
                                            mutableStateOf(viewModel.velocity)
                                        },
                                        40f,
                                        120f,
                                        steps = 80
                                    ) { viewModel.velocity=it }

                                    SliderWithTextField(
                                        stringResource(R.string.eye_to_bow_distance),

                                        remember {
                                            mutableStateOf(viewModel.eyeToBowDistance)
                                        },
                                        50f,
                                        100f,
                                        steps = 50
                                    ) { viewModel.eyeToBowDistance=it }
                                    SliderWithTextField(
                                        stringResource(R.string.eye_to_axis_distance),

                                        remember {
                                            mutableStateOf(viewModel.eyeToAxisDistance)
                                        },
                                        -40f,
                                        120f,
                                        steps = 160
                                    ) { viewModel.eyeToAxisDistance=it }
                                    SliderWithTextField(
                                        stringResource(R.string.shot_door_width),

                                        remember {
                                            mutableStateOf(viewModel.shotDoorWidth)
                                        },
                                        0f,
                                        0.1f,
                                        steps = 4
                                    ) { viewModel.shotDoorWidth=it }
                                    SliderWithTextField(
                                        stringResource(R.string.shot_head_width),

                                        remember {
                                            mutableStateOf(viewModel.shotHeadWidth)
                                        },
                                        0.02f,
                                        0.025f,
                                        steps = 2
                                    ) { viewModel.shotHeadWidth=it }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            PlotTrajectory(viewModel)
        }
    }
}




