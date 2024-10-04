package com.ai.aishotclientkotlin.domain.user_case.get_sigin



import com.ai.aishotclientkotlin.data.repository.UserRepository
import com.ai.aishotclientkotlin.domain.model.login.CrudModel
import com.ai.aishotclientkotlin.util.Resource
import com.ai.aishotclientkotlin.util.internetCheck
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
class SignInUseCase @Inject constructor(

    private var repository: UserRepository

) {

    operator fun invoke(Url: String, AppCode: String, PhoneNum: String, Password: String): Flow<Resource<CrudModel>> = flow {

        try {

            emit(Resource.Loading())

            val process = repository.userRegister(Url, AppCode, PhoneNum, Password)

            coroutineScope {

                emit(Resource.Success(process))

            }

        } catch (e: HttpException) {

            emit(Resource.Error(e.localizedMessage ?: e.toString()))

        } catch (e: IOException) {

            if (!internetCheck()) {

                emit(Resource.Internet(e.toString()))

            }
        }
    }
}