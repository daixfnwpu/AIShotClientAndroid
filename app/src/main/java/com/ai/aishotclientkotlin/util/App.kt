package com.ai.aishotclientkotlin.util

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
       // System.loadLibrary("native-lib")
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Initialization failed")
        } else {
            Log.d("OpenCV", "Initialization succeeded")
        }
      //  external fun processImage(imageAddr: Long)
    }
}