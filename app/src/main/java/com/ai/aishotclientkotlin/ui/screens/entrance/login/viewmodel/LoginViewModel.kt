package com.ai.aishotclientkotlin.ui.screens.entrance.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ai.aishotclientkotlin.domain.user_case.get_login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    @ApplicationContext val context: Context,
) : ViewModel(){

}
