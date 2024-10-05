package com.ai.aishotclientkotlin.ui.screens.shot.screen


import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import com.ai.aishotclientkotlin.util.ui.custom.FloatingInfoWindow
import com.ai.aishotclientkotlin.util.ui.custom.MoreSettingsWithLine
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
                if (viewModel.isShowCard) {
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

        if (hasCalPath) {
            val v_t = viewModel.getVelocityOfTargetObject()
            FloatingInfoWindow(
                viewModel.positionShotHead,
                viewModel.velocity,
                v_t.first,
                v_t.second
            )
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                            ) {
                                SliderWithTextField(
                                    modifier = Modifier.width(150.dp),
                                    label = stringResource(R.string.shot_distance),
                                    sliderValue = remember {
                                        mutableStateOf(viewModel.shotDistance)
                                    },
                                    rangeStart = 0f,
                                    rangeEnd = 100f,
                                    steps = 100
                                ) { viewModel.shotDistance = it }
                                SliderWithTextField(

                                    modifier = Modifier.width(150.dp),
                                    label = stringResource(R.string.launch_angle),

                                    sliderValue = remember {
                                        mutableStateOf(viewModel.objecttheta)
                                    },
                                    rangeStart = -90f,
                                    rangeEnd = 90f,
                                    steps = 180
                                ) { viewModel.objecttheta = (it) }


                                // More Settings

                                TextButton(onClick = {

                                    viewModel.toggleMoreSettings()
                                 //   ShotConfigGrid()
                                }) {
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
                        }
                    }
                }
            }
            if (viewModel.showMoreSettings) {
                ShotConfigGrid()
            }
            PlotTrajectory(viewModel)
        }
    }
}




