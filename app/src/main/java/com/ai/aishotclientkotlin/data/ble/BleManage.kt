package com.ai.aishotclientkotlin.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ai.aishotclientkotlin.data.repository.DeviceProfileRepository
import com.ai.aishotclientkotlin.util.SpManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject


//!!! TODO : bluetoothGatt is not null ,表示已经连接成功？
/// !!! TODO: buletooth的服务器，必须要重启后，才能够再连接？！！可否支持不用线扫描就连接呢？
/// ！！！TODO： BluetoothGatt 发送和接收消息，或者是订阅消息；
/// !!! TODO: 测试scan ble的stop功能。


@SuppressLint("MissingPermission")
object BLEManager {
    private var repository: DeviceProfileRepository? = null
    private val serviceUuid: UUID =
        UUID.fromString("0000181a-0000-1000-8000-00805f9b34fb") // Replace with actual service UUID

    private val descriptorUuid: UUID =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // Replace with actual descriptor UUID

    private var appContext: Context? = null
    fun initialize(context: Context) {
        // 初始化相关操作
        Log.e("ble", "initialize")
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        appContext = context


    }

    var onConnectionStateChanged: MutableList<(String) -> Unit> = mutableListOf()
    var onCharacteristicRead: MutableList<(Characteristic, ByteArray) -> Unit> = mutableListOf()
    var onCharacteristicWrite: MutableList<(Characteristic, Boolean) -> Unit> = mutableListOf()
    var onNotificationReceived: MutableList<(Characteristic, ByteArray) -> Unit> = mutableListOf()


    fun setDeviceProfileRepository(deviceProfileRepository: DeviceProfileRepository) {
        this.repository = deviceProfileRepository
    }

    // 当连接状态改变时，调用所有注册的回调
    fun handleConnectionStateChange(state: String) {
        onConnectionStateChanged.forEach { callback ->
            callback.invoke(state)
        }
    }

    // 当读取到特征时，调用所有注册的回调
    fun handleCharacteristicRead(characteristic: Characteristic, data: ByteArray) {
        onCharacteristicRead.forEach { callback ->
            callback.invoke(characteristic, data)
        }
    }

    // 当写入特征时，调用所有注册的回调
    fun handleCharacteristicWrite(characteristic: Characteristic, success: Boolean) {
        onCharacteristicWrite.forEach { callback ->
            callback.invoke(characteristic, success)
        }
    }

    // 当接收到通知时，调用所有注册的回调
    fun handleNotificationReceived(characteristic: Characteristic, data: ByteArray) {
        onNotificationReceived.forEach { callback ->
            callback.invoke(characteristic, data)
        }
    }


    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    //private  var bluetoothGatt: BluetoothGatt? = null
    //  private val listGattDevice : Map<Int,BluetoothGatt?> = mutableMapOf()

    private var mBleAIShotDevice: BluetoothGatt? = null
    private var mBleLaserDevice: BluetoothGatt? = null

    // 定义 ScanSettings
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER) // 设置为高频扫描模式
        .build()

    // GATT回调
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Timber.tag("BLE").e("Connected to GATT server.")
                gatt?.discoverServices() // 发现服务
                if (gatt?.device?.name?.startsWith("AIS_") == true) {
                    Timber.tag("BLE").e("Connected to AI shot.")
                    gatt?.device?.address?.let {
                        executeSaveDeviceAddress(
                            it,
                            SpManager.Sp.BLE_AISHOT
                        )
                    }


                } else {
                    Timber.tag("BLE").e("Connected to Laser.")
                    gatt?.device?.address?.let {
                        executeSaveDeviceAddress(
                            it,
                            SpManager.Sp.BLE_LASER
                        )
                    }
                }
                handleConnectionStateChange("Connected")


                // TODO : 连接成功后，启动服务；
