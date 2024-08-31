package com.ai.aishotclientkotlin.data.repository

import com.ai.aishotclientkotlin.data.remote.WordsApi
import com.ai.aishotclientkotlin.domain.model.CrudModel
import com.ai.aishotclientkotlin.domain.model.LoginModel
import com.haznedar.kelimedefterim.model.KelimelerModel
import com.haznedar.kelimedefterim.model.LanguagesModel
import javax.inject.Inject

class WordsRepository @Inject constructor(

    private val api : WordsApi

) : WordsRepositoryInterface {

    override suspend fun userLogin(url: String, appcode: String, email: String, password: String): LoginModel {
        return api.logInUser(url, appcode, email, password)
    }

    override suspend fun userRegister(url: String, appcode: String, email: String, password: String): CrudModel {
        return  api.signInUser(url, appcode, email, password)
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