package com.ai.aishotclientkotlin.ui.screens.settings.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.dao.entity.DeviceProfile
import com.ai.aishotclientkotlin.ui.screens.settings.model.DeviceInfoViewModel
import com.ai.aishotclientkotlin.ui.screens.settings.model.UserProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDisplayScreen(
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
// TODO 设置peekheight；
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", style = MaterialTheme.typography.titleMedium) },
                actions = {
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(Icons.Default.Edit, contentDescription = "修改信息")
                    }
                },
               // modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        content = { innerPadding ->
// 使用 Column 来展示设备和用户信息
            Column(
                modifier = Modifier
                    .padding(innerPadding) // 确保内容不会被顶部栏覆盖
                    .fillMaxSize()         // 使内容填满整个可用空间
                    .verticalScroll(rememberScrollState()), // 如果内容多于一屏，可以滚动
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DeviceInfoSurface()
                Spacer(modifier = Modifier.height(2.dp))
                UserInfoSurface(userProfileViewModel)
            }
        },

    )
}

@Composable
fun UserInfoSurface(userProfileViewModel: UserProfileViewModel) {
    val userProfileState by userProfileViewModel.userProfileState.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        /*   Column(
               modifier = Modifier
                   .fillMaxWidth(),
               verticalArrangement = Arrangement.Top,
               horizontalAlignment = Alignment.CenterHorizontally
           ) {*/
        // 用户信息卡片
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 用户头像
                AsyncImage(
                    model = userProfileState.avatarUrl,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .shadow(8.dp, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.poster),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {


                    //  Spacer(modifier = Modifier.height(8.dp))
                    // 姓名
                    ProfileRow(
                        label = "姓名", value = userProfileState.name
                    ) { userProfileViewModel.name = it }

                    // 昵称
                    ProfileRow(label = "昵称", value = userProfileState.nickname) {
                        userProfileViewModel.nickname = it
                    }

                    // 邮箱
                    ProfileRow(label = "邮箱", value = userProfileState.email) {
                        userProfileViewModel.email = it
                    }

                    // 电话号码
                    ProfileRow(label = "电话号码", value = userProfileState.phoneNumber) {
                        userProfileViewModel.phoneNumber = it
                    }
                }
            }

            // }
        }
    }
}