//                val intent = Intent(appContext, BleService::class.java)
//                startService(intent)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Timber.tag("BLE").e("Disconnected from GATT server.")
                handleConnectionStateChange("Disconnected")
                gatt?.disconnect()
                // gatt?.close()
                //  bluetoothGatt = null
                Log.e("BLEManager", "Disconnected and closed GATT connection.")
                if (gatt != null) {
                    reconnectWithRetry(gatt)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (gatt != null) {
                    subscribeToCharacteristic(gatt)
                }

            } else {
                Timber.tag("BLE").e("onServicesDiscovered received: " + status)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic?.value
                Timber.tag("BLE")
                    .e("Characteristic read: " + (data?.toString(Charsets.UTF_8) ?: "没有值"))


                characteristic?.let {
                    // 传递读到的特征值
                    val charEnum = Characteristic.fromUuid(it.uuid)
                    charEnum?.let { char -> handleCharacteristicRead(char, it.value) }
                }


            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            characteristic?.let {
                val charEnum = Characteristic.fromUuid(it.uuid)
                val success = status == BluetoothGatt.GATT_SUCCESS
                charEnum?.let { char -> handleCharacteristicWrite(char, success) }
            }
        }

        //onCharacteristicChanged: Invoked when the BLE device sends a notification for the characteristic you’ve subscribed to. This is where you read the updated data.
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.e("Ble", "onCharacteristicChanged")
            val data = characteristic?.value
            val uuid = characteristic?.uuid
            val charatic = uuid?.let { Characteristic.fromUuid(uuid) }
            Timber.tag("BLE").e("The charatic name is : " + charatic?.name)
            Timber.tag("BLE").e(
                uuid.toString() + " Received data: " + (data?.toString(Charsets.UTF_8) ?: "没有值")
            )


            characteristic?.let {
                val charEnum = Characteristic.fromUuid(it.uuid)
                charEnum?.let { char ->

                    handleNotificationReceived(char, it.value)

                }
            }
        }
    }


    fun startScan(onDeviceFound: (BluetoothDevice) -> Unit) {

        bluetoothLeScanner?.startScan(null, scanSettings, object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)

                result?.device?.let { device ->
                    onDeviceFound(device)


                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                results?.forEach { result ->
                    result.device?.let {
                        onDeviceFound(it)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                // 扫描失败处理 // TODO: 提示，关闭蓝牙后，再打开，同时，注意查看shot设备是否打开？
            }
        })
    }

    fun stopScan() {
        bluetoothLeScanner?.stopScan(object : ScanCallback() {})
    }

    // 获取已连接的设备
    fun getConnectedDevices(): List<BluetoothDevice> {
        return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT) // 获取 GATT 连接的设备
    }

    fun connectToDevice(deviceAddress: String): BluetoothGatt? {
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        device?.let { Timber.tag("ble").e("Call connectToDevice %s,%s", device.address, it.uuids) }

        //TODO : 修改了autoConnect 为true。看会不会影响？
        return device?.connectGatt(appContext, true, gattCallback)
    }

    // How to call this function
    fun executeSaveDeviceAddress(deviceAddress: String, deviceType: SpManager.Sp) {
        CoroutineScope(Dispatchers.IO).launch {
            saveDeviceAddress(deviceAddress, deviceType)
        }
    }

    private suspend fun saveDeviceAddress(deviceAddress: String, deviceType: SpManager.Sp) {
        if (deviceType == SpManager.Sp.BLE_AISHOT) {
            appContext?.let {
                SpManager(it).setSharedPreference(
                    SpManager.Sp.BLE_AISHOT,
                    deviceAddress
                )
            }
            repository?.loadDeviceProfiles(success = {

            }, error = {

            })?.collectLatest {
                it.forEach {
                    repository?.updateDeviceProfile(
                        it.id,
                        it.copy(ble_ai_shot_addr = deviceAddress),
                        success = {},
                        error = {})
                }
            }
        }
        if (deviceType == SpManager.Sp.BLE_LASER) {
            appContext?.let {
                SpManager(it).setSharedPreference(
                    SpManager.Sp.BLE_LASER,
                    deviceAddress
                )
            }

            repository?.loadDeviceProfiles(success = {

            }, error = {

            })?.collectLatest {
                it.forEach {
                    repository?.updateDeviceProfile(
                        it.id,
                        it.copy(ble_laser_addr = deviceAddress),
                        success = {},
                        error = {})
                }
            }
        }
    }

    fun reconnectLastDevice(bluetoothGatt: BluetoothGatt) {
        Log.e("BLE", "In The BLEManger reconnect the Device")
        val lastDeviceAddress = appContext?.let {
            SpManager(it).getSharedPreference(
                SpManager.Sp.BLE_AISHOT,
                null
            )
        }                      //sharedPreferences?.getString("LAST_CONNECTED_DEVICE", null)
        Timber.tag("BLE").e("lastDeviceAddress%s", lastDeviceAddress)
        if (bluetoothGatt == mBleAIShotDevice)
            lastDeviceAddress?.let { mBleLaserDevice = connectToDevice(it) }
        if (bluetoothGatt == mBleLaserDevice)
            lastDeviceAddress?.let { mBleLaserDevice = connectToDevice(it) }
    }

    fun reconnectAllBleDevice() {
        Log.e("BLE", "In The BLEManger reconnect the Device")
        val ble_aishot_address =
            appContext?.let { SpManager(it).getSharedPreference(SpManager.Sp.BLE_AISHOT, null) }
        val ble_laser_address =
            appContext?.let { SpManager(it).getSharedPreference(SpManager.Sp.BLE_LASER, null) }
        //sharedPreferences?.getString("LAST_CONNECTED_DEVICE", null)
        Timber.tag("BLE").e("ble_aishot_address: %s", ble_aishot_address)
        Timber.tag("BLE").e("ble_laser_address: %s", ble_laser_address)
        //   if(bluetoothGatt == mBleAIShotDevice)
        ble_aishot_address?.let { mBleLaserDevice = connectToDevice(it) }
        //  if(bluetoothGatt == mBleLaserDevice)
        ble_laser_address?.let { mBleLaserDevice = connectToDevice(it) }
    }

    /**
     * 如果你在应用退出时手动调用了 bluetoothGatt?.disconnect() 和 bluetoothGatt?.close()，
     * 那么当应用重启后将不会自动重连到设备。这是因为调用 close() 方法会释放与 BluetoothGatt 相关的资源，
     * 并且使得 BluetoothGatt 对象无效
     */
    // 断开设备连接
    @SuppressLint("MissingPermission")
    fun disconnect(bluetoothGatt: BluetoothGatt) {
        bluetoothGatt?.disconnect()
        //  bluetoothGatt?.close()
        // bluetoothGatt = null
        Timber.tag("BLE").e("Disconnected from device")
    }

    // 断开设备连接
    @SuppressLint("MissingPermission")
    fun disconnectAllDevice() {
        mBleAIShotDevice?.disconnect()
        mBleLaserDevice?.disconnect()
        //  bluetoothGatt?.close()
        // bluetoothGatt = null
        Timber.tag("BLE").e("Disconnected from device")
    }

    private fun subscribeToCharacteristic(bluetoothGatt: BluetoothGatt) {
        bluetoothGatt?.let { gatt ->

            Timber.tag("BLE").e("Services discovered")
            // 获取服务并处理特征
            val service = gatt?.getService(serviceUuid)

            Characteristic.values().forEach { characteristicEnum ->
                val characteristic =
                    service?.let { getCharacteristicFromService(it, characteristicEnum) }

                characteristic?.let {
                    readCharacteristic(characteristicEnum)
                    // Enable notifications
                    gatt.setCharacteristicNotification(it, true)

                    // Configure the descriptor for notifications
                    val descriptor = it.getDescriptor(descriptorUuid)
                    descriptor?.let { desc ->
                        // Check if ENABLE_NOTIFICATION_VALUE is deprecated
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Use updated methods or constants if available
                            desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        } else {
                            // Use legacy value if updated constants are not available
                            desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        }
                        gatt.writeDescriptor(desc)
                    }
                }
            }
        }
    }

    // 读某个特征值
    fun readCharacteristic(characteristic: Characteristic) {
        val  bluetoothGatt: BluetoothGatt? = when(characteristic.deviceType){
            SpManager.Sp.BLE_AISHOT -> mBleAIShotDevice
            SpManager.Sp.BLE_LASER -> mBleLaserDevice
            else -> {/*Log.e("BLEDEVICE","BELDEVICE TYPE WRONG");*/ null}
        }
        val gattCharacteristic =
            bluetoothGatt?.let { getCharacteristicFromDefaultService(it, characteristic) }
        //   bluetoothGatt?.readCharacteristic(gattCharacteristic)
        bluetoothGatt?.let {
            gattCharacteristic?.let { it1 ->
                characteristic.readCharacteristic(it, it1)
            }
        }
    }

    // 写某个特征值
    fun writeCharacteristic(

        characteristic: Characteristic,
        data: ByteArray
    ) {
       val  bluetoothGatt: BluetoothGatt? = when(characteristic.deviceType){
           SpManager.Sp.BLE_AISHOT -> mBleAIShotDevice
           SpManager.Sp.BLE_LASER -> mBleLaserDevice
           else -> {/*Log.e("BLEDEVICE","BELDEVICE TYPE WRONG");*/ null}
       }
        val gattCharacteristic =
            bluetoothGatt?.let { getCharacteristicFromDefaultService(it, characteristic) }

        bluetoothGatt?.let {
            gattCharacteristic?.let { it1 ->
                characteristic.writeCharacteristic(
                    it,
                    it1, data
                )
            }
        }

    }

    private fun getCharacteristicFromDefaultService(
        bluetoothGatt: BluetoothGatt,
        character: Characteristic,
        sUuid: UUID = serviceUuid
    ): BluetoothGattCharacteristic? {
        // Assume you have a reference to a BluetoothGatt instance
        val service = bluetoothGatt?.getService(sUuid)
        return service?.let { getCharacteristicFromService(it, character) }
    }


    private fun getCharacteristicFromService(
        service: BluetoothGattService, characteristic: Characteristic
    ): BluetoothGattCharacteristic? {
        return service.getCharacteristic(characteristic.uuid)
    }


    // 启用或禁用通知
    fun enableNotifications(
       // bluetoothGatt: BluetoothGatt,
        characteristic: Characteristic,
        enable: Boolean
    ) {

        val  bluetoothGatt: BluetoothGatt? = when(characteristic.deviceType){
            SpManager.Sp.BLE_AISHOT -> mBleAIShotDevice
            SpManager.Sp.BLE_LASER -> mBleLaserDevice
            else -> {/*Log.e("BLEDEVICE","BELDEVICE TYPE WRONG");*/ null}
        }

        val gattCharacteristic =
            bluetoothGatt?.let { getCharacteristicFromDefaultService(it, characteristic) }
        bluetoothGatt?.let {
            gattCharacteristic?.let { it1 ->
                characteristic.enableNotifications(
                    it,
                    it1, enable
                )
            }
        }
    }


    fun reconnectWithRetry(bluetoothGatt: BluetoothGatt, retryDelay: Long = 5000L) {

        // Set a retry mechanism if the connection fails
        Handler(Looper.getMainLooper()).postDelayed({

            if (bluetoothGatt == null) {
                reconnectLastDevice(bluetoothGatt)
                Log.e("BLEManager", "Retrying connection after delay.")
                reconnectWithRetry(bluetoothGatt, retryDelay)
            }
        }, retryDelay)
    }


}




