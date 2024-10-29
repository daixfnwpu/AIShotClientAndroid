package com.ai.aishotclientkotlin.ui.screens.game.screen

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.data.sensor.SensorViewModel
import com.ai.aishotclientkotlin.data.sensor.SensorViewModelFactory
import com.ai.aishotclientkotlin.engine.mediapipe.EyesDetected
import com.ai.aishotclientkotlin.engine.mediapipe.HandsDetected
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.rememberEngine


@Composable
fun ARSceneView(navController: NavController, viewModel: SensorViewModel = viewModel()) {
    val engine = rememberEngine()

    val context = LocalContext.current
    val materialLoader = MaterialLoader(engine, context = context)
    val appContext = LocalContext.current.applicationContext as Application

    var handsDetected by remember {
        mutableStateOf(HandsDetected(context).apply {
            init() // 在这里初始化 HandsDetected
        })
    }

    var eyesDetected by remember {
        mutableStateOf(EyesDetected(context).apply {
            init() // 在这里初始化 HandsDetected
        })
    }
// 使用 DisposableEffect 进行生命周期管理
    DisposableEffect(Unit) {
        // 开始 EyesDetected 操作
        onDispose {
            // 清理资源或停止操作，如关闭摄像头或停止检测
            handsDetected.release() // 你可以定义 stop() 方法来处理清理
            eyesDetected.release()
        }
    }
    // 创建 ViewModel 并传递自定义工厂
    val viewModel: SensorViewModel = viewModel(
        factory = SensorViewModelFactory(appContext)
    )
    val sensorData by viewModel.rotationData.collectAsState()

    var lastOpenHand = remember {
        handsDetected.isOpenHandleState.value;
    }

    val stateValue by handsDetected.isOpenHandleState
    LaunchedEffect(stateValue) {

        Log.e("AR", "stateValue is ${stateValue},lastOpenHand is : ${lastOpenHand}")

        if (stateValue == true && lastOpenHand == false) {
            Log.e("AR", "右手打开")
            val distancebetweeneyeandhand = calDistanceTwoMark(eyesDetected.rigthEyeCenterState.value, handsDetected.thumbAndIndexCenterState.value)

            Log.e("AR", "distancebetweeneyeandhand is : ${distancebetweeneyeandhand}")
            val realDistance= 6.5 * distancebetweeneyeandhand / eyesDetected.distanceBetweenTwoEye.value
            Log.e("AR", "realDistanceis : ${realDistance}cm")
            Log.e("AR", "power is  : ${handsDetected.calShotVelocity(handsDetected.powerSlubber.value)}")
            Log.e("AR", "isoscelesTriangle is : ${handsDetected.isoscelesTriangle}cm")
            Log.e("AR", "shotAngle is : ${handsDetected.shotAngle}cm")

            Log.e("AR", "handsDetected.thumbAndIndexCenterState is : ${handsDetected.thumbAndIndexCenterState}")

            lastOpenHand = true
        }else if(stateValue == false)
        {
            Log.e("AR", "右手握紧")
            lastOpenHand = false
        }else
        {
            Log.e("AR","其他状态")
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {


        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Azimuth: ${sensorData.first}")
            Text(text = "Pitch: ${sensorData.second}")
            Text(text = "Roll: ${sensorData.third}")
        }
        StartVRGame(handsDetected, eyesDetected, modifier = Modifier,showDrawLandmark = true)
    }
}