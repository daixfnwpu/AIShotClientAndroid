package com.ai.aishotclientkotlin.data.ble

import android.content.Intent

/* loaded from: classes.dex */
class BluetoothCommunication {
    private var btCache: ArrayList<Byte>? = null
    private var isDataHandlerThreadRuning = true
    private var mDataHandlerThread: Thread? = null
    private var mOnReceiveShakeHandListener: OnReceiveShakeHandListener? = null

    interface OnReceiveShakeHandListener {
        fun onReceiveShakeHand()
    }

    fun setReceiveShakeHandListener(onReceiveShakeHandListener: OnReceiveShakeHandListener?) {
        this.mOnReceiveShakeHandListener = onReceiveShakeHandListener
    }

    init {
        this.btCache = ArrayList()
        val thread: Thread = Thread(DataHandler())
        this.mDataHandlerThread = thread
        thread.start()
    }

    fun addDataToCache(b: Byte) {
        btCache!!.add(b)
    }

    fun addDataToCache(bArr: ByteArray) {
        for (b in bArr) {
            btCache!!.add(b)
        }
    }

    fun clearTheCommunicationCache() {
        btCache!!.clear()
    }

    fun onDestroy() {
        this.isDataHandlerThreadRuning = false
        this.mDataHandlerThread = null
        clearTheCommunicationCache()
        this.btCache = null
    }

