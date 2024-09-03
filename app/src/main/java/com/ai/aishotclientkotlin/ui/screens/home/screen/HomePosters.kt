package com.ai.aishotclientkotlin.ui.screens.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.ai.aishotclientkotlin.domain.model.login.Poster
import com.ai.aishotclientkotlin.ui.theme.AIShotClientKotlinTheme
import com.ai.aishotclientkotlin.util.ui.NetworkImage
import com.ai.aishotclientkotlin.util.ui.custom.StaggeredVerticalGrid


@Composable
fun HomePosters(
    modifier: Modifier = Modifier,
    posters: List<Poster>,
    selectPoster: (Long) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        StaggeredVerticalGrid(
            maxColumnWidth = 220.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            posters.forEach { poster ->
                key(poster.id) {
                    HomePoster(
                        poster = poster,
                        selectPoster = selectPoster
                    )
                }
            }
        }
    }
}

@Composable
private fun HomePoster(
    modifier: Modifier = Modifier,
    poster: Poster,
    selectPoster: (Long) -> Unit = {},
) {
    Surface(
        modifier = modifier
            .padding(4.dp)
            .clickable(
                onClick = { selectPoster(poster.id) }
            ),
        color = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        ConstraintLayout {
            val (image, title, content) = createRefs()
            NetworkImage(
                modifier = Modifier
                    .aspectRatio(0.8f)
                    .constrainAs(image) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                    },
                networkUrl = poster.poster,
            )

            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        centerHorizontallyTo(parent)
                        top.linkTo(image.bottom)
                    }
                    .padding(8.dp),
                text = poster.name,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier
                    .constrainAs(content) {
                        centerHorizontallyTo(parent)
                        top.linkTo(title.bottom)
                    }
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 12.dp),
                text = poster.playtime,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview(name = "HomePoster Light Theme")
private fun HomePosterPreviewLight() {
    AIShotClientKotlinTheme(darkTheme = false) {
        HomePoster(
            poster = Poster.mock()
        )
    }
}

@Composable
@Preview(name = "HomePoster Dark Theme")
private fun HomePosterPreviewDark() {
    AIShotClientKotlinTheme(darkTheme = true) {
        HomePoster(
            poster = Poster.mock()
        )
    }
}