package com.ai.aishotclientkotlin.ui.screens.settings.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.ui.screens.settings.model.SettingViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.screen.SliderWithTextField

@Composable
fun SettingScreen(
    navController: NavController?,
    viewModel: SettingViewModel = hiltViewModel(),
    // selectPoster: (MainScreenHomeTab, Long) -> Unit,
    //lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    var shotDistance by remember {
        mutableStateOf(20f)
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            SliderWithTextField(
                stringResource(R.string.shot_distance),
                remember {
                    mutableStateOf(shotDistance)
                } ,
                0f,
                100f,
                steps = 100
            ) { shotDistance = it }
           // SliderWithTextField()
        }
//        RadiusComboBox( remember {
//            mutableStateOf(radius)}, label = stringResource(id = R.string.radius))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiusComboBox(radius: MutableState<Float>, label: String, radiusOptions: List<Float> =listOf(0f, 2f, 4f, 6f, 8f, 10f) ) {
    // 预定义的半径值
    //val radiusOptions = listOf(0f, 2f, 4f, 6f, 8f, 10f)

    // 追踪下拉菜单是否展开
    var expanded by remember { mutableStateOf(false) }

    // 选中的值
    val selectedRadius = radius.value

    // 下拉菜单框
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // 显示当前选择的半径值

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
        ) {
            // Label Text
            Text(
                text = label,
                modifier = Modifier.padding(end = 8.dp) // 给 label 一些间距
            )

            // TextField for input
            TextField(
                value = selectedRadius.toString(),
                readOnly = true,
                onValueChange = {
                },
                modifier = Modifier
                    .weight(1f)
                    .menuAnchor()
                    .clickable { expanded = true } // TextField 占用剩余空间
            )
         //   Spacer(modifier = Modifier.weight(1.0f))
            Text(
                text = stringResource(id = R.string.click_and_modify),
                modifier = Modifier.padding(end = 8.dp) // 给 label 一些间距
            )
        }

            // 下拉菜单内容
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },

                ) {
                radiusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toString()) },
                        onClick = {
                            radius.value = option  // 更新选中的值
                            expanded = false  // 关闭下拉菜单
                        }
                    )
                }
            }
        }
}

@Preview
@Composable
fun PreviewMainScreen() {
    SettingScreen(navController = null)
}