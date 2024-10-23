package com.ai.aishotclientkotlin.ui.screens.entrance.login.util

import android.content.Context
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory

@Composable
fun WeChatLoginButton() {
    val context = LocalContext.current
    Button(onClick = { loginWithWeChat(context) }) {
        Text("微信")
    }
}

fun loginWithWeChat(context: Context) {
    val api = WXAPIFactory.createWXAPI(context, "your_wechat_appid", true)  // 替换为你的微信AppID
    if (!api.isWXAppInstalled) {
        Toast.makeText(context, "微信没有安装", Toast.LENGTH_SHORT).show()
        return
    }

    val req = SendAuth.Req()
    req.scope = "snsapi_userinfo"
    req.state = "wechat_sdk_demo"
    api.sendReq(req)
}
