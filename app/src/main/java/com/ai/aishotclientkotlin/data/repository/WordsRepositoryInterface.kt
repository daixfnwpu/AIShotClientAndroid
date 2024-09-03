package com.ai.aishotclientkotlin.data.repository

import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.ai.aishotclientkotlin.domain.model.login.LoginModel
import com.ai.aishotclientkotlin.domain.model.login.KelimelerModel
import com.ai.aishotclientkotlin.domain.model.login.LanguagesModel

interface WordsRepositoryInterface {
    suspend fun userLogin(url:String, appcode:String, email:String, password:String) : LoginModel

    suspend fun userRegister(Url:String, appcode:String, email:String, password:String) : CrudModel

    suspend fun listAllLanguage(Url: String, AppCode:String, userID:String) : LanguagesModel

    suspend fun listAllWords(Url: String, AppCode: String, UserLanguageID : String, UserID:String) : KelimelerModel
}
