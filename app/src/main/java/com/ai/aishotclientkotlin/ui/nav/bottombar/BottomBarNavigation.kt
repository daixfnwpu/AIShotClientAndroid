package com.ai.aishotclientkotlin.ui.nav.bottombar

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.home.screen.MovieScreen
import com.ai.aishotclientkotlin.ui.screens.settings.screen.SettingScreen
import com.ai.aishotclientkotlin.ui.screens.shop.screen.ShopScreen
import com.ai.aishotclientkotlin.ui.screens.shot.screen.ShotScreen


@Composable
fun BottomBarNavigation(

    navController: NavHostController

) {

    NavHost(navController = navController, startDestination = ScreenList.MainScreen.route) {
        //!!!!TODO ,,this is every important to set the pages;
        composable(ScreenList.MainScreen.route) {
           // ListPage(navController = navController)
            MovieScreen(navController = navController)
        }

        composable(ScreenList.ShotScreen.route) {
            ShotScreen(navController = navController)
        }
        composable(ScreenList.ShotScreen.route) {
           ShopScreen(navController = navController)
        }

        composable(ScreenList.SettingScreen.route) {

           SettingScreen(navController = navController)
        }

    }

}