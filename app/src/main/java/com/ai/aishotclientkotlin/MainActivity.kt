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
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ai.aishotclientkotlin.data.ble.BLEManager
import com.ai.aishotclientkotlin.data.ble.BleService
import com.ai.aishotclientkotlin.ui.nav.tool.SetupNavGraph
import com.ai.aishotclientkotlin.ui.theme.AIShotClientKotlinTheme
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var mediaPlayer: MediaPlayer

    private var eglDisplay: EGLDisplay? = null
    private val eglContext: EGLContext? = null
    private val eglSurface: EGLSurface? = null

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
        lifecycleScope.launch {

            BLEManager.initialize(context =baseContext )
            BLEManager.reconnectLastDevice()
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
        val intent = Intent(this, BleService::class.java)
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
        eglDestroySurface(eglDisplay, eglSurface)
        eglDestroyContext(eglDisplay, eglContext)
        eglTerminate(eglDisplay)
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
            BLEManager.disconnect()
        }catch (e:Exception) {
            Log.e("BLE","${e.toString()}")
        }
    }


}


