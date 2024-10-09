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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.ai.aishotclientkotlin.data.dao.entity.ShotConfig
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigBaseViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotConfigViewModel
import com.ai.aishotclientkotlin.util.ui.custom.MoreSettingsWithLine
import com.ai.aishotclientkotlin.util.ui.custom.PelletClassOption
import com.ai.aishotclientkotlin.util.ui.custom.RadiusComboBox
import com.ai.aishotclientkotlin.util.ui.custom.SliderWithTextField

@Composable
fun ShotConfigDetailScreen(
    id: Long = -1, // -1 表示 新建；
    viewModel: ShotConfigBaseViewModel,
    onDismiss: () -> Unit,
    onSave: (Long, ShotConfig) -> Unit ,// Int is -1 -> ADD; ELSE UPDATE;
    readonly: Boolean = false
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        ShotConfigCard(id= id,viewModel = viewModel,onDismiss = onDismiss,onSave = onSave,readonly = readonly)
    }
}

@Composable
fun ShotConfigCard(id: Long , // -1 表示 新建；
                   viewModel: ShotConfigBaseViewModel,
                   onDismiss: () -> Unit,
                   onSave: (Long, ShotConfig) -> Unit ,// Int is -1 -> ADD; ELSE UPDATE;
                   readonly: Boolean
                    ) {
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
            Row {
                Button(onClick = {
                    if (!readonly) {
                        if (id == -1L)
                            onSave(-1L, viewModel.getConfig())
                        else
                            viewModel.updateConfig()
                    }
                    onDismiss()
                }) {
                    if (!readonly) Text("保存") else Text("关闭")
                }
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
                    viewModel = viewModel,
                    label = stringResource(R.string.radius),
                    radiusOptions = listOf(6f, 7f, 8f, 9f, 10f, 11f, 12f),
                    modifier = Modifier.weight(1f)
                )
                PelletClassOption(viewModel, modifier = Modifier.weight(1f))
            }

            SliderWithTextField(
                stringResource(R.string.velocity),
                remember { mutableFloatStateOf(viewModel.velocity) },
                40f, 120f, steps = 80, modifier = Modifier.weight(1f)
            ) { viewModel.velocity = it }

            SliderWithTextField(
                stringResource(R.string.eye_to_bow_distance),
                remember { mutableFloatStateOf(viewModel.eyeToBowDistance) },
                0.50f, 1f, steps = 50, showLength = 2, modifier = Modifier.weight(1f)
            ) { viewModel.eyeToBowDistance = it }

            SliderWithTextField(
                stringResource(R.string.eye_to_axis_distance),
                remember { mutableFloatStateOf(viewModel.eyeToAxisDistance) },
                -0.040f, 0.120f, steps = 160, showLength = 3, modifier = Modifier.weight(1f)
            ) { viewModel.eyeToAxisDistance = it }

            SliderWithTextField(
                stringResource(R.string.shot_door_width),
                remember { mutableFloatStateOf(viewModel.shotDoorWidth) },
                0.04f, 0.06f, steps = 4, showLength = 2, modifier = Modifier.weight(1f)
            ) { viewModel.shotDoorWidth = it }

            SliderWithTextField(
                stringResource(R.string.shot_head_width),
                remember { mutableFloatStateOf(viewModel.shotHeadWidth) },
                0.02f, 0.025f, steps = 2, showLength = 3, modifier = Modifier.weight(1f)
            ) { viewModel.shotHeadWidth = it }

            SliderWithTextField(
                stringResource(R.string.altitude),
                remember { mutableFloatStateOf(viewModel.altitude.toFloat()) },
                0f, 5000f, steps = 50, showLength = 0, modifier = Modifier.weight(1f)
            ) { viewModel.altitude = it.toInt() }

            SliderWithTextField(
                stringResource(R.string.thickness_of_rubber),
                remember { mutableFloatStateOf(viewModel.thinofrubber_mm) },
                0.3f, 1f, steps = 70, showLength = 2, modifier = Modifier.weight(1f)
            ) { viewModel.thinofrubber_mm = it }

            SliderWithTextField(
                stringResource(R.string.init_length_of_rubber),
                remember { mutableFloatStateOf(viewModel.initlengthofrubber_m) },
                0.1f, 0.5f, steps = 40, showLength = 2, modifier = Modifier.weight(1f)
            ) { viewModel.initlengthofrubber_m = it }

            SliderWithTextField(
                stringResource(R.string.width_of_rubber),
                remember { mutableFloatStateOf(viewModel.widthofrubber_mm.toFloat()) },
                10f, 30f, steps = 20, showLength = 0, modifier = Modifier.weight(1f)
            ) { viewModel.widthofrubber_mm = it.toInt() }

            SliderWithTextField(
                stringResource(R.string.humidity),
                remember { mutableFloatStateOf(viewModel.humidity.toFloat()) },
                0f, 100f, steps = 100, showLength = 0, modifier = Modifier.weight(1f)
            ) { viewModel.humidity = it.toInt() }

            SliderWithTextField(
                stringResource(R.string.cross_of_rubber),
                remember { mutableFloatStateOf(viewModel.crossofrubber) },
                -10f, 10f, steps = 20, showLength = 2, modifier = Modifier.weight(1f)
            ) { viewModel.crossofrubber = it }

            SliderWithTextField(
                stringResource(R.string.air_density),
                remember { mutableFloatStateOf(viewModel.airrho) },
                0.5f, 1.5f, steps = 10, showLength = 3, modifier = Modifier.weight(1f)
            ) { viewModel.airrho = it }

            SliderWithTextField(
                stringResource(R.string.drag_coefficient),
                remember { mutableFloatStateOf(viewModel.Cd) },
                0.1f, 1f, steps = 9, showLength = 2, modifier = Modifier.weight(1f)
            ) { viewModel.Cd = it }
        }
    }

}
