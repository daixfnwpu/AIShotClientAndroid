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
    // selectPoster: (MainScreenHomeTab, Long) -> Unit,
    //lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    // UI state variables
    var radius by remember { mutableStateOf(5f) }
    var velocity by remember { mutableStateOf(60f) }
    var angle by remember { mutableStateOf(45f) }
    var pellet by remember { mutableStateOf(PelletClass.MUD) }
    var eyeToBowDistance by remember { mutableStateOf(0.7f) }
    var eyeToAxisDistance by remember { mutableStateOf(0.06f) }
    var shotDoorWidth by remember { mutableStateOf(0.04f) }
    var shotDistance by remember { mutableStateOf(20f) }

    var isShowCard by remember {
        mutableStateOf(false)
    }
    var showMoreSettings by remember { mutableStateOf(false) } // 用来控制“更多设置”的显示
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = {
                isShowCard = !isShowCard
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
            AnimatedVisibility(visible = isShowCard) {
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
                                    mutableStateOf(shotDistance)
                                },
                                0f,
                                100f,
                                steps = 100
                            ) { shotDistance = it }
                            SliderWithTextField(
                                stringResource(R.string.launch_angle),

                                remember {
                                    mutableStateOf(angle)
                                },
                                -90f,
                                90f,
                                steps = 180
                            ) { angle = it }

                            //!!TODO: change to ,need then show and modify it;
//                            CircularInput(angle = remember {
//                                mutableStateOf(angle)
//                            })
                            PelletClassOption(selectedOption = remember {
                                mutableStateOf(pellet)
                            })

                            // More Settings

                            TextButton(onClick = { showMoreSettings = !showMoreSettings }) {
                                Row {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "more",
                                        tint = Color.Red,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        if (showMoreSettings) "隐藏更多设置" else "更多设置",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            AnimatedVisibility(visible = showMoreSettings) {
                                Column {

                                    RadiusComboBox(
                                        radius = remember { mutableStateOf(radius) },
                                        label = stringResource(R.string.radius),
                                        radiusOptions = listOf(6f, 7f, 8f, 9f, 10f, 11f, 12f)
                                    );
                                    // SliderInput(stringResource(R.string.radius), radius, 6f, 12f, step = 6) { velocity = it }

                                    SliderWithTextField(
                                        stringResource(R.string.velocity),

                                        remember {
                                            mutableStateOf(velocity)
                                        },
                                        40f,
                                        120f,
                                        steps = 80
                                    ) { velocity = it }

                                    SliderWithTextField(
                                        stringResource(R.string.eye_to_bow_distance),

                                        remember {
                                            mutableStateOf(eyeToBowDistance)
                                        },
                                        50f,
                                        100f,
                                        steps = 50
                                    ) { eyeToBowDistance = it }
                                    SliderWithTextField(
                                        stringResource(R.string.eye_to_axis_distance),

                                        remember {
                                            mutableStateOf(eyeToAxisDistance)
                                        },
                                        -40f,
                                        120f,
                                        steps = 160
                                    ) { eyeToAxisDistance = it }
                                    SliderWithTextField(
                                        stringResource(R.string.shot_door_width),

                                        remember {
                                            mutableStateOf(shotDoorWidth)
                                        },
                                        0f,
                                        0.1f,
                                        steps = 4
                                    ) { shotDoorWidth = it }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Canvas to plot the graph
            val destiny = if (pellet == PelletClass.MUD)
                            2.5f
                            else if(pellet == PelletClass.STEEL)
                                7.6f
                            else
                                2.5f
            PlotTrajectory(
//                radius = radius * 0.001f,
//                velocity = velocity,
//                angle = angle,
//                destiny =destiny,
//                shotDistance = shotDistance
            )

        }
    }
}




