package com.ai.aishotclientkotlin.ui.nav.bottombar


import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ai.aishotclientkotlin.ui.theme.RedVisne
import com.ai.aishotclientkotlin.ui.nav.util.ScreenList
@Composable
fun BottomNavigation(navController: NavHostController) {

    val items = listOf(
        ScreenList.MainScreen,
        ScreenList.ShotScreen,
        ScreenList.GameScreen,
        ScreenList.ShopScreen,
        ScreenList.SettingScreen)

    NavigationBar (
        containerColor =  RedVisne,
       // modifier = Modifier.height(30.dp),
        contentColor = Color.Black)
    {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon!!), contentDescription = stringResource(item.title!!), modifier = Modifier.size(15.dp)) },
                label = { Text(text = stringResource(item.title!!), fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(selectedTextColor = Color.White, unselectedTextColor = Color.White.copy(0.4f)),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {

                        navController.graph.startDestinationRoute?.let { screenroute ->
                            Log.e("bottomBar",screenroute)
                            popUpTo(screenroute) {
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