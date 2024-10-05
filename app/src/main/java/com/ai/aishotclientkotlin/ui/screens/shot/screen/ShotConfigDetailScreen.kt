package com.ai.aishotclientkotlin.ui.screens.shot.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.domain.model.bi.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigBaseViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigViewModel
import com.ai.aishotclientkotlin.util.ui.custom.MoreSettingsWithLine
import com.ai.aishotclientkotlin.util.ui.custom.PelletClassOption
import com.ai.aishotclientkotlin.util.ui.custom.RadiusComboBox
import com.ai.aishotclientkotlin.util.ui.custom.SliderWithTextField

@Composable
fun ShotConfigDetailScreen(
    id: Int = -1, // -1 表示 新建；
    viewModel: ShotConfigBaseViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSave: (Int, ShotConfig) -> Unit // Int is -1 -> ADD; ELSE UPDATE;
) {

    // val viewModel: ShotConfigViewModel = hiltViewModel(key = id)
    Dialog(onDismissRequest = { onDismiss }) {


        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            val scrollState = rememberScrollState()
            Row {
                Button(onClick = {
                    if(id== -1)
                        onSave(-1,viewModel.getConfig())
                    else
                        viewModel.updateConfig()
                    onDismiss() // 关闭弹窗
                }) {
                    Text("保存")
                }
                Button(onClick = {
                    //  onSave() // 保存修改后的配置
                    onDismiss() // 关闭弹窗
                }) {
                    Text("取消")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            )
            {

                //!!TODO: change to ,need then show and modify it;
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .height(48.dp)
                       .align(Alignment.CenterHorizontally),
                    //    verticalAlignment = Alignment.CenterVertically
                )
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
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
                            mutableStateOf(viewModel.altitude.toFloat())
                        },
                        0.02f,
                        0.025f,
                        steps = 2,
                        showLength = 3,
                        modifier = Modifier.weight(1f),
                    ) { viewModel.altitude = it.toInt() }
                }
            }

        }



        //}
    }
}