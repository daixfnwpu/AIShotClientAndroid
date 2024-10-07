package com.ai.aishotclientkotlin.ui.nav.bottombar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.entrance.signin.screen.SignInPage
import com.ai.aishotclientkotlin.ui.screens.home.screen.BilibiliVideoScreen
import com.ai.aishotclientkotlin.ui.screens.home.screen.ExoPlayerScreen
import com.ai.aishotclientkotlin.ui.screens.home.screen.FullScreenImageCarousel
import com.ai.aishotclientkotlin.ui.screens.home.screen.IjkPlayerView
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

        composable(route = ScreenList.VideoScreen.route + "/{videoSite}", arguments = listOf(
            navArgument("videoSite") {
                type = NavType.StringType
                defaultValue = "Null"
                nullable = true
            })
        ) {
            var videoid = it.arguments?.getString("videoSite") ?: "Null"
//            BilibiliVideoScreen(Api.getBilibiliVideoPath(videoSite)){
//                navController.navigateUp()
//            }

            //TODO : for test: 905833761.mp4
          //  videoid = "905833761.mp4"
            ExoPlayerScreen(modifier = Modifier.fillMaxSize(), videoUrl = Api.getMySiteVideoPath(videoid))
            {
                navController.navigateUp()
            }
        }

        composable(route = ScreenList.PhotoCarouselScreen.route + "/{imageUrls}", arguments = listOf(
            navArgument("imageUrls") {
                type = NavType.StringType
                defaultValue = "assets/image/placeholder_image.png"
                nullable = true
            })
        ) {
            val imageUrlString = it.arguments?.getString("imageUrls") ?: ""

            // 将逗号分隔的字符串解析为数组
            // 判断是否是默认的占位符图片
            val imageUrls = if (imageUrlString == "assets/image/placeholder_image.png") {
                listOf(imageUrlString)  // 返回包含占位图片的列表
            } else if (imageUrlString.isNotEmpty()) {
                imageUrlString.split(",")  // 分割逗号分隔的字符串为图片列表
            } else {
                emptyList()  // 如果为空，返回空列表
            }

            FullScreenImageCarousel(imageUrls) {
                navController.navigateUp()
            }
        }






        composable(
            route =ScreenList.MovieDetailScreen.route +"/{movieId}" ,
            arguments = listOf(
                navArgument("movieId") {  type = NavType.LongType}
            )
        ) { backStackEntry ->

            val posterId =
                backStackEntry.arguments?.getLong("movieId")
                    ?: return@composable

            MovieDetailScreen(navController,posterId) {
                navController.navigateUp()
            }
        }
        // TODO ,到底是在这里构造路由？还是在其他地方？
       /* composable(route = ScreenList.VideoScreen.route + "/{movieUrl}",
            arguments = listOf(
                navArgument("movieUrl") {  type = NavType.StringType}
            )
            ) { entry ->
            var movieUrl =
                entry.arguments?.getString("movieUrl")
                    ?: return@composable


            // TODO FOR TEST : movieUrl
            movieUrl= "https://www.bilibili.com/video/BV1724Re5EY8?t=5.61"
            BilibiliVideoScreen(movieUrl)
        }*/

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