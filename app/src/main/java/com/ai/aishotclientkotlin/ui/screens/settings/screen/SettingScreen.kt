package com.ai.aishotclientkotlin.ui.screens.settings.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.engine.IsoscelesTriangle
import com.ai.aishotclientkotlin.engine.mlkt.ObjectDetectionDemo
import com.ai.aishotclientkotlin.engine.mlkt.ObjectDetectionScreen
import com.ai.aishotclientkotlin.engine.mlkt.detectFaceContours
import com.ai.aishotclientkotlin.engine.opencv.Conture

import com.ai.aishotclientkotlin.ui.screens.settings.model.SettingViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.model.ShotViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.screen.PlotTrajectory
import com.ai.aishotclientkotlin.ui.screens.shot.screen.RajawaliInCompose


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
    Log.d("Conture",points.toString())
    if (points != null)
        IsoscelesTriangle.findAdjustDirection(points,conture.getImageWidth())
   // Surface(modifier = Modifier.fillMaxSize()) {
    RajawaliInCompose(context)
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