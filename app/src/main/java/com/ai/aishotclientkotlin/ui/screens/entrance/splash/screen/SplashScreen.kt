package com.ai.aishotclientkotlin.ui.screens.entrance.splash.screen
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.ui.nav.tool.AUTH_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.tool.SCAFFOLD_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.entrance.splash.viewmodel.SplashViewModel
import com.ai.aishotclientkotlin.ui.theme.LoginScreenTheme
import com.ai.aishotclientkotlin.ui.theme.RedVisne
import com.ai.aishotclientkotlin.util.Constants
import com.ai.aishotclientkotlin.util.SpManager
import com.airbnb.lottie.compose.*
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.components.rememberImageComponent

@Composable
fun SplashPage(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val state = viewModel.state.value

    val context = LocalContext.current

    val phoneNum = SpManager(context).getSharedPreference( SpManager.Sp.USERNAME, "Null").toString()

    val pass = SpManager(context).getSharedPreference( SpManager.Sp.PASSWORD, "Null").toString()

    LaunchedEffect(key1 = Unit) {

        viewModel.getUserLogin(Api.LOGIN, Constants.TYPETWO, phoneNum, pass)

    }

    when (state.success) {

        0 -> {

            LaunchedEffect(key1 = Unit) {

                Log.e("Splash", "0")

                navController.navigate(AUTH_GRAPH_ROUTE) {

                    popUpTo("Splash_Screen") { inclusive = true } // Sayfayı back stackden silme

                }

            }

        }

        1 -> {

            LaunchedEffect(key1 = Unit) {

                Log.e("Splash", "1")

                navController.navigate(SCAFFOLD_GRAPH_ROUTE)
                {

                    popUpTo("Splash_Screen") { inclusive = true } // Sayfayı back stackden silme

                }

            }
        }

        202 -> {

            LaunchedEffect(key1 = Unit) {

                Log.e("Splash", "2")

                navController.navigate(AUTH_GRAPH_ROUTE) {

                    popUpTo("Splash_Screen") { inclusive = true } // Sayfayı back stackden silme

                }

            }

        }

        3 -> {

            LaunchedEffect(key1 = Unit) {

                Log.e("Splash", "3")

                navController.navigate(AUTH_GRAPH_ROUTE) {

                    popUpTo("Splash_Screen") { inclusive = true } // Sayfayı back stackden silme

                }
            }
        }

    }

    when (state.internet) {

        true -> {

            LaunchedEffect(key1 = Unit) {

                Log.e("Splash to", "No Internet Page")

                navController.navigate(
                    ScreenList.NoConnectionScreen.withArgs()
                ) {

                    popUpTo("Splash_Screen") { inclusive = true } // Sayfayı back stackden silme

                }

            }


        }

        false -> {


        }


    }

    LoginScreenTheme() {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(RedVisne)
        ) {

            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.weight(10f)) {
                LoadingAnimation(3.5f)

            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(70f)) {
                WelcomeLogo()
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(20f)) {

                HaznedarText()

            }

        }
    }
}


@Composable
fun LoadingAnimation(speed: Float) {

    val compositionLoading by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.anim_loading))
    val progressLoading by animateLottieCompositionAsState(
        composition = compositionLoading,
        isPlaying = true,
        speed = speed,
        restartOnPlay = true,
        iterations = LottieConstants.IterateForever
    )

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(top = 1.dp)
    ) {

        LottieAnimation(
            composition = compositionLoading,
            progress = progressLoading,
            modifier = Modifier.size(45.dp)
        )

    }
}

//data class LoadingPlugin(val source: Any?) : ImagePlugin.LoadingStatePlugin {
//
//    @Composable
//    override fun compose(
//        modifier: Modifier,
//        imageOptions: ImageOptions,
//        executor: @Composable (IntSize) -> Unit
//    ): ImagePlugin = apply {
//        if (source != null && imageOptions != null) {
//            ImageBySource(
//                source = source,
//                modifier = modifier,
//                alignment = imageOptions.alignment,
//                contentDescription = imageOptions.contentDescription,
//                contentScale = imageOptions.contentScale,
//                colorFilter = imageOptions.colorFilter,
//                alpha = imageOptions.alpha
//            )
//        }
//    }
//
//}


@Composable
fun WelcomeLogo() {

    GlideImage(

        modifier = Modifier
            //.height(400.dp)
            .fillMaxWidth(),
        //!! TODO ,same with others;
        imageModel = { Api.SplashImagePath },
        // Crop, Fit, Inside, FillHeight, FillWidth, None
        imageOptions = ImageOptions(contentScale = ContentScale.Fit),
        //  contentScale = ContentScale.Fit,
        // shows an image with a circular revealed animation.



        component = rememberImageComponent {
            add(CircularRevealPlugin( duration = 250))
          //  +CircularRevealPlugin(duration = 250)
        },


       // circularReveal = CircularReveal(duration = 250),
        // shows a placeholder ImageBitmap when loading.

        //placeHolder = ImageBitmap.imageResource(R.drawable.placeholder),

        // shows an error ImageBitmap when the request failed.

        //error = ImageBitmap.imageResource(R.drawable.error)
    )

}

@Composable
fun HaznedarText() {

    val customFont = Font(R.font.phantasm)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(bottom = 5.dp)
    ) {

        Text(
            text = stringResource(R.string.haznedar),
            color = Color.White,
            fontFamily = FontFamily(customFont),
            fontSize = 28.sp
        )
    }
}
