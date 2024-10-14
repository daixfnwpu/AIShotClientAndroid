package com.ai.aishotclientkotlin.data.dao.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "DeviceProfile")
data class DeviceProfile(
    @PrimaryKey()
    var id : Long,
    var model: String,             // 型号，默认是 "铂金版"
    var bow_gate_distance: Float = 0.04f,           // 弓门距离，默认 0.04m
    var head_width: Float = 0.025f,                // 端头宽度，默认 0.025m
    var rubber_thickness: Float = 0.0045f,         // 皮筋厚度，默认 0.0045m
    var initial_rubber_length: Float = 0.22f,       // 皮筋初始化长度，默认 0.22m
    var rubber_width: Float = 0.025f,              // 皮筋宽度，默认等于端头宽度
    var wifi_account: String = "aishotclient",     // Wi-Fi 账号，默认值
    var wifi_password: String = "aishotclient123", // Wi-Fi 密码，默认值
    var ble_connection: Boolean = false,           // BLE 连接状态，默认 false
    var battery_level: Int = 100                   // 电量，默认 100%
) {
    // 手动实现 copy 函数
    /*fun copy(
        id: Long = this.id,
        model: String = this.model,
        bow_gate_distance: Float = this.bow_gate_distance,
        head_width: Float = this.head_width,
        rubber_thickness: Float = this.rubber_thickness,
        initial_rubber_length: Float = this.initial_rubber_length,
        rubber_width: Float = this.rubber_width,
        wifi_account: String = this.wifi_account,
        wifi_password: String = this.wifi_password,
        ble_connection: Boolean = this.ble_connection,
        battery_level: Int = this.battery_level
    ): DeviceProfile {
        return DeviceProfile(
            id = id,
            model = model,
            bow_gate_distance = bow_gate_distance,
            head_width = head_width,
            rubber_thickness = rubber_thickness,
            initial_rubber_length = initial_rubber_length,
            rubber_width = rubber_width,
            wifi_account = wifi_account,
            wifi_password = wifi_password,
            ble_connection = ble_connection,
            battery_level = battery_level
        )
    }*/
}