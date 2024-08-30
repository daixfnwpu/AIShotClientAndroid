package com.ai.aishotclientkotlin.ui.screens.entrance.login.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.domain.user_case.get_login.LoginUseCase
import com.ai.aishotclientkotlin.ui.screens.entrance.login.state.LoginState
import com.ai.aishotclientkotlin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

                        }
                        is Resource.Internet -> {

                        }
                        is Resource.Error -> {

                        }
                    }
                }.launchIn(viewModelScope)
        }
    }
}
