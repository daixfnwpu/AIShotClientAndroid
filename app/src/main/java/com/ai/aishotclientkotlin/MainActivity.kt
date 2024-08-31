package com.ai.aishotclientkotlin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ai.aishotclientkotlin.ui.theme.AIShotClientKotlinTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ai.aishotclientkotlin.ui.nav.tool.SetupNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isCheck = true
        lifecycleScope.launch {
            delay(3000L)
            isCheck = false
        }
        installSplashScreen().apply {
            setKeepOnScreenCondition { isCheck }
        }
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            AIShotClientKotlinTheme {
                SetupNavGraph(navController = navController)
            }
        }
    }
}


