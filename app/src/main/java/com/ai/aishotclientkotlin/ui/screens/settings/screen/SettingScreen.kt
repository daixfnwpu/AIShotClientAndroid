package com.ai.aishotclientkotlin.ui.screens.settings.screen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.ui.screens.home.screen.ImagePickerUI
import com.ai.aishotclientkotlin.ui.screens.settings.model.SettingViewModel
import com.ai.aishotclientkotlin.util.ui.NetworkImage
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.kmpalette.palette.graphics.Palette
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(
    navController: NavController?,
    viewModel: SettingViewModel = hiltViewModel(),
    // selectPoster: (MainScreenHomeTab, Long) -> Unit,
    //lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
  /*  var bitmapincludeConture: Bitmap? = null
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rubber)
    val conture = Conture(bitmap)
    val points = conture.getPointsOfContours()
    bitmapincludeConture = conture.getContourImage()
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
    }*/
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {

        UploadAvatarScreen(viewModel)
        UserAvatarScreen(viewModel)
    }

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

/*@Preview
@Composable
fun PreviewBitmapImageView() {
    val context = LocalContext.current
    // Load a sample Bitmap from resources or elsewhere
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.facedetect)

    ObjectDetectionScreen(bitmap)
}*/

@Composable
fun UploadAvatarScreen( viewModel: SettingViewModel) {

    val scope = rememberCoroutineScope()
    var palette = remember { mutableStateOf<Palette?>(null) }
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                ImagePickerUI(viewModel)
                NetworkImage(
                    networkUrl = viewModel.avatarUpdateUri.value,
                    circularReveal = 30,
                    modifier = Modifier
                        .height(30.dp),
                    palette = palette
                )
                Button(
                    onClick = {
                        Log.e("SETTINGSCREEN","Cliked")
                       viewModel.onImageSelected(viewModel.avatarUpdateUri.value)
                        Log.e("SETTINGSCREEN","Cliked!!!")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Upload Movie")
                }
            }

        }


    }
}
@Composable
fun UserAvatarScreen(viewModel: SettingViewModel) {

    var palette = remember { mutableStateOf<Palette?>(null) }
    NetworkImage(
        networkUrl = viewModel.avatarUrl.value,
        circularReveal = 30,
        modifier = Modifier
            .height(30.dp),
        palette = palette
    )

}