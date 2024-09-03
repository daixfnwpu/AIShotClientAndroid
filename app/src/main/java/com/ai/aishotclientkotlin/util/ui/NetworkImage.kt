package com.ai.aishotclientkotlin.util.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.ai.aishotclientkotlin.ui.theme.shimmerHighLight
import com.ai.aishotclientkotlin.util.NetworkUrlPreviewProvider
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.palette.rememberPaletteState

/**
 * A wrapper around [CoilImage] setting a default [contentScale] and showing
 * an indicator when loading poster images.
 *
 * @see CoilImage https://github.com/skydoves/landscapist#coil
 */
@Preview
@Composable
fun NetworkImage(
    @PreviewParameter(NetworkUrlPreviewProvider::class) networkUrl: Any?,
    modifier: Modifier = Modifier,
    circularReveal: Int = 350,
    contentScale: ContentScale = ContentScale.FillBounds,
 //   palette: Palette
//    shimmerParams: ShimmerParams? = ShimmerParams(
//        baseColor = MaterialTheme.colors.background,
//        highlightColor = shimmerHighLight,
//        dropOff = 0.65f
//    ),
) {
    val url = networkUrl ?: return
    var palette by rememberPaletteState(null)

  //  if (shimmerParams == null) {
        CoilImage(
            imageModel ={ url},
            modifier = modifier,
            component = rememberImageComponent {
                +CircularRevealPlugin(
                    duration  = circularReveal
                )
                +ShimmerPlugin(
                    Shimmer.Flash(
                        baseColor = MaterialTheme.colorScheme.background,
                        highlightColor = shimmerHighLight,
                        dropOff = 0.65f
                    ),
                )
                +PalettePlugin { palette = it }
            },
         //   contentScale = contentScale,
            imageOptions = ImageOptions(contentScale = contentScale),
           // bitmapPalette = bitmapPalette,
            failure = {
                Text(
                    text = "image request failed.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
//    } else {
//        CoilImage(
//            imageModel = {url},
//            modifier = modifier,
//            component = rememberImageComponent {
//                +CircularRevealPlugin(
//                    duration  = circularReveal
//                )
//                +ShimmerPlugin(
//                    Shimmer.Flash(
//                        baseColor = MaterialTheme.colorScheme.background,
//                        highlightColor = shimmerHighLight,
//                        dropOff = 0.65f
//                    ),
//                )
//            },
//            //   contentScale = contentScale,
//            imageOptions = ImageOptions(contentScale = contentScale),
//            //contentScale = contentScale,
//           // circularReveal = circularReveal,
//            bitmapPalette = bitmapPalette,
//            failure = {
//                Text(
//                    text = "image request failed.",
//                    textAlign = TextAlign.Center,
//                    style = MaterialTheme.typography.body2,
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        )
//    }
}


@Preview
@Composable
private fun NetworkImagePreview() {
    NetworkImage(
        networkUrl = "",
        modifier = Modifier.fillMaxSize()
    )
}