@Composable
fun DeviceInfoSurface(deviceViewModel: DeviceInfoViewModel = hiltViewModel()) {
    // 获取设备状态
    val deviceProfile by deviceViewModel.deviceProfile.collectAsState()

    // 保存用户选择的型号
    var selectedDevice by remember { mutableStateOf<DeviceProfile?>(null) }

    val icon = painterResource(id = R.drawable.ic_visibility)
    // 监听 deviceProfile 的变化并更新 selectedDevice
    LaunchedEffect(deviceProfile) {
        if (deviceProfile.isNotEmpty()) {
            selectedDevice = deviceProfile.firstOrNull()
            Log.e("HTTP", "deviceProfile is Not Empty")
        } else {
            Log.e("HTTP", "deviceProfile is Empty")
        }
    }
    if (deviceProfile.isEmpty()) {
        CircularProgressIndicator() // 或者自定义加载界面
    }else{
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .background(MaterialTheme.colorScheme.background),
            shape = MaterialTheme.shapes.large,
            // shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {

                Spacer(modifier = Modifier.weight(1.0f))
                IconButton(onClick = {
                    selectedDevice?.let { deviceViewModel.updateDevice(0, it) }
                }) {
                    Icon(icon,"Update the DeviceProfile")
                }
            }
                Spacer(modifier = Modifier.height(8.dp))

                // 设备图片显示
                Row() {
                    AsyncImage(
                        model = "https://example.com/device-image-url", // 替换为实际的设备图片 URL
                        contentDescription = "设备图片",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .shadow(8.dp, CircleShape), // 可选，设备图片显示为圆形
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.placeholder_image),
                        error = painterResource(R.drawable.poster),
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = "设备型号: ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1.0f))

                    // 设备型号选择 DropdownMenu
                    DropdownMenuItem(
                        models = deviceProfile.map { it.model },
                        selectedModel = selectedDevice?.model ?: "",
                        onModelSelected = {
                            selectedDevice = deviceProfile.find { device -> device.model == it }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column {


                    selectedDevice?.let { model ->
                        // 根据所选设备型号显示不同的设置信息

                        Spacer(modifier = Modifier.height(16.dp))

                        // 显示设备的设置信息
                        ProfileRow(label = "弓门距离", value = "${model.bow_gate_distance} m",
                            onValueChange = { newModel ->
                                //   selectedDevice = selectedDevice!!.copy(model = newModel)
                                selectedDevice!!.model = newModel

                                //TODO : 将copy修改为：  selectedDevice = selectedDevice!!.copy(model = newModel) 是否会有问题？
                                deviceViewModel.updateDevice(
                                    0,
                                    selectedDevice!!
                                ) // 更新 ViewModel 中的设备型号
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileRow(
                            label = "端头宽度",
                            value = "${model.head_width} m",
                            onValueChange = { value ->
                                //selectedDevice = selectedDevice!!.copy(model = newModel)
                                selectedDevice!!.head_width = value.toFloat()
                                deviceViewModel.updateDevice(
                                    0,
                                    selectedDevice!!
                                ) // 更新 ViewModel 中的设备型号
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = "皮筋厚度: ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.weight(1.0f))

                            // 设备型号选择 DropdownMenu
                            DropdownMenuItem(
                                models = deviceViewModel.THICKNESS_CHOICES.map { it.first },
                                selectedModel = (selectedDevice?.rubber_thickness ?: "").toString(),
                                onModelSelected = {
                                    selectedDevice?.rubber_thickness = it.toFloat()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = "皮筋宽度: ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.weight(1.0f))

                            // 设备型号选择 DropdownMenu
                            DropdownMenuItem(
                                models =  deviceViewModel.WIDTH_CHOICES.map { it.first },
                                selectedModel = (selectedDevice?.rubber_width ?: "").toString(),
                                onModelSelected = {
                                    selectedDevice?.rubber_width  = it.toFloat()
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = "皮筋初始化长度: ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.weight(1.0f))

                            // 设备型号选择 DropdownMenu
                            DropdownMenuItem(
                                models = deviceViewModel.LENGTH_CHOICES.map { it.first },
                                selectedModel = (selectedDevice?.initial_rubber_length ?: "").toString(),
                                onModelSelected = {
                                    selectedDevice?.initial_rubber_length = it.toFloat()
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileRow(
                            label = "Wi-Fi 账号",
                            value = model.wifi_account,
                            onValueChange = { value ->
                                // selectedDevice = selectedDevice!!.copy(model = newModel)
                                selectedDevice!!.wifi_account = value
                                deviceViewModel.updateDevice(
                                    0,
                                    selectedDevice!!
                                ) // 更新 ViewModel 中的设备型号
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileRow(
                            label = "Wi-Fi 密码",
                            value = model.wifi_password,
                            onValueChange = { value ->
                                selectedDevice!!.wifi_password = value
                                // selectedDevice = selectedDevice!!.copy(model = newModel)
                                deviceViewModel.updateDevice(
                                    0,
                                    selectedDevice!!
                                ) // 更新 ViewModel 中的设备型号
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileRow(
                            label = "AISHOT连接状态",
                            value = model.ble_ai_shot_addr,
                            onValueChange = { value ->
                                selectedDevice!!.ble_ai_shot_addr = value
                                deviceViewModel.updateDevice(
                                    0,
                                    selectedDevice!!
                                ) // 更新 ViewModel 中的设备型号
                            }
                        )
                        ProfileRow(
                            label = "Laser连接状态",
                            value = model.ble_laser_addr,
                            onValueChange = { value ->
                                selectedDevice!!.ble_laser_addr = value
                                deviceViewModel.updateDevice(
                                    0,
                                    selectedDevice!!
                                ) // 更新 ViewModel 中的设备型号
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileRow(
                            label = "电量",
                            value = "${model.battery_level}%",
                            onValueChange = { value ->
                                //     selectedDevice = selectedDevice!!.copy(model = newModel)
                                selectedDevice!!.battery_level = value.toInt()
                                deviceViewModel.updateDevice(
                                    0,
                                    selectedDevice!!
                                ) // 更新 ViewModel 中的设备型号
                            })
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownMenuItem(
    models: List<String>,
    selectedModel: String,
    onModelSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier) {
        TextButton(onClick = { expanded = true }) {
            Text(text = selectedModel)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model) },
                    onClick = {
                        onModelSelected(model)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun ProfileRow(
    label: String,
    value: String, // 当前值从 ViewModel 传入
    onValueChange: (String) -> Unit
)// 当值改变时调用此函数更新 ViewModel)
{
    var isEditing by remember { mutableStateOf(false) } // 编辑模式的状态
    var currentValue by remember { mutableStateOf(value) } // 存储值

    // 切换编辑模式
    fun toggleEditMode() {
        isEditing = !isEditing
    }

    // 检测双击的手势
    val doubleTapModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = { toggleEditMode() }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isEditing = false } // 点击行外部恢复为非编辑模式
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (isEditing) {
            // 编辑模式下显示 TextField
            TextField(
                value = value,
                onValueChange = {
                    currentValue = it // 更新本地的当前值
                    onValueChange(it) // 更新到 ViewModel
                },
                modifier = Modifier
                    .weight(1f)
                    .then(doubleTapModifier), // 支持双击切换编辑
                textStyle = MaterialTheme.typography.labelMedium,
                singleLine = true
            )
        } else {
            // 非编辑模式下显示 Text
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = doubleTapModifier // 支持双击切换编辑
            )
        }
    }
    HorizontalDivider() // 行的分割线
}


@Composable
fun UserProfileSettingsScreen(
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 控制 Avatar 上传对话框的显示
    val showUploadDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // Section: Avatar
      //  Text("个人信息", style = MaterialTheme.typography.headlineSmall)

        Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onCancel) {
                    Text("取消")
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = onSave) {
                    Text("保存")
                }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = userProfileViewModel.avatarUrl,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                showUploadDialog.value = true  // 点击按钮显示对话框
                Log.e("EVENT","showUploadDialog.value is ${showUploadDialog.value}")
             }) {
                Text("更改头像")
            }

            if (showUploadDialog.value) {
                UploadAvatarScreen(userProfileViewModel)
            }
        }

        // Section: Basic Info
        Spacer(modifier = Modifier.height(16.dp))
        Text("基本信息", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = userProfileViewModel.name,
            onValueChange = { userProfileViewModel.name = it },
            label = { Text("姓名") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = userProfileViewModel.nickname,
            onValueChange = { userProfileViewModel.nickname = it },
            label = { Text("昵称") },
            modifier = Modifier.fillMaxWidth()
        )

        // 新增电话号码
        OutlinedTextField(
            value = userProfileViewModel.phoneNumber,
            onValueChange = { userProfileViewModel.phoneNumber = it },
            label = { Text("电话号码") }
        )
        // Section: Password Change
        Spacer(modifier = Modifier.height(16.dp))
        Text("安全设置", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = userProfileViewModel.currentPassword,
            onValueChange = { userProfileViewModel.currentPassword = it },
            label = { Text("当前密码") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        OutlinedTextField(
            value = userProfileViewModel.newPassword,
            onValueChange = { userProfileViewModel.newPassword = it },
            label = { Text("新密码") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        OutlinedTextField(
            value = userProfileViewModel.confirmPassword,
            onValueChange = { userProfileViewModel.confirmPassword = it },
            label = { Text("确认新密码") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )


    }
}
