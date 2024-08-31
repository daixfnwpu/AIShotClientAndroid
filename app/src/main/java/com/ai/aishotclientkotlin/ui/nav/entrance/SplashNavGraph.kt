package com.ai.aishotclientkotlin.ui.nav.entrance

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.ai.aishotclientkotlin.ui.nav.tool.SPLASH_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.entrance.splash.screen.SplashPage


fun NavGraphBuilder.splashNavGraph(
    navController: NavController
){

    navigation(startDestination = ScreenList.SplashScreen.route, route = SPLASH_GRAPH_ROUTE) {

        composable(route = ScreenList.SplashScreen.route){
            SplashPage(navController = navController, hiltViewModel())
        }

    }

}