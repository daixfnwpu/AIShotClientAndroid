package com.ai.aishotclientkotlin.ui.nav.bottombar


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ai.aishotclientkotlin.ui.nav.tool.SCAFFOLD_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.main.MainScreenPage


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