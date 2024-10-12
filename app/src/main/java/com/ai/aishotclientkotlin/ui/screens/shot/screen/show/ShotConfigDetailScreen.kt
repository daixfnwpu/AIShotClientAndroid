package com.ai.aishotclientkotlin.ui.screens.shot.screen.show

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.screens.shot.model.show.ShotConfigBaseViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.show.ShotConfigViewModel
import com.ai.aishotclientkotlin.util.ui.custom.PelletClassOption
import com.ai.aishotclientkotlin.util.ui.custom.RadiusComboBox
import com.ai.aishotclientkotlin.util.ui.custom.SliderWithTextField

@Composable
fun ShotConfigDetailScreen(
    id: Long, // -1 表示 新建；
    onDismiss: () -> Unit,
    viewModel: ShotConfigViewModel = hiltViewModel(),
    readonly : Boolean = false
) {
        var detailsViewModel : ShotConfigBaseViewModel = hiltViewModel()
        val isLoading by detailsViewModel.isLoading
        if(id != -1L) //编辑或者查看
        {
            detailsViewModel.loadItemDetails(id, onComplete = {
            })
            if (isLoading) {
                // 如果正在加载，显示加载动画或占位符
                CircularProgressIndicator()
            } else {
                ShotConfigCard(id= id,viewModel = detailsViewModel,onDismiss = onDismiss,onSave = {shotConfig ->
                    //   viewModel.addRow(shotConfig)
                    detailsViewModel.updateConfig()
                },readonly = readonly)
            }

        }else // 新建：
        {
            detailsViewModel.createNewConfig()
            ShotConfigCard(id= id,viewModel = detailsViewModel,onDismiss = onDismiss,onSave = { shotConfig ->
                viewModel.addRow(shotConfig)
                detailsViewModel.saveConfig()
            },readonly = readonly)
        }

}

@Composable
fun ShotConfigCard(id: Long, // -1 表示 新建；
                   viewModel: ShotConfigBaseViewModel,
                   onDismiss: () -> Unit,
                   onSave:  (ShotConfig) -> Unit,// Int is -1 -> ADD; ELSE UPDATE;
                   readonly: Boolean
                    ) {
    val config by viewModel.configDetail.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    if (!readonly) {
                        onSave(config!!)
                    }
                    onDismiss()
                }) {
                    if (!readonly) Text("保存") else Text("关闭")
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = { onDismiss() }) {
                    Text("取消")
                }
            }

            // Fields start here
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                RadiusComboBox(
                    config = config,
                    label = stringResource(R.string.radius),
                    radiusOptions = listOf(6f, 7f, 8f, 9f, 10f, 11f, 12f),
                    modifier = Modifier.weight(1f)
                )
                PelletClassOption(config, modifier = Modifier.weight(1f))
            }

            SliderWithTextField(
                stringResource(R.string.velocity),
                remember { mutableFloatStateOf(config!!.initvelocity) },
                40f, 120f, steps = 80, modifier = Modifier.weight(1f)
            ) { config!!.initvelocity = it }

            SliderWithTextField(
                stringResource(R.string.eye_to_bow_distance),
                remember { mutableFloatStateOf(config!!.eyeToBowDistance) },
                0.50f, 1f, steps = 50, showLength = 2, modifier = Modifier.weight(1f)
            ) { config!!.eyeToBowDistance = it }

            SliderWithTextField(
                stringResource(R.string.eye_to_axis_distance),
                remember { mutableFloatStateOf(config!!.eyeToAxisDistance) },
                -0.040f, 0.120f, steps = 160, showLength = 3, modifier = Modifier.weight(1f)
            ) { config!!.eyeToAxisDistance = it }

            SliderWithTextField(
                stringResource(R.string.shot_door_width),
                remember { mutableFloatStateOf(config!!.shotDoorWidth) },
                0.04f, 0.06f, steps = 4, showLength = 2, modifier = Modifier.weight(1f)
            ) { config!!.shotDoorWidth = it }

            SliderWithTextField(
                stringResource(R.string.shot_head_width),
                remember { mutableFloatStateOf(config!!.shotHeadWidth) },
                0.02f, 0.025f, steps = 2, showLength = 3, modifier = Modifier.weight(1f)
            ) { config!!.shotHeadWidth = it }

            SliderWithTextField(
                stringResource(R.string.altitude),
                remember { mutableFloatStateOf(config!!.altitude.toFloat()) },
                0f, 5000f, steps = 50, showLength = 0, modifier = Modifier.weight(1f)
            ) { config!!.altitude = it.toInt() }

            SliderWithTextField(
                stringResource(R.string.thickness_of_rubber),
                remember { mutableFloatStateOf(config!!.thinofrubber_mm) },
                0.3f, 1f, steps = 70, showLength = 2, modifier = Modifier.weight(1f)
            ) { config!!.thinofrubber_mm = it }

            SliderWithTextField(
                stringResource(R.string.init_length_of_rubber),
                remember { mutableFloatStateOf(config!!.initlengthofrubber_m) },
                0.1f, 0.5f, steps = 40, showLength = 2, modifier = Modifier.weight(1f)
            ) { config!!.initlengthofrubber_m = it }

            SliderWithTextField(
                stringResource(R.string.width_of_rubber),
                remember { mutableFloatStateOf(config!!.widthofrubber_mm.toFloat()) },
                10f, 30f, steps = 20, showLength = 0, modifier = Modifier.weight(1f)
            ) { config!!.widthofrubber_mm = it.toInt() }

            SliderWithTextField(
                stringResource(R.string.humidity),
                remember { mutableFloatStateOf(config!!.humidity.toFloat()) },
                0f, 100f, steps = 100, showLength = 0, modifier = Modifier.weight(1f)
            ) { config!!.humidity = it.toInt() }

            SliderWithTextField(
                stringResource(R.string.cross_of_rubber),
                remember { mutableFloatStateOf(config!!.crossofrubber) },
                -10f, 10f, steps = 20, showLength = 2, modifier = Modifier.weight(1f)
            ) { config!!.crossofrubber = it }

            SliderWithTextField(
                stringResource(R.string.air_density),
                remember { mutableFloatStateOf(config!!.airrho) },
                0.5f, 1.5f, steps = 10, showLength = 3, modifier = Modifier.weight(1f)
            ) { config!!.airrho = it }

            SliderWithTextField(
                stringResource(R.string.drag_coefficient),
                remember { mutableFloatStateOf(config!!.Cd) },
                0.1f, 1f, steps = 9, showLength = 2, modifier = Modifier.weight(1f)
            ) { config!!.Cd = it }
        }
    }

}
