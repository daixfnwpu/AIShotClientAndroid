package com.ai.aishotclientkotlin.ui.screens.settings.screen
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ai.aishotclientkotlin.ui.screens.settings.model.BLEViewModel

@Composable
fun BLEScreen(bleViewModel: BLEViewModel = viewModel()) {
    var showDeviceList by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }//"64:E8:33:51:2D:31"
    val devices by bleViewModel.devices.collectAsState()
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
