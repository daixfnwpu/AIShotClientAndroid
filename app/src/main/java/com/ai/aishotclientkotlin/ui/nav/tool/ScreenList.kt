package com.ai.aishotclientkotlin.ui.nav.tool

import com.ai.aishotclientkotlin.R

const val ROOT_GRAPH_ROUTE = "root"
const val AUTH_GRAPH_ROUTE = "auth"
const val HOME_GRAPH_ROUTE = "home"
const val SPLASH_GRAPH_ROUTE = "splash"
const val SCAFFOLD_GRAPH_ROUTE = "scaffold"

sealed class ScreenList(

    val route: String,
    val title: Int? = null,
    val icon: Int? = null

) {
    //!! TODO and the Screens of shop,shot,setting.
    data object SplashScreen : ScreenList("Splash_Screen")
    object LoginScreen : ScreenList("Login_Screen")
    object SignInScreen : ScreenList("SignIn_Screen")
    object NoConnectionScreen : ScreenList("No_Connection_Screen")



    object MainScreen : ScreenList("Main_Screen",R.string.show, R.drawable.icon_youtube)
    object MovieDetailScreen : ScreenList("Movie_Detail_Screen")
    object ShotScreen : ScreenList("Shot_Screen",R.string.Shot, R.drawable.star)


    object ShopScreen : ScreenList("Shop_Screen",R.string.Shop, R.drawable.ic_favorite)

    object SettingScreen : ScreenList("Settings_Screen", R.string.Setting, R.drawable.ic_settings)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

}