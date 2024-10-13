package com.ai.aishotclientkotlin.ui.nav.tool

import com.ai.aishotclientkotlin.R

const val ROOT_GRAPH_ROUTE = "root"
const val AUTH_GRAPH_ROUTE = "auth"
const val HOME_GRAPH_ROUTE = "home"
const val SPLASH_GRAPH_ROUTE = "splash"
const val SCAFFOLD_GRAPH_ROUTE = "scaffold"
const val VIDEO_GRAPH_ROUTE = "video"
sealed class ScreenList(

    val route: String,
    val title: Int? = null,
    val icon: Int? = null

) {
    //!! TODO and the Screens of shop,shot,setting.
    data object SplashScreen : ScreenList("Splash_Screen")
    data object LoginScreen : ScreenList("Login_Screen")
    data object SignInScreen : ScreenList("SignIn_Screen")
    data object NoConnectionScreen : ScreenList("No_Connection_Screen")



    data object MainScreen : ScreenList("Main_Screen",R.string.show, R.drawable.icon_youtube)
    data object MovieDetailScreen : ScreenList("Movie_Detail_Screen")
    data object VideoScreen: ScreenList("Video_Screen")
    data object PhotoCarouselScreen: ScreenList("PhotoCarousel_Screen")
    data object ShotConfigDetailScreen: ScreenList("ShotConfigDetail_Screen")
    data object ShotScreen : ScreenList("Shot_Screen",R.string.Shot, R.drawable.star)

    data object FilterableExcelWithAdvancedFiltersScreen: ScreenList("FilterableExcelWithAdvancedFilters_Screen")

    data object ShopScreen : ScreenList("Shop_Screen",R.string.Shop, R.drawable.ic_favorite)

    data object SettingScreen : ScreenList("Settings_Screen", R.string.Setting, R.drawable.ic_settings)
    data object SettingModifyScreen : ScreenList("Settings_Modify_Screen")
    data object GameScreen : ScreenList("Game_Screen",R.drawable.ic_time)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

}