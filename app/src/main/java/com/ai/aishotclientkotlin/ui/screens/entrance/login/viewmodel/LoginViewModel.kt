package com.ai.aishotclientkotlin.ui.screens.entrance.login.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.domain.user_case.get_login.LoginUseCase
import com.ai.aishotclientkotlin.ui.screens.entrance.login.state.LoginState
import com.ai.aishotclientkotlin.util.Resource
import com.ai.aishotclientkotlin.util.SpManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    @ApplicationContext val context: Context,
) : ViewModel() {
    private val _state = mutableStateOf(LoginState())
    val  state: State<LoginState> = _state
    var job: Job? = null
    fun getUserLogin (url:String,appCode: String,phoneNum :String,password: String){
        if (url.trim().isEmpty() && appCode.trim().isEmpty() && phoneNum.trim().isEmpty()
            && password.trim().isEmpty())
        {
            _state.value = LoginState(error = "Value can't be empty!", isLoading = false)
            return
        }
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(Url = url, AppCode = appCode,PhoneNum = phoneNum,Password = password)
                .onEach { result ->
                    when(result){
                        is Resource.Loading -> {
                            _state.value = LoginState(isLoading = true, internet = false)
                        }
                        is Resource.Success -> {
                            when (result.data?.success) {

                                0 -> {

                                    _state.value = LoginState(
                                        isLoading = false,
                                        success = 0,
                                        internet = false,
                                        error = result.data.message
                                    )

                                }

                                1 -> {
                                    Log.e("loginJson: ",result.data.loginJSON.toString())
                                    for (i in result.data.loginJSON) {

                                        //!!TODO(why this context cannot be injected!!!!
                                        SpManager(context).getThenSetSharedPreference(
                                            SpManager.Sp.USERNAME,
                                            i.pstPhoneNum
                                        )
                                        SpManager(context).getThenSetSharedPreference(
                                            SpManager.Sp.PASSWORD,
                                            i.pstPassword
                                        )
                                        SpManager(context).getThenSetSharedPreference(
                                            SpManager.Sp.USERID,
                                            i.pstUserID
                                        )

                                    }

                                    _state.value = state.value.copy(
                                        isLoading = false,
                                        internet = false,
                                        loginList = result.data.loginJSON,
                                        success = 1
                                    )


                                }

                                202 -> {

                                    _state.value = LoginState(
                                        isLoading = false,
                                        success = 202,
                                        internet = false,
                                        error = "Unregistered PhoneNum Address"
                                    )


                                }

                                203 -> {

                                    _state.value = LoginState(
                                        isLoading = false,
                                        success = 203,
                                        internet = false,
                                        error = "Wrong Password"
                                    )
                                }
                            }
                        }
                        is Resource.Internet -> {
                            delay(200)
                            _state.value = LoginState(
                                internet = true,
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            delay(200)
                            _state.value = LoginState(
                                error = result.message ?: "An unexpected error occurred",
                                isLoading = false
                            )
                        }
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun clearViewModel() {
        state.value.internet = false
        state.value.isLoading = false
        state.value.success = -1
        state.value.loginList = emptyList()
        state.value.error = ""
    }
}
