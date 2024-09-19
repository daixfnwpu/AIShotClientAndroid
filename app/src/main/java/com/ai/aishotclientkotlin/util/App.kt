package com.ai.aishotclientkotlin.util

import android.app.Application
import android.util.Log
import com.ai.aishotclientkotlin.data.shotclient.BLEManager
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader
import timber.log.Timber
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // 在开发模式下初始化 Timber
       // if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
     //   }
       // System.loadLibrary("native-lib")
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Initialization failed")
        } else {
            Log.d("OpenCV", "Initialization succeeded")
        }
    }


}