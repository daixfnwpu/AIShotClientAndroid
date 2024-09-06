package com.ai.aishotclientkotlin.ui.screens.shot.screen


import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
    var radius  by remember { mutableStateOf(5f) }
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
                text = stringResource(id = R.string.add_entry),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AnimatedVisibility(visible = isShowCard) {
                Card(
                    onClick = { /* Do something */ },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        {
                            RadiusComboBox(radius= remember { mutableStateOf(radius) },label=stringResource(R.string.radius),radiusOptions = listOf(6f,7f, 8f, 9f,10f,11f,12f));
                           // SliderInput(stringResource(R.string.radius), radius, 6f, 12f, step = 6) { velocity = it }
                            PelletClassOption(selectedOption= remember {
                                mutableStateOf(pellet)
                            })
                            SliderInput(stringResource(R.string.velocity), velocity, 40f, 120f, step = 80) { velocity = it }
                            SliderInput(stringResource(R.string.launch_angle), angle, -90f, 90f,step=180) { angle = it }

                            CircularInput(angle = remember {
                                mutableStateOf(angle)
                            })
                            SliderInput(
                                stringResource(R.string.eye_to_bow_distance),
                                eyeToBowDistance,
                                50f,
                                100f,
                                step = 50
                            ) { eyeToBowDistance = it }
                            SliderInput(
                                stringResource(R.string.eye_to_axis_distance),
                                eyeToAxisDistance,
                                -40f,
                                120f,
                                step = 160
                            ) { eyeToAxisDistance = it }
                            SliderInput(
                                stringResource(R.string.shot_door_width),
                                shotDoorWidth,
                                0f,
                                0.1f,
                                step = 4
                            ) { shotDoorWidth = it }
                            SliderInput(
                                stringResource(R.string.shot_distance),
                                shotDistance,
                                0f,
                                100f,
                                step = 100
                            ) { shotDistance = it }
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
fun SliderInput(
    label: String,
    value: Float,
    rangeStart: Float,
    rangeEnd: Float,
    step: Int  =0,
    onValueChange: (Float) -> Unit
) {
    Row {
        Text(text = "$label: ${"%.2f".format(value)}")
        Slider(value = value, onValueChange = onValueChange, valueRange = rangeStart..rangeEnd)
    }
}
enum class PelletClass{
    STEEL,MUD
}
@Composable
fun PelletClassOption(selectedOption:MutableState<PelletClass> ) {
   // var selectedOption by remember { mutableStateOf("Option 1") }

    Column {
        Text(text = stringResource(id = R.string.pelletclass))

        // 单选按钮组
        Row {
            RadioButton(
                selected = selectedOption.value ==PelletClass.STEEL ,
                onClick = { selectedOption.value = PelletClass.STEEL }
            )
            Text(text = stringResource(id = R.string.pelletsteel), modifier = Modifier.clickable { selectedOption.value == PelletClass.STEEL })
        }

        Row {
            RadioButton(
                selected = selectedOption.value == PelletClass.MUD,
                onClick = { selectedOption.value = PelletClass.MUD }
            )
            Text(text = stringResource(id = R.string.pelletmud), modifier = Modifier.clickable { selectedOption.value == PelletClass.MUD })
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
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Selected Angle: ${angle.value.toInt()}°")

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


//TODO!!!! : expanded can not expand
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiusComboBox(radius: MutableState<Float>,label: String ,radiusOptions: List<Float> =listOf(0f, 2f, 4f, 6f, 8f, 10f) ) {
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
        TextField(
            value = selectedRadius.toString(),
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        // 下拉菜单内容
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
fun ModifyRadius(radiusState: MutableState<Float>) {
    // 显示当前的 radius
    Text(text = "Current Radius: ${radiusState.value}")

    // 提供一个按钮来增加 radius 的值
    Button(onClick = { radiusState.value += 1f }) {
        Text(text = "Increase Radius")
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