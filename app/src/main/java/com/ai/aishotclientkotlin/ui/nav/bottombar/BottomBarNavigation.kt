package com.ai.aishotclientkotlin.ui.nav.bottombar

import androidx.compose.animation.ExperimentalAnimationApi

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.home.screen.MovieScreen


@Composable
fun BottomBarNavigation(

    navController: NavHostController

) {

    NavHost(navController = navController, startDestination = ScreenList.ListScreen.route) {
        //!!!!TODO ,,this is every important to set the pages;
        composable(ScreenList.ListScreen.route) {
           // ListPage(navController = navController)
            MovieScreen(navController = navController)
        }


        composable(ScreenList.AcademiaScreen.route) {
           // AcademiaPage()
        }
        composable(ScreenList.TimerScreen.route) {
          //  TimerPage()
        }

        composable(ScreenList.SettingsicScreen.route) {

          //  SettingsPage(navController)
        }

    }

}