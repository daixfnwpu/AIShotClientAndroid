package com.ai.aishotclientkotlin.ui.screens.entrance.splash.state

import com.ai.aishotclientkotlin.domain.model.LoginModel


data class SplashState (
    val isLoading : Boolean = false,
    val loginList : List<LoginModel.LoginJSON> = emptyList(),
    val success : Int = -1,
    val error : String = "",
    val internet :  Boolean = false
)