package com.ai.aishotclientkotlin.util.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.ai.aishotclientkotlin.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent

///!!!TODO palette must be import,util now,it failure
import com.skydoves.landscapist.palette.PaletteLoadedListener
import com.skydoves.landscapist.palette.PalettePlugin

/**
 * A wrapper around [CoilImage] setting a default [contentScale] and showing
 * an indicator when loading disney poster images.
 *
 * @see CoilImage https://github.com/skydoves/landscapist#coil
 */
@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    circularRevealEnabled: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
    ///!!!TODO same as bellow
    paletteLoadedListener: PaletteLoadedListener? = null
) {
    CoilImage(
        imageModel = { url },
        modifier = modifier,
        imageOptions = ImageOptions(contentScale = contentScale),
        component = rememberImageComponent {
            if (circularRevealEnabled) {
                +CircularRevealPlugin()
            } else {
                +CrossfadePlugin(duration = 350)
            }
            ///!!!TODO same as bellow
            if (paletteLoadedListener != null) {
                +PalettePlugin(paletteLoadedListener = paletteLoadedListener)
            }
        },
        previewPlaceholder = R.drawable.poster,
        failure = {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "image request failed.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
    )
}

@Preview
@Composable
private fun NetworkImagePreview() {
    NetworkImage(
        url = "",
        modifier = Modifier.fillMaxSize()
    )
}