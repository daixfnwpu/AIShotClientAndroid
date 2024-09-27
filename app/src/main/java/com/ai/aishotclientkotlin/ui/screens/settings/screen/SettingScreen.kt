package com.ai.aishotclientkotlin.ui.screens.settings.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.engine.IsoscelesTriangle
import com.ai.aishotclientkotlin.engine.ar.EyesDetected
import com.ai.aishotclientkotlin.engine.ar.HandsDetected
import com.ai.aishotclientkotlin.engine.mlkt.ObjectDetectionScreen
import com.ai.aishotclientkotlin.engine.opencv.Conture

import com.ai.aishotclientkotlin.ui.screens.settings.model.SettingViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.screen.HandGestureRecognitionUI


@Composable
fun SettingScreen(
    navController: NavController?,
    viewModel: SettingViewModel = hiltViewModel(),
    // selectPoster: (MainScreenHomeTab, Long) -> Unit,
    //lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    var bitmapincludeConture: Bitmap? = null
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rubber)
    val conture = Conture(bitmap)
  //  val points = conture.getContours()?.get(0)?.toList() // return 6 points;
    val points = conture.getPointsOfContours()
    bitmapincludeConture = conture.getContourImage()
    Log.e("Conture",points.toString())

    var handsDetected by remember {
        mutableStateOf(HandsDetected(context).apply {
            init() // 在这里初始化 HandsDetected
        })
    }



    if (points != null)
        IsoscelesTriangle.findAdjustDirection(points,conture.getImageWidth())

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


    var eyesmarksState by eyesDetected.eyesmarksState

    var lastOpenHand = handsDetected.isOpenHandleState.value;

    LaunchedEffect(handsDetected.isOpenHandleState) {
        if (handsDetected.isOpenHandleState.value == true && lastOpenHand ==false) {
            Log.d("AR", "右手打开")
            val distancebetweeneyeandhand =eyesDetected.rigthEyeCenterState.value.y - handsDetected.thumbAndIndexCenterState.value.y
            Log.d("AR", "distancebetweeneyeandhand is : ${distancebetweeneyeandhand}")
            lastOpenHand = true
        }else if(handsDetected.isOpenHandleState.value == false)
        {
            Log.d("AR", "右手握紧")
            lastOpenHand = false
        }
    }
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
       // AiShotSceneView(modifier = Modifier.weight(1f).height(400.dp))
        HandGestureRecognitionUI(handsDetected,eyesDetected,modifier= Modifier
            .weight(1f)
            .fillMaxSize())
    }

   // Surface(modifier = Modifier.fillMaxSize()) {

            //    RadiusComboBox(radius,"raduis")
            // SliderWithTextField()
//            if (bitmapincludeConture != null) {
//                BitmapImageView(bitmapincludeConture)
//            }
       // PlotTrajectory()
          //  ObjectDetectionScreen()
          //  ObjectDetectionDemo()
           // detectFaceContours(bitmap)

   // }

}

@Composable
fun BitmapImageView(bitmap: Bitmap) {
    // Convert Bitmap to ImageBitmap
    val imageBitmap: ImageBitmap = bitmap.asImageBitmap()

    // Display the image
    Image(
        bitmap = imageBitmap,
        contentDescription = "Description of the image",
        modifier = Modifier.size(200.dp) // Set size or other modifiers as needed
    )
}

@Preview
@Composable
fun PreviewBitmapImageView() {
    val context = LocalContext.current
    // Load a sample Bitmap from resources or elsewhere
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.facedetect)

    ObjectDetectionScreen(bitmap)
}