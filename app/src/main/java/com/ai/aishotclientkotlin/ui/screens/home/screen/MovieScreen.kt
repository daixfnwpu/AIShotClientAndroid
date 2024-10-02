/*
 * Designed and developed by 2021 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ai.aishotclientkotlin.ui.screens.home.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.data.remote.Api
//import androidx.palette.graphics.Palette
import com.ai.aishotclientkotlin.domain.model.bi.entity.Movie
import com.ai.aishotclientkotlin.domain.model.bi.network.NetworkState
import com.ai.aishotclientkotlin.domain.model.bi.network.onLoading
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.home.model.MainViewModel
import com.ai.aishotclientkotlin.util.ui.NetworkImage
import com.ai.aishotclientkotlin.util.ui.custom.paging
//import com.google.accompanist.insets.statusBarsPadding
import com.kmpalette.palette.graphics.Palette
//import com.skydoves.landscapist.palette.BitmapPalette



@Composable
fun MovieScreen(
  navController: NavController,
  viewModel: MainViewModel = hiltViewModel(),
 // selectPoster: (MainScreenHomeTab, Long) -> Unit,
  //lazyListState: LazyListState,
  modifier: Modifier = Modifier
) {
  val networkState: NetworkState by viewModel.movieLoadingState
  val movies by viewModel.movies

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
   // state = lazyListState,
    modifier = modifier
      .statusBarsPadding()
      .background(MaterialTheme.colorScheme.background)
  ) {

    paging(
      items = movies,
      currentIndexFlow = viewModel.moviePageStateFlow,
      fetch = { viewModel.fetchNextMoviePage() }
    ) {

      MoviePoster(
        movie = it,
        navController = navController
      )
    }
  }

  networkState.onLoading {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {

      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center)
      )
    }
  }
}

@Composable
fun MoviePoster(
  navController: NavController,
  movie: Movie,
  //selectPoster: (MainScreenHomeTab, Long) -> Unit, // TODO ,use the navController instead it?
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(290.dp)
      .clickable(
        onClick = {
      //    selectPoster(MainScreenHomeTab.MOVIE, movie.id)
          navController.navigate(
            ScreenList.MovieDetailScreen.withArgs(movie.id . toString())
          )

        }
      ),
    color = MaterialTheme.colorScheme.onBackground
  ) {

    ConstraintLayout {
      val (image, box, title) = createRefs()

      var palette = remember { mutableStateOf<Palette?>(null) }
      NetworkImage(
        networkUrl = Api.getPosterPath(movie.poster_path),
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .constrainAs(image) {
            top.linkTo(parent.top)
          },
        palette = palette
        // TODO : check is important or not ????
//        bitmapPalette = BitmapPalette {
//          palette = it
//        }
      )

      Crossfade(
        targetState = palette.value,
        modifier = Modifier
          .height(50.dp)
          .constrainAs(box) {
            top.linkTo(image.bottom)
            bottom.linkTo(parent.bottom)
          }
      ) {

        Box(
          modifier = Modifier
            .background(Color(it?.darkVibrantSwatch?.rgb ?: 0))
            .alpha(0.7f)
            .fillMaxSize()
        )
      }

      Text(
        text = movie.title,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .fillMaxWidth()
          .alpha(0.85f)
          .padding(horizontal = 8.dp)
          .constrainAs(title) {
            top.linkTo(box.top)
            bottom.linkTo(box.bottom)
          }
      )
    }
  }
}
