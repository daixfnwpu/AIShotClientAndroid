package com.ai.aishotclientkotlin

import android.app.ActivityManager
import android.content.Context
import android.opengl.EGL14
import android.opengl.EGL14.EGL_DEFAULT_DISPLAY
import android.opengl.EGL14.eglDestroyContext
import android.opengl.EGL14.eglDestroySurface
import android.opengl.EGL14.eglGetDisplay
import android.opengl.EGL14.eglTerminate
import android.opengl.EGL14.EGL_NO_DISPLAY
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
import com.ai.aishotclientkotlin.ui.nav.tool.SetupNavGraph
import com.ai.aishotclientkotlin.ui.theme.AIShotClientKotlinTheme
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

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
    }


}


