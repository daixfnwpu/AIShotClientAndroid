package com.ai.aishotclientkotlin.ui.screens.entrance.splash.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.domain.user_case.get_login.LoginUseCase
import com.ai.aishotclientkotlin.ui.screens.entrance.splash.state.SplashState
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
class SplashViewModel @Inject constructor(

    private val LoginUseCase: LoginUseCase,
    @ApplicationContext val context: Context,

    ) : ViewModel() {

    private val _state = mutableStateOf(SplashState())
    val state: State<SplashState> = _state

    private var job: Job? = null

    fun getUserLogin(url: String, appCode: String, phoneNum: String, password: String) {

        if (url.isEmpty() && appCode.isEmpty() && phoneNum.isEmpty() && password.isEmpty()) {

            _state.value = SplashState(
                success = 4,
                error = "Values can't be empty!",
                isLoading = false
            )

            return
        }

        job?.cancel()

        job = viewModelScope.launch(Dispatchers.IO) {

            LoginUseCase(
                Url = url,
                AppCode = appCode,
                PhoneNum = phoneNum,
                Password = password
            ).onEach { result ->

                when (result) {

                    is Resource.Loading -> {
                        _state.value = SplashState(isLoading = true)

                    }

                    is Resource.Success -> {
                      //  delay(2250)

                        when (result.data?.success) {

                            0 -> {

                                _state.value = SplashState(
                                    isLoading = false,
                                    success = 0,
                                    internet = false,
                                    error = result.data.message
                                )

                            }

                            1 -> {

                                _state.value = state.value.copy(
                                    isLoading = false,
                                    internet = false,
                                    loginList = result.data.loginJSON,
                                    success = 1
                                )

                                SpManager(context).getThenSetSharedPreference(
                                    SpManager.Sp.JWT_TOKEN,
                                    result.data.access
                                )
                                SpManager(context).getThenSetSharedPreference(
                                    SpManager.Sp.REFRESH_TOKEN,
                                    result.data.refresh
                                )
                                SpManager(context).getThenSetSharedPreference(
                                    SpManager.Sp.USERID,
                                    result.data.userId
                                )




                            }

                            202 -> {

                                _state.value = SplashState(
                                    isLoading = false,
                                    success = 202,
                                    internet = false,
                                    error = "Unferistered Mail Adress!"
                                )

                            }

                            203 -> {

                                _state.value = SplashState(
                                    isLoading = false,
                                    success = 203,
                                    internet = false,
                                    error = "Wrong Password!"
                                )
                            }
                        }
                    }

                    is Resource.Internet -> {
                        delay(2000)

                        _state.value = SplashState(
                            internet = true,
                            isLoading = false
                        )

                    }

                    is Resource.Error -> {
                        delay(2000)

                        _state.value = SplashState(
                            error = result.message ?: "An unexpected error occurred",
                            isLoading = false
                        )

                    }
                }

            }.launchIn(viewModelScope)

        }

    }
}
