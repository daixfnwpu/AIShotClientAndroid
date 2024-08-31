package com.ai.aishotclientkotlin.ui.screens.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ai.aishotclientkotlin.ui.nav.bottombar.BottomBarNavigation
import com.ai.aishotclientkotlin.ui.nav.bottombar.BottomNavigation

@Composable
fun MainScreenPage(
    navControllerScaffold: NavController
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) }
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {

            BottomBarNavigation(navController = navController)

        }
    }
}
