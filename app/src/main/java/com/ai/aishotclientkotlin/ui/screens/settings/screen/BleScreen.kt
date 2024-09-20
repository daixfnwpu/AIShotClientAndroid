package com.ai.aishotclientkotlin.ui.screens.settings.screen
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ai.aishotclientkotlin.data.shotclient.Characteristic
import com.ai.aishotclientkotlin.ui.screens.settings.model.BLEViewModel

@Composable
fun BLEScreen(bleViewModel: BLEViewModel = viewModel()) {
    var showDeviceList by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }//"64:E8:33:51:2D:31"
    val devices by bleViewModel.scandevices.collectAsState()
    val connectedDevices by bleViewModel.connectdevices.collectAsState()


    LaunchedEffect(Unit) {
        bleViewModel.getConnectedDevices()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            showDeviceList = true
            bleViewModel.startScan() // 开始扫描设备
        }) {
            Text("Scan BLE Devices")
        }

     //   BleTextSender(bleViewModel)

        BleInputScreen(bleViewModel)
        // 设备列表弹窗
        if (showDeviceList && !isConnected) {
            AlertDialog(
                onDismissRequest = {
                    showDeviceList = false
                    bleViewModel.stopScan() // 停止扫描
                },
                title = { Text("Select a Device") },
                text = {
                    // 使用LazyColumn显示扫描到的设备列表
                    LazyColumn {
                        items(devices.size) { index ->
                            val device = devices[index]
                            DeviceItem(device) {
                                // 点击设备，开始连接
                                bleViewModel.connectToDevice(device) {
                                    isConnected = true
                                    showDeviceList = false // 连接成功后关闭列表
                                    bleViewModel.stopScan() // 停止扫描

                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showDeviceList = false
                        bleViewModel.stopScan() // 停止扫描
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (isConnected) {
            Text("Connected to device!")
        }
        if(connectedDevices.isNotEmpty())
        {
            Text("已经有设备连接成功")
        }

    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = device.name ?: "Unknown Device")
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = device.address)
    }
}


@Composable
fun BleTextSender(
    bleViewModel: BLEViewModel
) {
    var text by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            label = { Text("Enter text to send") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                isSending = true
                bleViewModel.writeDataToCharacteristic(Characteristic.radius,text)
                isSending = false
            },
            enabled = !isSending,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Data")
        }
    }
}


/// TODO : 需要处理一个Flow 参照另外的flow的例子；

@Composable
fun BleInputScreen(viewModel: BLEViewModel = viewModel()) {
    val bleState by viewModel.bleState.collectAsState()
    val characteristics by viewModel.characteristics.collectAsState()
    val writeResults by viewModel.writeResults.collectAsState()
    val devices by viewModel.scandevices.collectAsState()
    val connectedDevices by viewModel.connectdevices.collectAsState()
    Column {
        var connectState =  if (connectedDevices.isNotEmpty()) "Connected" else "Disconnected"
        Text(text = "BLE State: $connectState")

        // 显示所有特征及其值
        characteristics.forEach { (characteristic, value) ->
            Text(text = "${characteristic.name}: ${value.toString()}")
            Button(onClick = { viewModel.readDataFromCharacteristic(characteristic) }) {
                Text(text = "Read ${characteristic.name}")
            }
        }

        // 输入框，用于写入数据
        var inputText by remember { mutableStateOf("") }
        TextField(value = inputText, onValueChange = { inputText = it }, label = { Text("Send Data") })

        // 写入数据到特定特征
        Button(onClick = { viewModel.writeDataToCharacteristic(Characteristic.radius, inputText) }) {
            Text(text = "Send Data to Radius")
        }

        // 显示写入结果
        Text(text = "Write Success: ${writeResults[Characteristic.radius]}")

        // 启用或禁用通知
        Button(onClick = { viewModel.enableNotifications(Characteristic.radius, true) }) {
            Text(text = "Enable Notifications for Radius")
        }
    }
}

