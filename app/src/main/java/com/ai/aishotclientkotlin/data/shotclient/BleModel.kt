package com.ai.aishotclientkotlin.data.shotclient

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import java.util.UUID


// 修改后的 Characteristic 类，嵌套了 Property 类
enum class Characteristic(
    val uuid: UUID,
    val properties: Int // 操作权限，组合使用 Property 枚举
) {
    radius(UUID.fromString("00002a6e-0000-1000-8000-00805F9B34FB"), Property.READ.value or Property.WRITE.value),
    velocity(UUID.fromString("00002a6f-0000-1000-8000-00805F9B34FB"), Property.READ.value or Property.NOTIFY.value),
    velocityAngle(UUID.fromString("00002a60-0000-1000-8000-00805F9B34FB"), Property.WRITE.value),
    density(UUID.fromString("00002a61-0000-1000-8000-00805F9B34FB"), Property.READ.value),
    eyeToBowDistance(UUID.fromString("00002a62-0000-1000-8000-00805F9B34FB"), Property.READ.value),
    eyeToAxisDistance(UUID.fromString("00002a63-0000-1000-8000-00805F9B34FB"), Property.READ.value or Property.NOTIFY.value),
    shotDoorWidth(UUID.fromString("00002a64-0000-1000-8000-00805F9B34FB"), Property.READ.value),
    shotHeadWidth(UUID.fromString("00002a65-0000-1000-8000-00805F9B34FB"), Property.READ.value or Property.WRITE.value),
    shotDistance(UUID.fromString("00002a66-0000-1000-8000-00805F9B34FB"), Property.READ.value or Property.NOTIFY.value),
    shotDiffDistance(UUID.fromString("00002a67-0000-1000-8000-00805F9B34FB"), Property.READ.value),
    angleTarget(UUID.fromString("00002a68-0000-1000-8000-00805F9B34FB"), Property.WRITE.value),
    //TODO: COMMAND 用于app向AISHOT  write command 的通道；
    COMMAND(UUID.fromString("00002a70-0000-1000-8000-00805F9B34FB"), Property.WRITE.value),
    shotPointHead(UUID.fromString("00002a69-0000-1000-8000-00805F9B34FB"), Property.WRITE.value);

    // 嵌套的 Property 类，定义操作权限的位标志（读、写、通知等）
    enum class Property(val value: Int) {
        READ(0x01),
        WRITE(0x02),
        NOTIFY(0x04)
    }

    // Companion object to look up enum by UUID
    companion object {
        private val uuidToCharacteristicMap = entries.associateBy { it.uuid }

        fun fromUuid(uuid: UUID): Characteristic? {
            return uuidToCharacteristicMap[uuid]
        }
    }

    // 可变的 value 值，创建时未指定，运行时可读写
    var value: Any? = null
        private set

    // 修改 value 值的函数
    fun updateValue(newValue: Any?) {
        value = newValue
    }

    // 检查特性是否具有指定的操作权限
    fun hasProperty(property: Property): Boolean {
        return (properties and property.value) != 0
    }


    // 处理读特性
    @SuppressLint("MissingPermission")
    fun readCharacteristic(gatt: BluetoothGatt, bluetoothGattCharacteristic: BluetoothGattCharacteristic) {
        if (hasProperty(Property.READ)) {
            gatt.readCharacteristic(bluetoothGattCharacteristic)
        }
    }

    // 处理写特性
    @SuppressLint("MissingPermission")
    fun writeCharacteristic(gatt: BluetoothGatt, bluetoothGattCharacteristic: BluetoothGattCharacteristic, data: ByteArray) {
        if (hasProperty(Property.WRITE)) {
            bluetoothGattCharacteristic.value = data
            gatt.writeCharacteristic(bluetoothGattCharacteristic)
        }
    }

    // 处理通知订阅
    @SuppressLint("MissingPermission")
    fun enableNotifications(gatt: BluetoothGatt, bluetoothGattCharacteristic: BluetoothGattCharacteristic, enable: Boolean) {
        if (hasProperty(Property.NOTIFY)) {
            gatt.setCharacteristicNotification(bluetoothGattCharacteristic, enable)
            // 通常需要配置描述符以启用通知
            val descriptor = bluetoothGattCharacteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")
            )
            descriptor.value = if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }
    }
}