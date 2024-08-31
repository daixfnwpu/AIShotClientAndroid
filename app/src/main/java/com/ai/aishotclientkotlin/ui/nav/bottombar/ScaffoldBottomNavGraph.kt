package com.ai.aishotclientkotlin.ui.nav.bottombar


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ai.aishotclientkotlin.ui.nav.tool.SCAFFOLD_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.home.MainScreenPage


fun NavGraphBuilder.scaffoldNavGraph(
    navController: NavController
){

    navigation(startDestination = ScreenList.MainScreen.route,
        SCAFFOLD_GRAPH_ROUTE){

        composable(ScreenList.MainScreen.route){

            MainScreenPage(navControllerScaffold = navController)
        }
    }
}