    /* loaded from: classes.dex */
    private inner class DataHandler() : Runnable {
        private fun sendDataBroadcast(bArr: ByteArray) {
            if (bArr.size != 19) {
                return
            }
            val d = (((bArr[0].toInt() and 255) shl 8) or (bArr[1].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d)
            val f = (d / 10.0).toFloat()
            val d2 = (((bArr[2].toInt() and 255) shl 8) or (bArr[3].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d2)
            val d3 = (((bArr[4].toInt() and 255) shl 8) or (bArr[5].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d3)
            val f2 = (d3 / 10.0).toFloat()
            val d4 = (((bArr[6].toInt() and 255) shl 8) or (bArr[7].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d4)
            val d5 = (((bArr[8].toInt() and 255) shl 8) or (bArr[9].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d5)
            val d6 = (((bArr[10].toInt() and 255) shl 8) or (bArr[11].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d6)
            val d7 = (((bArr[12].toInt() and 255) shl 8) or (bArr[13].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d7)
            val d8 = (((bArr[14].toInt() and 255) shl 8) or (bArr[15].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d8)
            val d9 = (((bArr[16].toInt() and 255) shl 8) or (bArr[17].toInt() and 255)).toShort()
                .toDouble()
            java.lang.Double.isNaN(d9)
            val b = bArr[18]
            val intent = Intent()
            intent.putExtra(INTENT_TAG_ANGLE_ELEVATION, f)
            intent.putExtra(INTENT_TAG_LINEAR_DISTANCE, (d2 / 10.0).toFloat())
            intent.putExtra(INTENT_TAG_VERTICAL_HEIGHT, f2)
            intent.putExtra(INTENT_TAG_LEVEL_DISTANCE, (d4 / 10.0).toFloat())
            intent.putExtra("TWO_POINT_HEIGHT", (d5 / 10.0).toFloat())
            intent.putExtra(INTENT_TAG_ANGLE_COMPASS, (d6 / 10.0).toFloat())
            intent.putExtra(INTENT_TAG_HORIZONTAL_ANGLE, (d7 / 10.0).toFloat())
            intent.putExtra(INTENT_TAG_POINT_TO_POINT_DISTANCE, (d8 / 10.0).toFloat())
            intent.putExtra("SPEED", (d9 / 10.0).toFloat())
            intent.putExtra(INTENT_TAG_DISTANCE_UNIT, b.toInt())
            intent.setAction(StringForGlobal.DATA_ACTION_FOR_BROADCAST)
            MyApplication.mGlobalContext.sendBroadcast(intent)
        }

        private fun sendDeviceConnectedBroadcast() {
            val intent = Intent()
            intent.setAction(StringForGlobal.DEVICE_CONNECTED_ACTION_BROADCAST)
            MyApplication.mGlobalContext.sendBroadcast(intent)
        }

        // java.lang.Runnable
        override fun run() {
            while (this@BluetoothCommunication.isDataHandlerThreadRuning) {
                if (btCache!!.size >= 5) {
                    var b: Byte = 0
                    if ((btCache!![0].toInt() and 255) == COMMUNICATION_HEAD_H) {
                        if ((btCache!![1].toInt() and 255) != 167) {
                            for (i in 1 downTo 0) {
                                btCache!!.removeAt(i)
                            }
                        } else if (btCache!!.size >= (btCache!![2].toInt() and 255) + 4) {
                            val byteValue =
                                btCache!![(btCache!![2].toInt() and 255) + 2].toInt() and 255
                            val byteValue2 =
                                btCache!![(btCache!![2].toInt() and 255) + 3].toInt() and 255
                            if (byteValue == COMMUNICATION_END_H && byteValue2 == COMMUNICATION_END_L) {
                                for (i2 in 2 until (btCache!![2].toInt() and 255) + 1) {
                                    b = (b + btCache!![i2]).toByte()
                                }
                                if (b == btCache!![(btCache!![2].toInt() and 255) + 1]) {
                                    val byteValue3 = btCache!![4].toInt() and 255
                                    if (byteValue3 != 8) {
                                        if (byteValue3 == CMD_MEASURE_BACK) {
                                            val bArr = ByteArray(19)
                                            for (i3 in 5..23) {
                                                bArr[i3 - 5] = btCache!![i3]
                                            }
                                            sendDataBroadcast(bArr)
                                        } else if (byteValue3 == CMD_CONNECTED_BACK) {
                                            sendDeviceConnectedBroadcast()
                                        }
                                    } else if (this@BluetoothCommunication.mOnReceiveShakeHandListener != null) {
                                        mOnReceiveShakeHandListener!!.onReceiveShakeHand()
                                    }
                                }
                            }
                            try {
                                for (byteValue4 in (btCache!![2].toInt() and 255) + 3 downTo 0) {
                                    btCache!!.removeAt(byteValue4)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        btCache!!.removeAt(0)
                    }
                } else {
                    try {
                        Thread.sleep(50L)
                    } catch (e2: InterruptedException) {
                        e2.printStackTrace()
                    }
                }
            }
        }
    }

    companion object {
        private const val CMD_ADDRESS_BACK = 139
        private const val CMD_ADDRESS_SET = 11
        private const val CMD_BAUDRATE_BACK = 138
        private const val CMD_BAUDRATE_SET = 10
        private const val CMD_CONFIG_BACK = 130
        private const val CMD_CONFIG_GET = 2
        private const val CMD_CONNECTED_BACK = 134
        private const val CMD_CONNECTED_SET = 6
        private const val CMD_DISCONNECTED_BACK = 135
        private const val CMD_DISCONNECTED_SET = 7
        private const val CMD_ENCRYPT_BACK = 132
        private const val CMD_ENCRYPT_SET = 4
        private const val CMD_GUID_BACK = 131
        private const val CMD_GUID_GET = 3
        private const val CMD_MEASURE_BACK = 133
        private const val CMD_MEASURE_GET = 5
        private const val CMD_RENAME_SET = 9
        private const val CMD_RENAM_BACK = 137
        private const val CMD_SHAKE_HAND_BACK = 136
        private const val CMD_SHAKE_HAND_SET = 8
        private const val CMD_TEMPERATURE_BACK = 140
        private const val CMD_TEMPERATURE_SET = 12
        private const val CMD_VERSION_BACK = 129
        private const val CMD_VERSION_GET = 1
        private const val COMMUNICATION_END_H = 188
        private const val COMMUNICATION_END_L = 183
        private const val COMMUNICATION_HEAD_H = 174
        private const val COMMUNICATION_HEAD_L = 167
        const val INTENT_TAG_ANGLE_COMPASS: String = "NOR"
        const val INTENT_TAG_ANGLE_ELEVATION: String = "ANG"
        const val INTENT_TAG_DISTANCE_UNIT: String = "UNIT"
        const val INTENT_TAG_HORIZONTAL_ANGLE: String = "HORIZONTAL_ANGLE"
        const val INTENT_TAG_LEVEL_DISTANCE: String = "LEV"
        const val INTENT_TAG_LINEAR_DISTANCE: String = "DIS"
        const val INTENT_TAG_POINT_TO_POINT_DISTANCE: String = "P2PDIS"
        const val INTENT_TAG_TARGET_SPEED: String = "SPEED"
        const val INTENT_TAG_TWO_POINT_HEIGHT: String = "TWO_POINT_HEIGHT"
        const val INTENT_TAG_VERTICAL_HEIGHT: String = "HIG"
    }
}