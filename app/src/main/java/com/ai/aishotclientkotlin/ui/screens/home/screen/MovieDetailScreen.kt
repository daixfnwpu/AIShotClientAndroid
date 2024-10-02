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

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController

import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.domain.model.bi.Keyword
import com.ai.aishotclientkotlin.domain.model.bi.Review
import com.ai.aishotclientkotlin.domain.model.bi.Video
import com.ai.aishotclientkotlin.domain.model.bi.entity.Movie
import com.ai.aishotclientkotlin.ui.nav.tool.ScreenList
import com.ai.aishotclientkotlin.ui.screens.home.model.MovieDetailViewModel
import com.ai.aishotclientkotlin.ui.theme.background
import com.ai.aishotclientkotlin.ui.theme.Purple200
import com.ai.aishotclientkotlin.util.ui.NetworkImage
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithArrow
import com.ai.aishotclientkotlin.util.ui.custom.RatingBar
import com.google.accompanist.flowlayout.FlowRow
import com.kmpalette.palette.graphics.Palette
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
  navController: NavController,
  posterId: Long,
  viewModel: MovieDetailViewModel,
  pressOnBack: () -> Unit
) {
  val movie: Movie? by viewModel.movieFlow.collectAsState(initial = null)

  LaunchedEffect(key1 = posterId) {
    viewModel.fetchMovieDetailsById(posterId)
  }

  Column(
    modifier = Modifier
      .verticalScroll(rememberScrollState())
      .background(background)
      .fillMaxSize(),
  ) {

    AppBarWithArrow(movie?.title,showHeart = true, pressOnBack)

    MovieDetailHeader(viewModel)

    MovieDetailVideos(navController,viewModel)

    MovieDetailSummary(viewModel)

    MovieDetailReviews(viewModel)

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun MovieDetailHeader(
  viewModel: MovieDetailViewModel
) {
  val movie: Movie? by viewModel.movieFlow.collectAsState(initial = null)

  Column(modifier = Modifier,
      verticalArrangement =Arrangement.Center) {

    var palette = remember { mutableStateOf<Palette?>(null) }
    NetworkImage(
      networkUrl = Api.getBackdropPath(movie?.backdrop_path),
      circularReveal = 300,
    //  shimmerParams = null,
//      bitmapPalette = BitmapPalette {
//        palette = it
//      },
      modifier = Modifier.align(Alignment.CenterHorizontally)
        .height(280.dp),
      palette = palette
    )

    Spacer(modifier = Modifier.height(25.dp))

    Text(
      text = movie?.title ?: "",
      style = MaterialTheme.typography.headlineSmall,
      color = Color.White,
      textAlign = TextAlign.Center,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Release Date: ${movie?.release_date}",
      style = MaterialTheme.typography.bodyLarge,
      color = Color.White,
      textAlign = TextAlign.Center,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    RatingBar(
      rating = (movie?.vote_average ?: 0f) / 2f,
      color = Color(palette?.value?.vibrantSwatch?.rgb ?: 0),
      modifier = Modifier
        .height(15.dp)
        .align(Alignment.CenterHorizontally)
    )
  }
}

@Composable
private fun MovieDetailVideos(
  navController: NavController,
  viewModel: MovieDetailViewModel
) {
  val videos by viewModel.videoListFlow.collectAsState(listOf())

  videos.whatIfNotNullOrEmpty {

    Column {

      Spacer(modifier = Modifier.height(23.dp))

      Text(
        text = stringResource(R.string.trailers),
        style = MaterialTheme.typography.headlineSmall,
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 15.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      LazyRow(
        modifier = Modifier
          .padding(horizontal = 15.dp)
      ) {

        items(items = videos) { video ->

          VideoThumbnail(navController,video)

          Spacer(modifier = Modifier.width(12.dp))
        }
      }
    }
  }
}

@Composable
private fun VideoThumbnail(
  navController: NavController,
  video: Video
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  Surface(
    shape = RoundedCornerShape(8.dp),
    tonalElevation = 8.dp,
  ) {

    ConstraintLayout(
      modifier = Modifier
        .width(150.dp)
        .height(100.dp)
        .clickable(
          onClick = {
            val playVideoIntent =
              Intent(Intent.ACTION_VIEW, Uri.parse(Api.getYoutubeVideoPath(video.key)))
            context.startActivity(playVideoIntent)
          }
        )
    ) {
      val (thumbnail, icon, box, title) = createRefs()
    //!!TODO , check which Palette? import androidx.palette.graphics.Palette
      var palette = remember { mutableStateOf<Palette?>(null) }
      NetworkImage(
        networkUrl = Api.getYoutubeThumbnailPath(video.key),
        modifier = Modifier
          .fillMaxSize()
          .clickable
            (
            onClick = {
              //    selectPoster(MainScreenHomeTab.MOVIE, movie.id)
              //TODO : TEST :
              scope.launch {

                val videoId = video.site.substringAfter("/video/").substringBefore("?")
                Log.e("URL","site is : ${video.site}; videoId is ${videoId}")
                navController.navigate(
                  ScreenList.VideoScreen.withArgs(videoId)
                )

              }

            }
          )
          .constrainAs(thumbnail) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
          },
//        bitmapPalette = BitmapPalette {
//          palette = it
//        }
        palette = palette
      )

      Image(
        bitmap = ImageBitmap.imageResource(R.drawable.icon_youtube),
        contentDescription = null,
        modifier = Modifier
          .width(30.dp)
          .height(20.dp)
          .constrainAs(icon) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          }
      )

      Crossfade(
        targetState = palette,
        modifier = Modifier
          .height(25.dp)
          .constrainAs(box) {
            bottom.linkTo(parent.bottom)
          }
      ) {

        Box(
          modifier = Modifier
            .background(Color(it?.value?.darkVibrantSwatch?.rgb ?: 0))
            .alpha(0.7f)
            .fillMaxSize()
        )
      }

      Text(
        text = video.name,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        maxLines = 1,
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

@Composable
private fun MovieDetailSummary(
  viewModel: MovieDetailViewModel
) {
  val movie: Movie? by viewModel.movieFlow.collectAsState(initial = null)
  val keywords by viewModel.keywordListFlow.collectAsState(listOf())

  keywords.whatIfNotNullOrEmpty {

    Column {

      Spacer(modifier = Modifier.height(23.dp))

      Text(
        text = stringResource(R.string.summary),
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 15.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = movie?.overview ?: "",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 15.dp)
      )

      Spacer(modifier = Modifier.height(15.dp))

      FlowRow {

        it.forEach {

          Keyword(it)
        }
      }
    }
  }
}

@Composable
private fun Keyword(keyword: Keyword) {
  Surface(
    shape = RoundedCornerShape(32.dp),
    tonalElevation = 8.dp,
    color = Purple200,
    modifier = Modifier.padding(8.dp)
  ) {

    Text(
      text = keyword.name,
      style = MaterialTheme.typography.bodyLarge,
      color = Color.White,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}

@Composable
private fun MovieDetailReviews(
  viewModel: MovieDetailViewModel
) {
  val reviews by viewModel.reviewListFlow.collectAsState(listOf())

  reviews.whatIfNotNullOrEmpty {

    Column {

      Spacer(modifier = Modifier.height(23.dp))

      Text(
        text = stringResource(R.string.reviews),
        style = MaterialTheme.typography.headlineSmall,
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 15.dp)
      )

      Column {

        reviews.forEach {

          Review(it)
        }
      }
    }
  }
}

@Composable
private fun Review(
  review: Review
) {
  var expanded: Boolean by remember { mutableStateOf(false) }

  Column {

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = review.author,
      style = MaterialTheme.typography.bodyLarge,
      color = Color.White,
      maxLines = 1,
      fontWeight = FontWeight.Bold,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    if (expanded) {
      Text(
        text = review.content,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 15.dp)
          .clickable { expanded = !expanded }
      )
    } else {
      Text(
        text = review.content,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 15.dp)
          .clickable { expanded = !expanded }
      )
    }
  }
}
