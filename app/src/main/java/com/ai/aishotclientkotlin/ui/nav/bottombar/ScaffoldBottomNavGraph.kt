package com.ai.aishotclientkotlin.ui.nav.bottombar


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ai.aishotclientkotlin.ui.nav.util.SCAFFOLD_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.util.ScreenList
import com.ai.aishotclientkotlin.ui.screens.MainScreenPage


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