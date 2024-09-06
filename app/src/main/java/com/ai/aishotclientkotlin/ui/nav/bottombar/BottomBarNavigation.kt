package com.ai.aishotclientkotlin.ui.nav.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.entrance.signin.screen.SignInPage
import com.ai.aishotclientkotlin.ui.screens.home.screen.MovieDetailScreen
import com.ai.aishotclientkotlin.ui.screens.home.screen.MovieScreen
import com.ai.aishotclientkotlin.ui.screens.settings.screen.SettingScreen
import com.ai.aishotclientkotlin.ui.screens.shop.screen.ShopScreen
import com.ai.aishotclientkotlin.ui.screens.shot.screen.ShotScreen


@Composable
fun BottomBarNavigation(
    navController: NavHostController
) {
   // val navController = remember { mutableStateOf<NavHostController?>(null) }
    NavHost(navController = navController, startDestination = ScreenList.MainScreen.route) {
        //!!!!TODO ,,this is every important to set the pages;
        composable(ScreenList.MainScreen.route) {
           // ListPage(navController = navController)
            MovieScreen(navController = navController)
        }
//        composable(route = ScreenList.SignInScreen.route +"/{phoneNum}", arguments = listOf(
//            navArgument("phoneNum") {
//                type = NavType.StringType
//                defaultValue = "Null"
//                nullable = true
//            }
//        )
//        ) {
//            val phoneNum = it.arguments?.getString("phoneNum") ?: "Null"
//            SignInPage(navController = navController, phoneNum = phoneNum)
//        }

        composable(
            route =ScreenList.MovieDetailScreen.route +"/{movieId}" ,
            arguments = listOf(
                navArgument("movieId") {  type = NavType.LongType}
            )
        ) { backStackEntry ->

            val posterId =
                backStackEntry.arguments?.getLong("movieId")
                    ?: return@composable

            MovieDetailScreen(posterId, hiltViewModel()) {
                navController.navigateUp()
            }
        }

        composable(ScreenList.ShotScreen.route) {
            ShotScreen(navController = navController)
        }
        composable(ScreenList.ShopScreen.route) {
           ShopScreen(navController = navController)
        }

        composable(ScreenList.SettingScreen.route) {

           SettingScreen(navController = navController)
        }

    }

}