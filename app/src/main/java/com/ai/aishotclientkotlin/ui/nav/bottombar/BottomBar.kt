package com.ai.aishotclientkotlin.ui.nav.bottombar


import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ai.aishotclientkotlin.ui.theme.RedVisne
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
@Composable
fun BottomNavigation(navController: NavHostController) {

    val items = listOf(
        ScreenList.ListScreen,
        ScreenList.AcademiaScreen,
        ScreenList.TimerScreen,
        ScreenList.SettingsicScreen)

    NavigationBar (
        containerColor =  RedVisne,
        contentColor = Color.Black)
    {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon!!), contentDescription = item.title) },
                label = { Text(text = item.title!!, fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(selectedTextColor = Color.White, unselectedTextColor = Color.White.copy(0.4f)),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}