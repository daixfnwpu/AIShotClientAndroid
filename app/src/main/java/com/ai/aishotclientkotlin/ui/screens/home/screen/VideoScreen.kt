package com.ai.aishotclientkotlin.ui.screens.home.screen

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BilibiliVideoScreen(videoUrl: String) {
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