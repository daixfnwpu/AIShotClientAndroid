package com.ai.aishotclientkotlin

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.opengl.EGL14
import android.opengl.EGL14.EGL_DEFAULT_DISPLAY
import android.opengl.EGL14.EGL_NO_DISPLAY
import android.opengl.EGL14.eglDestroyContext
import android.opengl.EGL14.eglDestroySurface
import android.opengl.EGL14.eglGetDisplay
import android.opengl.EGL14.eglTerminate
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ai.aishotclientkotlin.data.ble.BLEManager
import com.ai.aishotclientkotlin.data.ble.BleAudioService
import com.ai.aishotclientkotlin.data.repository.DeviceProfileRepository
import com.ai.aishotclientkotlin.ui.nav.util.SetupNavGraph
import com.ai.aishotclientkotlin.ui.theme.AIShotClientKotlinTheme
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var mediaPlayer: MediaPlayer

    private var eglDisplay: EGLDisplay? = null
    private val eglContext: EGLContext? = null
    private val eglSurface: EGLSurface? = null

    @Inject
    lateinit var deviceProfileRepository: DeviceProfileRepository

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun checkConcurrentSupport(context: Context): Boolean = withContext(
        Dispatchers.IO) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val concurrentCameraIds = cameraManager.concurrentCameraIds
        concurrentCameraIds != null && concurrentCameraIds.size >= 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var isCheck = true
//        lifecycleScope.launch {
//            delay(3000L)
//            isCheck = false
//        }
//        installSplashScreen().apply {
//            setKeepOnScreenCondition { isCheck }
//        }




        // 检查设备是否支持双摄像头并发流
        var isConcurrentSupported = false

        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                isConcurrentSupported = checkConcurrentSupport(baseContext)
            }
        }
        lifecycleScope.launch {

            BLEManager.initialize(context =baseContext )
            BLEManager.setDeviceProfileRepository(deviceProfileRepository)
            BLEManager.reconnectAllBleDevice()
            Log.e("BLE","thougth BLEManger reconnect the Device")
            val glVersion = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion
            Log.e("OpenGL", "Supported OpenGL ES Version: $glVersion")

        }
        enableEdgeToEdge()
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
        setContent {
            navController = rememberNavController()
            AIShotClientKotlinTheme {
                SetupNavGraph(navController = navController)
            }
        }
       // if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
    //    }
        initEGL();
        val intent = Intent(this, BleAudioService::class.java)
        startService(intent)


    }


    private fun getComeraProperty() {
        try {
            val manager = getSystemService(CAMERA_SERVICE) as CameraManager
            val cameraId = manager.cameraIdList[0] // 获取主摄像头的ID
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val focalLengths =
                characteristics[CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS]
            if (focalLengths != null && focalLengths.size > 0) {
                val focalLength = focalLengths[0]
                Log.d("CameraInfo", "Focal Length: $focalLength mm")
            }

            // 获取传感器物理尺寸，单位为毫米
            val sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
            if (sensorSize != null) {
                val sensorWidth = sensorSize.width // 传感器宽度，单位为mm
                val sensorHeight = sensorSize.height // 传感器高度，单位为mm
                Log.d("CameraInfo", "Sensor Size: $sensorWidth mm x $sensorHeight mm")
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }
    private fun initEGL() {
        eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY)
        if (eglDisplay === EGL_NO_DISPLAY) {
            throw RuntimeException("Error: eglGetDisplay failed!")
        }
        val majorVersion = IntArray(1)  // 主版本号
        val minorVersion = IntArray(1)  // 次版本号
        if (!EGL14.eglInitialize(eglDisplay, majorVersion, 0, minorVersion, 0)) {
            throw RuntimeException("Error: eglInitialize failed!")
        }

    }
    private fun destroyEGL() {
        if(eglDisplay!=null) {
            eglDestroySurface(eglDisplay, eglSurface)
            eglDestroyContext(eglDisplay, eglContext)
            eglTerminate(eglDisplay)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyEGL()
        super.onDestroy()

        // Release the MediaPlayer when the activity is destroyed
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        try {
            BLEManager.disconnectAllDevice()
        }catch (e:Exception) {
            Log.e("BLE","${e.toString()}")
        }
    }


}


