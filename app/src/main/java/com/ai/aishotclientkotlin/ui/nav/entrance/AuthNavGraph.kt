package com.ai.aishotclientkotlin.ui.nav.entrance

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ai.aishotclientkotlin.ui.nav.tool.AUTH_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.entrance.login.screen.LoginPage
import com.ai.aishotclientkotlin.ui.screens.entrance.signin.screen.SignInPage
import com.ai.aishotclientkotlin.util.NoConnectionPage

fun NavGraphBuilder.authNavGraph(
    navController: NavController
){

    navigation(startDestination = ScreenList.LoginScreen.route, route = AUTH_GRAPH_ROUTE) {

        composable(route = ScreenList.LoginScreen.route){
            LoginPage(navController = navController)
        }

        composable(route = ScreenList.LoginScreen.route + "/{phoneNum}", arguments = listOf(
            navArgument("phoneNum") {
                type = NavType.StringType
                defaultValue = "Null"
                nullable = true
            })
        ) {
            val phoneNum = it.arguments?.getString("phoneNum") ?: "Null"
            LoginPage(navController = navController, phoneNum = phoneNum)
        }

        composable(route = ScreenList.SignInScreen.route +"/{phoneNum}", arguments = listOf(
            navArgument("phoneNum") {
                type = NavType.StringType
                defaultValue = "Null"
                nullable = true
            }
        )
        ) {
            val phoneNum = it.arguments?.getString("phoneNum") ?: "Null"
            SignInPage(navController = navController, phoneNum = phoneNum)
        }

        composable(route = ScreenList.NoConnectionScreen.route){
            NoConnectionPage(navController = navController)
        }
    }
}