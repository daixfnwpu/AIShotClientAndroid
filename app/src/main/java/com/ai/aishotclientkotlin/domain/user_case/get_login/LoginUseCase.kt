package com.ai.aishotclientkotlin.domain.user_case.get_login


import android.util.Log
import com.ai.aishotclientkotlin.data.repository.WordsRepository
import com.ai.aishotclientkotlin.domain.model.LoginModel
import com.ai.aishotclientkotlin.util.Resource
import com.ai.aishotclientkotlin.util.internetCheck
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class LoginUseCase @Inject constructor(

    private val repository: WordsRepository

) {

    operator fun invoke(Url: String, AppCode: String, PhoneNum: String, Password: String): Flow<Resource<LoginModel>> = flow {

        try {

            emit(Resource.Loading())

            val process = repository.userLogin(Url, AppCode, PhoneNum, Password)

            Log.e("LOG :::", process.success.toString())
            Log.e("LOG :::", process.message.toString())

            coroutineScope {

                emit(Resource.Success(process))

            }


        } catch (e: HttpException) {

            emit(Resource.Error(e.localizedMessage ?: "Beklenmedik bir hata oluştu!"))
            Log.e("LOG :::", e.localizedMessage)

        } catch (e: IOException) {

            if (!internetCheck()) {

                emit(Resource.Internet("Sunucuya ulaşılamadı. İnternet bağlantınızı kontrol ediniz!"))
                Log.e("LOG :::", e.localizedMessage)

            }
        }
    }
}