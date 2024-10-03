package com.ai.aishotclientkotlin.ui.screens.home.screen

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File
import java.net.URL

@Composable
fun UploadMovieDialog(onDismiss: () -> Unit, onUpload: (String, List<Uri>, Uri?) -> Unit) {
    var movieDescription by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = uris
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 电影描述输入框
                Text(text = "Movie Description")
                BasicTextField(
                    value = movieDescription,
                    onValueChange = { movieDescription = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        if (movieDescription.isEmpty()) {
                            Text(text = "Enter movie description...")
                        }
                        innerTextField()
                    }
                )

                // 图片上传区域
                Text(text = "Upload Images (Max 9)")
                ImagePicker(selectedImages = selectedImages, onImagesPicked = { uris ->
                    if (uris.size <= 9) selectedImages = uris
                },pickImagesLauncher =pickImagesLauncher )

                // 视频上传区域
                Text(text = "Upload Video")
                VideoPicker(selectedVideo = selectedVideo, onVideoPicked = { uri ->
                    selectedVideo = uri
                }, pickVideoLauncher = pickImagesLauncher)

                // 上传按钮
                Button(
                    onClick = {
                        onUpload(movieDescription, selectedImages, selectedVideo)
                        onDismiss()
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
fun ImagePicker(
    selectedImages: List<Uri>,
    onImagesPicked: (List<Uri>) -> Unit,
    pickImagesLauncher: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>
) {
    Column {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedImages.size) { index ->
                val imageUri = selectedImages[index]
                CoilImage(
                    imageModel = {imageUri},
                    //contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                )
            }
            // 添加图片按钮
            item {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .clickable { pickImagesLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "+", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun VideoPicker(selectedVideo: Uri?, onVideoPicked: (Uri) -> Unit,
                pickVideoLauncher: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray)
            .clickable {
                pickVideoLauncher.launch("video/*")
                pickVideo(onVideoPicked)
                       },
        contentAlignment = Alignment.Center
    ) {
        if (selectedVideo != null) {

            CoilImage(
                imageModel = {selectedVideo},
                //contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray, RoundedCornerShape(8.dp)),
                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
            )
        } else {
            Text(text = "Pick a Video", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// 这里可以实现调用系统图片选择器的逻辑
private fun pickImages(onImagesPicked: (List<Uri>) -> Unit) {
    // TODO: 调用图片选择器并返回图片的 Uri 列表
}

// 这里可以实现调用系统视频选择器的逻辑
private fun pickVideo(onVideoPicked: (Uri) -> Unit) {
    // TODO: 调用视频选择器并返回视频的 Uri
}







@Composable
fun ImagePickerExample() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text(text = "选择图片")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedImageUri?.let { uri ->

            LoadImageFromNetwork(uri.toString())
        }
    }
}
@Composable
fun LoadImageFromNetwork(url: String) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    // 使用协程在后台加载图片
    LaunchedEffect(url) {
        imageBitmap = withContext(Dispatchers.IO) {
            val connection = URL(url).openStream()
            BitmapFactory.decodeStream(connection).asImageBitmap()
        }
    }

    // 如果图片加载完成则显示图片
    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}




