package com.ai.aishotclientkotlin.util.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel


@Composable
fun SliderWithTextField(

    label: String,
    sliderValue: MutableState<Float>,
    rangeStart: Float,
    rangeEnd: Float,

    steps: Int = 0,
    showLength: Int = 1,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit
) {

    var textFieldValue by remember { mutableStateOf("%.${showLength}f".format(sliderValue.value)) }
    var showSlider by remember { mutableStateOf(false) } // State to show or hide slider
    var iconPosition by remember { mutableStateOf(Offset.Zero) }
    var iconSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    Surface(
        modifier = modifier
            .padding(0.dp)
            //.width(150.dp)
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(48.dp)
                .padding(start = 2.dp)
        ) {
            // TextField for keyboard input
            Text(
                text = label,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(end = 2.dp) // 给 label 一些间距
                    .weight(2f)
            )
            TextField(
                value = textFieldValue,
                onValueChange = { newText ->
                    val intValue = newText.toFloatOrNull() // Try to parse the input as an integer
                    if (intValue != null && intValue in rangeStart..rangeEnd) {
                        sliderValue.value = intValue
                        textFieldValue = String.format("%.${showLength}f",newText)
                        onValueChange(intValue)
                    } else {
                        textFieldValue = String.format("%.${showLength}f",newText) // Keep invalid input
                    }
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(3f),
                singleLine = true,
                textStyle = TextStyle(fontSize = 12.sp),
                trailingIcon = {
                    // Add an Icon at the end of the TextField
                    Icon(
                        imageVector = Icons.Default.MoreVert,  // Use any icon you'd like
                        contentDescription = "Show Slider",
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    showSlider = !showSlider
                                }
                            }
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInRoot()
                                val size = coordinates.size
                                iconPosition = position
                                iconSize = size
                            }, // Toggle slider visibility on click
                        tint = Color.Gray
                    )
                }
            )

        }

        if (showSlider) {
            val popupOffset = with(density) {
                IntOffset(
                    x = 0,
                    y = iconSize.height
                )
            }
            Popup(
                alignment = Alignment.BottomCenter, // 设置 Popup 显示位置
                offset = popupOffset, // 设置Popup在点击位置
                onDismissRequest = { showSlider = false },
                properties = PopupProperties(focusable = true) // 确保 Popup 可聚焦
            ) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        //   .fillMaxWidth()
                        .size(500.dp, 50.dp)
                        .background(Color.Transparent)
                        .zIndex(10f)
                ) {
                    Slider(
                        value = sliderValue.value,
                        onValueChange = { newValue ->
                            // onValueChange
                            textFieldValue = String.format("%.1f",newValue)
                            sliderValue.value = String.format("%.1f",newValue).toFloat()
                            onValueChange(newValue)
                        },
                        valueRange = rangeStart..rangeEnd,
                        modifier = Modifier.fillMaxSize(),//.rotate(270f),
                        steps = steps
                    )
                }
            }
        }
    }

}



@Composable
fun SliderInput(
    label: String,
    value: Float,
    rangeStart: Float,
    rangeEnd: Float,
    step: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Row(modifier = Modifier.height(24.dp)) {
        Text(text = "$label: ${"%.2f".format(value)}",fontSize = 12.sp)
        Slider(value = value, onValueChange = onValueChange, valueRange = rangeStart..rangeEnd)
    }
}

enum class PelletClass {
    STEEL, MUD
}

