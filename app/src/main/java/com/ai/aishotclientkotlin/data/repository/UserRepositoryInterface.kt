package com.ai.aishotclientkotlin.data.repository

import android.net.Uri
import com.ai.aishotclientkotlin.domain.model.login.AccessTokenModel
import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.ai.aishotclientkotlin.domain.model.login.LoginModel
import com.ai.aishotclientkotlin.domain.model.login.KelimelerModel
import com.ai.aishotclientkotlin.domain.model.login.LanguagesModel
import com.ai.aishotclientkotlin.domain.model.login.RefreshTokenModel
import kotlinx.coroutines.flow.Flow

interface UserRepositoryInterface {
    suspend fun userLogin(url:String, appcode:String, email:String, password:String) : LoginModel
    suspend fun userRegister(Url:String, appcode:String, email:String, password:String) : CrudModel
    suspend fun userRefreshToken(url: String,  refreshToken: RefreshTokenModel): AccessTokenModel
    suspend fun listAllLanguage(Url: String, AppCode:String, userID:String) : LanguagesModel

    suspend fun listAllWords(Url: String, AppCode: String, UserLanguageID : String, UserID:String) : KelimelerModel




}
