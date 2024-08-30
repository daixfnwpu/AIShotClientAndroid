package com.ai.aishotclientkotlin.ui.screens.entrance.login.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.ui.screens.entrance.login.viewmodel.LoginViewModel
import dagger.hilt.android.HiltAndroidApp


@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    phoneNum: String? = null
)
{
    val state = viewModel.state.value

}