package com.ai.aishotclientkotlin.ui.screens.settings.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ai.aishotclientkotlin.ui.screens.settings.model.BLEViewModel

@Composable
fun BLEScreen(bleViewModel: BLEViewModel = viewModel()) {
    var isScanning by remember { mutableStateOf(false) }
    var deviceAddress by remember { mutableStateOf("64:E8:33:51:2D:31") } // 示例MAC地址
    //"3564B42E-36C8-70A4-FAF9-281BC1831655"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 扫描按钮
        Button(onClick = {
            if (isScanning) {
                bleViewModel.stopScan()
            } else {
                bleViewModel.startScan()
            }
            isScanning = !isScanning

        }) {
            Text(if (isScanning) "Stop Scan" else "Start Scan")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 连接设备按钮
        Button(onClick = {
            bleViewModel.connectToDevice(deviceAddress)
        }) {
            Text("Connect to Device")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 断开设备按钮
        Button(onClick = {
            bleViewModel.disconnect()
        }) {
            Text("Disconnect Device")
        }
    }
}
