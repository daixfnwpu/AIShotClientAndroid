package com.ai.aishotclientkotlin.domain.model.sensor

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val _rotationData = MutableStateFlow(Triple(0f, 0f, 0f)) // 方位角, 俯仰角, 滚动角
    val rotationData: StateFlow<Triple<Float, Float, Float>> get() = _rotationData

    init {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientationValues = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationValues)

            val azimuth = orientationValues[0].toDegrees() // 方位角
            val pitch = orientationValues[1].toDegrees()   // 俯仰角
            val roll = orientationValues[2].toDegrees()    // 滚动角

            viewModelScope.launch {
                _rotationData.emit(Triple(azimuth, pitch, roll))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 处理传感器的准确性变化（通常不需要）
    }

    override fun onCleared() {
        super.onCleared()
        // 确保在 ViewModel 销毁时取消传感器的监听
        sensorManager.unregisterListener(this)
    }

    private fun Float.toDegrees(): Float {
        return Math.toDegrees(this.toDouble()).toFloat()
    }
}

class SensorViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SensorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SensorViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}