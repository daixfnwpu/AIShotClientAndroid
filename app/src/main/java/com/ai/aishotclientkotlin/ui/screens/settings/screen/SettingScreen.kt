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
import com.ai.aishotclientkotlin.engine.mlkt.ObjectDetectionScreen
import com.ai.aishotclientkotlin.engine.opencv.Conture

import com.ai.aishotclientkotlin.ui.screens.settings.model.SettingViewModel
import com.ai.aishotclientkotlin.ui.screens.shot.screen.AiShotSceneView
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
    Log.d("Conture",points.toString())
    if (points != null)
        IsoscelesTriangle.findAdjustDirection(points,conture.getImageWidth())

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        AiShotSceneView(modifier = Modifier.weight(1f).fillMaxSize())
        HandGestureRecognitionUI(modifier= Modifier.weight(1f).fillMaxSize())
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