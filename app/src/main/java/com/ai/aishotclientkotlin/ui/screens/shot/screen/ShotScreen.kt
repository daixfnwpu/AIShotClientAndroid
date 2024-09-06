package com.ai.aishotclientkotlin.ui.screens.shot.screen


import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithMenu
import kotlin.math.PI
import kotlin.math.atan2
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
                text = stringResource(id = R.string.add_entry),fontSize = 16.sp
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
                                } ,
                                0f,
                                100f,
                                step = 100
                            ) { shotDistance = it }
                            SliderWithTextField(
                                stringResource(R.string.launch_angle),

                                remember {
                                    mutableStateOf(  angle)
                                } ,
                                -90f,
                                90f,
                                step = 180
                            ) { angle = it }

                            //!!TODO: change to ,need then show and modify it;
                            CircularInput(angle = remember {
                                mutableStateOf(angle)
                            })
                            PelletClassOption(selectedOption = remember {
                                mutableStateOf(pellet)
                            })

                            // More Settings

                            TextButton(onClick = { showMoreSettings = !showMoreSettings }) {
                                Text(if (showMoreSettings) "隐藏更多设置" else "更多设置",fontSize = 16.sp)
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
                                            mutableStateOf(  velocity)
                                        } ,
                                        40f,
                                        120f,
                                        step = 80
                                    ) { velocity = it }

                                    SliderWithTextField(
                                        stringResource(R.string.eye_to_bow_distance),

                                        remember {
                                            mutableStateOf(  eyeToBowDistance)
                                        } ,
                                        50f,
                                        100f,
                                        step = 50
                                    ) { eyeToBowDistance = it }
                                    SliderWithTextField(
                                        stringResource(R.string.eye_to_axis_distance),

                                        remember {
                                            mutableStateOf(  eyeToAxisDistance)
                                        } ,
                                        -40f,
                                        120f,
                                        step = 160
                                    ) { eyeToAxisDistance = it }
                                    SliderWithTextField(
                                        stringResource(R.string.shot_door_width),

                                        remember {
                                            mutableStateOf(  shotDoorWidth)
                                        } ,
                                        0f,
                                        0.1f,
                                        step = 4
                                    ) { shotDoorWidth = it }
                                }
                            }
                        }
                    }
                }
            }
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
}


@Composable
fun SliderWithTextField(
    label: String,
    sliderValue: MutableState<Float>,
    rangeStart: Float,
    rangeEnd: Float,
    step: Int = 0,
    onValueChange: (Float) -> Unit

) {
  //  var sliderValue by remember { mutableStateOf(50f) }
 //   var textFieldValue by remember { sliderValue.toString() }
    var textFieldValue by remember { mutableStateOf(sliderValue.value.toInt().toString()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .height(24.dp)
            .padding(16.dp)
    ) {
        // TextField for keyboard input
        Text(
            text = label,
            modifier = Modifier.padding(end = 8.dp) // 给 label 一些间距
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = textFieldValue,
            onValueChange = { newText ->
                val intValue = newText.toIntOrNull() // Try to parse the input as an integer
                if (intValue != null && intValue in 0..100) {
                    sliderValue.value = intValue.toFloat()
                    textFieldValue = newText
                } else {
                    textFieldValue = newText // Keep invalid input
                }
            },
            modifier = Modifier.width(128.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Slider for sliding input
        Slider(
            value = sliderValue.value,
            onValueChange = { newValue ->
                onValueChange
                textFieldValue = newValue.toInt().toString()
            },
            valueRange = rangeStart..rangeEnd,
            modifier = Modifier.fillMaxWidth(),
            steps = step
        )
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
        Text(text = "$label: ${"%.2f".format(value)}",fontSize = 16.sp)
        Slider(value = value, onValueChange = onValueChange, valueRange = rangeStart..rangeEnd)
    }
}

enum class PelletClass {
    STEEL, MUD
}

@Composable
fun PelletClassOption(selectedOption: MutableState<PelletClass>) {
    // var selectedOption by remember { mutableStateOf("Option 1") }

    Row(modifier = Modifier.height(24.dp)) {
        Text(text = stringResource(id = R.string.pelletclass),fontSize = 16.sp)

        // 单选按钮组
        Row {
            RadioButton(
                selected = selectedOption.value == PelletClass.STEEL,
                onClick = { selectedOption.value = PelletClass.STEEL }

            )
            Text(
                text = stringResource(id = R.string.pelletsteel),
                fontSize = 16.sp,
                modifier = Modifier.clickable { selectedOption.value == PelletClass.STEEL })
        }

        Row {
            RadioButton(
                selected = selectedOption.value == PelletClass.MUD,
                onClick = { selectedOption.value = PelletClass.MUD }
            )
            Text(
                text = stringResource(id = R.string.pelletmud),
                fontSize = 16.sp,
                modifier = Modifier.clickable { selectedOption.value == PelletClass.MUD })
        }

        //   Text(text = "Selected: $selectedOption")
    }
}

@Composable
fun CircularInput(angle: MutableState<Float>) {
    // var angle by remember { mutableStateOf(0f) }  // 保存角度状态
    val radius = 100f  // 圆盘半径

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Selected Angle: ${angle.value.toInt()}°",fontSize = 16.sp,)

        Spacer(modifier = Modifier.height(16.dp))

        // Canvas 用来绘制圆盘和指针
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val touchX = change.position.x
                        val touchY = change.position.y

                        // 根据触摸位置计算角度
                        val deltaX = touchX - centerX
                        val deltaY = touchY - centerY
                        val radians = atan2(deltaY, deltaX)
                        angle.value = Math
                            .toDegrees(radians.toDouble())
                            .toFloat()

                        // 处理角度在负数范围时，将其转为正角度
                        if (angle.value < 0) {
                            angle.value += 360
                        }
                    }
                }
        ) {
            // 绘制圆盘
            drawCircle(
                color = Color.Gray,
                radius = radius
            )

            // 计算指针位置
            val pointerX = radius * cos(angle.value * PI / 180).toFloat() + size.width / 2
            val pointerY = radius * sin(angle.value * PI / 180).toFloat() + size.height / 2

            // 绘制指针
            drawLine(
                color = Color.Red,
                start = Offset(size.width / 2, size.height / 2),
                end = Offset(pointerX, pointerY),
                strokeWidth = 4f
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiusComboBox(
    radius: MutableState<Float>,
    label: String,
    radiusOptions: List<Float> = listOf(0f, 2f, 4f, 6f, 8f, 10f)
) {
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
                .fillMaxWidth()
                .height(24.dp),
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
        ) {
            // Label Text
            Text(
                text = label,
                fontSize = 16.sp,
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
                    .height(24.dp)
                    .menuAnchor()
                    .clickable { expanded = true } // TextField 占用剩余空间
            )
            //   Spacer(modifier = Modifier.weight(1.0f))
            Text(
                text = stringResource(id = R.string.click_and_modify),
                fontSize = 16.sp,
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
                    text = { Text(option.toString(),fontSize = 16.sp) },
                    onClick = {
                        radius.value = option  // 更新选中的值
                        expanded = false  // 关闭下拉菜单
                    }
                )
            }
        }
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
    Text(text = "Current Radius: ${radiusState.value}",fontSize = 16.sp)

    // 提供一个按钮来增加 radius 的值
    Button(onClick = { radiusState.value += 1f }) {
        Text(text = "Increase Radius",fontSize = 16.sp,)
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