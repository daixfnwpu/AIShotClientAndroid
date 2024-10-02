package com.ai.aishotclientkotlin.ui.screens.home.screen

import android.view.SurfaceHolder
import android.view.SurfaceView
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ai.aishotclientkotlin.ui.theme.background
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithArrow
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilibiliVideoScreen(videoUrl: String,pressOnBack: () -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(background)
            .fillMaxSize(),
    ) {
        AppBarWithArrow("",showHeart = false, pressOnBack)
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true // 启用 JavaScript
                    settings.cacheMode = WebSettings.LOAD_DEFAULT

                    webViewClient = WebViewClient() // 使用 WebViewClient 避免打开外部浏览器

                    loadUrl(videoUrl) // 加载 Bilibili 视频的 URL
                }
            }
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IjkPlayerView(
    modifier: Modifier = Modifier,
    videoUrl: String,
    pressOnBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var mediaPlayer by remember { mutableStateOf<IjkMediaPlayer?>(null) }

    // 监听生命周期变化
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> mediaPlayer?.pause()
                Lifecycle.Event.ON_RESUME -> mediaPlayer?.start()
                Lifecycle.Event.ON_DESTROY -> {
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(background)
            .fillMaxSize(),
    ) {
        AppBarWithArrow("", showHeart = false, pressOnBack)
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            mediaPlayer = IjkMediaPlayer().apply {
                                setDisplay(holder)
                                dataSource = videoUrl
                                prepareAsync()

                                setOnPreparedListener {
                                    start()
                                }

                                setOnErrorListener { _, what, extra ->
                                    // 处理错误
                                    true
                                }
                            }
                        }

                        override fun surfaceChanged(
                            holder: SurfaceHolder,
                            format: Int,
                            width: Int,
                            height: Int
                        ) {
                            // 可选：处理表面变化
                        }

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            mediaPlayer?.release()
                            mediaPlayer = null
                        }
                    })
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExoPlayerScreen(modifier: Modifier = Modifier,
                    videoUrl: String,
                    pressOnBack: () -> Unit) {
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    // 使用 DisposableEffect 来管理 ExoPlayer 的生命周期
    DisposableEffect(
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(background)
                .fillMaxSize(),
        ) {
            AppBarWithArrow("", showHeart = false, pressOnBack)
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    val playerView = PlayerView(context).apply {
                        // 创建 ExoPlayer 实例
                        exoPlayer = ExoPlayer.Builder(context).build().also { player ->
                            // 设置播放的媒体资源
                            val mediaItem = MediaItem.fromUri(videoUrl)
                            player.setMediaItem(mediaItem)
                            player.prepare()
                            player.playWhenReady = true

                            this.player = player
                        }
                    }
                    playerView
                }
            )
        }
    ) {
        // 销毁时释放资源
        onDispose {
            exoPlayer?.release()
        }
    }
}