package com.ai.aishotclientkotlin.ui.screens.settings.screen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.inputmethodservice.Keyboard
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.home.screen.ImagePickerUI
import com.ai.aishotclientkotlin.ui.screens.settings.model.SettingViewModel
import com.ai.aishotclientkotlin.ui.screens.settings.model.UserProfileViewModel
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
        UserProfileDisplayScreen(onSave = {}, onCancel = {}, onNavigateToSettings = {
            navController?.navigate(
                ScreenList.SettingModifyScreen.withArgs()
            )
        })
//        UploadAvatarScreen(viewModel)
//        UserAvatarScreen(viewModel)
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
@Composable
fun UploadAvatarScreen(viewModel: UserProfileViewModel) {

    val scope = rememberCoroutineScope()
    var palette = remember { mutableStateOf<Palette?>(null) }
    var showDialog = remember { mutableStateOf(true) }  // 控制对话框是否显示

    if (showDialog.value) {  // 当 showDialog 为 true 时显示 Dialog
        Dialog(onDismissRequest = { showDialog.value = false }) {  // 点击外部关闭
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 400.dp)
                    .heightIn(max = 300.dp) // 限制最大宽度
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 确保 ImagePickerUI 能够正常选择图片
                    ImagePickerUI(viewModel)

                    // 显示网络图片
                    NetworkImage(
                        networkUrl = viewModel.avatarUpdateUri.value,
                        circularReveal = 30,
                        modifier = Modifier
                            .height(100.dp)  // 更新高度，适合图片展示
                            .width(100.dp),  // 可设置宽度
                        palette = palette
                    )

                    Spacer(modifier = Modifier.height(16.dp))  // 增加间距

                    // 上传按钮
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                viewModel.onImageSelected(viewModel.avatarUpdateUri.value)
                                // 上传后关闭对话框
                                showDialog.value = false  // 点击上传后关闭对话框
                            },
                            modifier = Modifier
                        ) {
                            Text(text = "Upload")  // 修改按钮文字
                        }

                        Spacer(modifier = Modifier.weight(1.0f))  // 增加间距
                        Button(
                            onClick = {
                                showDialog.value = false  // 点击后关闭对话框
                            },
                            modifier = Modifier
                        ) {
                            Text(text = "Close")  // 设置关闭按钮文字
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun UserAvatarScreen(viewModel: UserProfileViewModel) {

    var palette = remember { mutableStateOf<Palette?>(null) }
    NetworkImage(
        networkUrl = viewModel.avatarUrl,
        circularReveal = 30,
        modifier = Modifier
            .height(30.dp),
        palette = palette
    )

}