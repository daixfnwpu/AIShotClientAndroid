package com.ai.aishotclientkotlin.ui.screens.shot.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.ai.aishotclientkotlin.util.ui.custom.FloatingInfoWindow
import com.ai.aishotclientkotlin.util.ui.custom.MoreSettingsWithLine
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
    var hasCalPath by remember { mutableStateOf<Boolean>(false) }
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = {
             //   isShowCard = !isShowCard
                if(viewModel.isShowCard) {
                    viewModel.updatePositionsAndObjectPosition()
                    hasCalPath = true
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

        if(hasCalPath)
        {
            val v_t = viewModel.getVelocityOfTargetObject()
            FloatingInfoWindow(viewModel.positionShotHead,viewModel.velocity,v_t.first,v_t.second)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                //  .height(24.dp)
                .padding(2.dp)
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
                                .padding(2.dp)
                        )
                        {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)) {
                                SliderWithTextField(
                                    modifier = Modifier.width(150.dp),
                                    label = stringResource(R.string.shot_distance),
                                    sliderValue = remember {
                                        mutableStateOf(viewModel.shotDistance)
                                    },
                                    rangeStart = 0f,
                                    rangeEnd = 100f,
                                    steps = 100
                                ) { viewModel.shotDistance= it }
                                SliderWithTextField(

                                    modifier = Modifier.width(150.dp),
                                    label = stringResource(R.string.launch_angle),

                                    sliderValue = remember {
                                        mutableStateOf(viewModel.angle)
                                    },
                                    rangeStart = -90f,
                                    rangeEnd = 90f,
                                    steps = 180
                                ) { viewModel.angle = (it) }



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
                                            if (viewModel.showMoreSettings) "隐藏" else "更多",
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                            MoreSettingsWithLine()
                            AnimatedVisibility(visible = viewModel.showMoreSettings) {
                                Column {
                                    //!!TODO: change to ,need then show and modify it;
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp).align(Alignment.CenterHorizontally)
                                        , verticalAlignment = Alignment.CenterVertically)
                                    {
                                        RadiusComboBox(
                                            viewModel = viewModel,
                                            label = stringResource(R.string.radius),
                                            radiusOptions = listOf(6f, 7f, 8f, 9f, 10f, 11f, 12f),
                                            modifier = Modifier.weight(1f)
                                        );
                                        // SliderInput(stringResource(R.string.radius), radius, 6f, 12f, step = 6) { velocity = it }
                                        PelletClassOption(viewModel, modifier = Modifier.weight(1f))
                                    }
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp))
                                    {
                                        SliderWithTextField(
                                            stringResource(R.string.velocity),

                                            remember {
                                                mutableStateOf(viewModel.velocity)
                                            },
                                            40f,
                                            120f,
                                            steps = 80,
                                            modifier = Modifier.weight(1f),
                                        ) { viewModel.velocity = it }

                                        SliderWithTextField(
                                            stringResource(R.string.eye_to_bow_distance),

                                            remember {
                                                mutableStateOf(viewModel.eyeToBowDistance)
                                            },
                                            0.50f,
                                            1f,
                                            steps = 50,
                                            showLength = 2,
                                            modifier = Modifier.weight(1f),
                                        ) { viewModel.eyeToBowDistance = it }
                                    }
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp))
                                    {
                                        SliderWithTextField(
                                            stringResource(R.string.eye_to_axis_distance),

                                            remember {
                                                mutableStateOf(viewModel.eyeToAxisDistance)
                                            },
                                            -0.040f,
                                            0.120f,
                                            steps = 160,
                                            showLength = 3,

                                            modifier = Modifier.weight(1f),
                                        ) { viewModel.eyeToAxisDistance = it }
                                        SliderWithTextField(
                                            stringResource(R.string.shot_door_width),

                                            remember {
                                                mutableStateOf(viewModel.shotDoorWidth)
                                            },
                                            0.04f,
                                            0.06f,
                                            steps = 4,
                                            showLength = 2,

                                            modifier = Modifier.weight(1f),
                                        ) { viewModel.shotDoorWidth = it }
                                    }
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp))
                                    {
                                        SliderWithTextField(
                                            stringResource(R.string.shot_head_width),

                                            remember {
                                                mutableStateOf(viewModel.shotHeadWidth)
                                            },
                                            0.02f,
                                            0.025f,
                                            steps = 2,
                                            showLength = 3,
                                            modifier = Modifier.weight(1f),
                                        ) { viewModel.shotHeadWidth = it }
                                        SliderWithTextField(
                                            stringResource(R.string.altitude),

                                            remember {
                                                mutableStateOf(viewModel.altitude)
                                            },
                                            0.02f,
                                            0.025f,
                                            steps = 2,
                                            showLength = 3,
                                            modifier = Modifier.weight(1f),
                                        ) { viewModel.altitude = it }
                                    }
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