@Composable
fun PelletClassOption(viewModel: ShotViewModel,modifier: Modifier= Modifier) {
    // var selectedOption by remember { mutableStateOf("Option 1") }

    Row(modifier = modifier,verticalAlignment = Alignment.CenterVertically,) {
        Text(text = stringResource(id = R.string.pelletclass),fontSize = 12.sp,modifier= Modifier.background(Color.White))

        // 单选按钮组
        Row(verticalAlignment = Alignment.CenterVertically,) {
            RadioButton(
                selected = viewModel.pellet == PelletClass.STEEL,
                onClick = { viewModel.pellet = PelletClass.STEEL }
            )
            Text(
                text = stringResource(id = R.string.pelletsteel),
                fontSize = 12.sp,
                modifier = Modifier.clickable { viewModel.pellet = PelletClass.STEEL })
        }

        Row(verticalAlignment = Alignment.CenterVertically,) {
            RadioButton(
                selected = viewModel.pellet == PelletClass.MUD,
                onClick = { viewModel.pellet = PelletClass.MUD }
            )
            Text(
                text = stringResource(id = R.string.pelletmud),
                fontSize = 12.sp,
                modifier = Modifier.clickable { viewModel.pellet = PelletClass.MUD })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiusComboBox(
    viewModel: ShotViewModel,
    label: String,
    radiusOptions: List<Float> = listOf(6f, 7f, 8f, 9f, 10f, 11f, 12f),
    modifier: Modifier= Modifier
) {

    // 追踪下拉菜单是否展开
    var expanded by remember { mutableStateOf(false) }

    // 选中的值
    val selectedRadius = viewModel.radius

    // 下拉菜单框
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        // 显示当前选择的半径值

        Row(
            modifier = Modifier.padding(0.dp),
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
        ) {
            // Label Text
            Text(
                text = label,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 2.dp).weight(2f).background(Color.White) // 给 label 一些间距
            )

            // TextField for input
            TextField(
                value = String.format("%.1f", viewModel.radius),//radius.value.toString(),
                readOnly = true,
                onValueChange = {
                },
                textStyle = TextStyle(fontSize = 12.sp),
                modifier = Modifier
                    //       .height(24.dp)
                    .menuAnchor()
                    .weight(3f)
                    .padding(start = 0.dp)
                    .clickable { expanded = true } // TextField 占用剩余空间
            )
            //   Spacer(modifier = Modifier.weight(1.0f))
        }

        // 下拉菜单内容
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },

            ) {
            radiusOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString(),fontSize = 12.sp) },
                    onClick = {
                        viewModel.radius = option  // 更新选中的值
                        expanded = false  // 关闭下拉菜单
                    }
                )
            }
        }
    }
}

//
//@Preview
//@Composable
//fun PreviewMainScreen() {
//    ShotScreen(navController = null)
//}
@Composable
fun RadiusInputFieldWithLabel(
    label: String,
    radius: MutableState<Float>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
            value = radius.value.toString(),
            onValueChange = { newValue ->
                radius.value = newValue.toFloatOrNull() ?: 0f
            },
            modifier = Modifier
                .weight(1f) // TextField 占用剩余空间
        )
    }
}


@Composable
fun ModifyRadius(radiusState: MutableState<Float>) {
    // 显示当前的 radius
    Text(text = "Current Radius: ${radiusState.value}",fontSize = 12.sp)

    // 提供一个按钮来增加 radius 的值
    Button(onClick = { radiusState.value += 1f }) {
        Text(text = "Increase Radius",fontSize = 12.sp,)
    }
}

@Composable
fun RadiusSlider() {
    var radius by remember { mutableStateOf(5f) }

    Column(modifier = Modifier.padding(16.dp)) {
        // 将 radius 的状态传递给 ModifyRadius 函数
        ModifyRadius(radiusState = remember { mutableStateOf(radius) })

        Slider(
            value = radius,
            onValueChange = { radius = it },
            valueRange = 0f..10f,
            steps = 9
        )
    }
}


@Composable
fun FloatingInfoWindow(positionOfHead: Float,velocity:Float,headVelocity:Float,flyTime: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .width(100.dp) // 设置宽度
                .height(100.dp) // 设置高度
                .align(Alignment.TopEnd) // 位置在右边中间
                .padding(end = 2.dp) // 设置距离屏幕右边的边距
                .background(
                    color = Color.Gray.copy(alpha = 0.1f), // 设置半透明背景
                    shape = RoundedCornerShape(8.dp) // 可选，设置圆角
                )
        ) {
            // 在此处添加需要显示的信息
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 2.dp)
                    .wrapContentWidth()
                    )
            {

                Text(
                    text =String.format("瞄点   %.1f",positionOfHead*1000),
                    modifier = Modifier
                        .wrapContentWidth(),
                    color = Color.Blue

                )
                Text(
                    text = String.format("初速度   %.1f",velocity),
                    modifier = Modifier
                        .wrapContentWidth(),
                    color = Color.Blue
                )
                Text(
                    text = String.format("击中速度%.1f",headVelocity),
                    modifier = Modifier
                        .wrapContentWidth(),
                    color = Color.Blue
                )

                Text(
                    text = String.format("飞行时间%.1f",flyTime),
                    modifier = Modifier
                        .wrapContentWidth(),
                    color = Color.Blue
                )
            }
        }
    }
}