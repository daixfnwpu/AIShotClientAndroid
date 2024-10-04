package com.ai.aishotclientkotlin.data.repository

import android.net.Uri
import android.util.Log
import com.ai.aishotclientkotlin.data.remote.UserService
import com.ai.aishotclientkotlin.dependencyinjection.AppModule
import com.ai.aishotclientkotlin.domain.model.login.AccessTokenModel
import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.ai.aishotclientkotlin.domain.model.login.LoginModel
import com.ai.aishotclientkotlin.domain.model.login.KelimelerModel
import com.ai.aishotclientkotlin.domain.model.login.LanguagesModel
import com.ai.aishotclientkotlin.domain.model.login.RefreshTokenModel
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserRepository @Inject constructor(

    private val api : UserService

) : UserRepositoryInterface {
    /// TODO Fix: return value is wrong : LoginModel(loginJSON=[LoginJSON(pstPhoneNum=13880766241, pstUserID=null, pstPassword=null), LoginJSON(pstPhoneNum=null, pstUserID=13880766241, pstPassword=null), LoginJSON(pstPhoneNum=null, pstUserID=null, pstPassword=bpktxxiv123)], message=Login successful, success=1)
    override suspend fun userLogin(url: String, appcode: String, phoneNum: String, password: String): LoginModel {
        return api.logInUser(url, appcode, phoneNum, password)
    }





    override suspend fun userRegister(url: String, appcode: String, phoneNum: String, password: String): CrudModel {
        return  api.signInUser(url, appcode, phoneNum, password)
    }

    /*
        POST /api/token/refresh/
        Content-Type: application/json

        {
            "refresh": "your_refresh_token_here"
        }
     */
    override suspend fun userRefreshToken(url: String,  refreshToken: RefreshTokenModel): AccessTokenModel {
        return  api.refreshToken(url, refreshToken);
    }

    override suspend fun listAllLanguage(url: String, appCode:String, userID: String): LanguagesModel {
        return api.listAllLanguages(url,appCode, userID)
    }

    override suspend fun listAllWords(
        Url: String,
        AppCode: String,
        UserLanguageID: String,
        UserID: String
    ): KelimelerModel {

        return api.listAllWords(Url, AppCode, UserLanguageID, UserID)

    }


}

// 扩展 Retrofit 回调为 suspend 函数
suspend fun <T> Call<T>.awaitResponse(): Response<T> {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : retrofit2.Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Exception) {
                // Ignore cancel exception
            }
        }
    }
}