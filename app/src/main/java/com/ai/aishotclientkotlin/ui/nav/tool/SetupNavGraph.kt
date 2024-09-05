package com.ai.aishotclientkotlin.ui.nav.tool

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ai.aishotclientkotlin.ui.nav.bottombar.scaffoldNavGraph
import com.ai.aishotclientkotlin.ui.nav.entrance.authNavGraph
import com.ai.aishotclientkotlin.ui.nav.entrance.splashNavGraph

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = SPLASH_GRAPH_ROUTE,
        route = ROOT_GRAPH_ROUTE
    ) {
        authNavGraph(navController = navController)
        splashNavGraph(navController = navController)

        //!!TODO this Main Screen of APP
        scaffoldNavGraph(navController = navController)
    }
}