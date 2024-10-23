package com.ai.aishotclientkotlin.ui.screens.entrance.login.util

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import okhttp3.*
import java.io.IOException

class WXEntryActivity : Activity(), IWXAPIEventHandler {
    private lateinit var api: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, "your_wechat_appid", false)  // 替换为你的微信AppID
        api.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq?) {}

    override fun onResp(resp: BaseResp?) {
        if (resp?.type == ConstantsAPI.COMMAND_SENDAUTH) {
            val authResp = resp as SendAuth.Resp
            if (authResp.errCode == BaseResp.ErrCode.ERR_OK) {
                val code = authResp.code  // 微信返回的授权码 code
                sendCodeToServer(code)  // 将 code 发送到服务器
            }
        }
        finish()
    }

    private fun sendCodeToServer(code: String) {
        val url = "https://yourserver.com/wechat_login"  // 替换为你的服务器URL
        val client = OkHttpClient()
        val body = FormBody.Builder()
            .add("code", code)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WXEntryActivity", "Failed to send code to server: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    handleLoginResponse(responseData)  // 处理服务器返回的用户信息
                }
            }
        })
    }

    private fun handleLoginResponse(responseData: String?) {
        // 在这里解析服务器返回的数据并处理登录逻辑
        Log.d("WXEntryActivity", "Server response: $responseData")
        // 根据需要存储用户信息或进入主页面
    }
}
