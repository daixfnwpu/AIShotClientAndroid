package com.ai.aishotclientkotlin.util

import android.app.Application
import android.util.Log
import com.google.android.filament.Filament
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
       // System.loadLibrary("mediapipe_jni")
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Initialization failed")
        } else {
            Log.d("OpenCV", "Initialization succeeded")
        }
        Filament.init()
    }
}