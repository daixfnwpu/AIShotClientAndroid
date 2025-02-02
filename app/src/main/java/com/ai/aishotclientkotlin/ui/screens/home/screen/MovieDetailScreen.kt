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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Comment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.domain.model.bi.bean.Keyword
import com.ai.aishotclientkotlin.domain.model.bi.bean.Video
import com.ai.aishotclientkotlin.data.dao.entity.Movie
import com.ai.aishotclientkotlin.data.dao.entity.Review
import com.ai.aishotclientkotlin.ui.nav.util.ScreenList
import com.ai.aishotclientkotlin.ui.screens.home.model.MovieDetailViewModel
import com.ai.aishotclientkotlin.ui.screens.home.model.ReviewViewModel
import com.ai.aishotclientkotlin.ui.theme.Purple200
import com.ai.aishotclientkotlin.ui.theme.background800
import com.ai.aishotclientkotlin.util.ui.NetworkImage
import com.ai.aishotclientkotlin.util.ui.custom.AppBarWithArrow
import com.ai.aishotclientkotlin.util.ui.custom.RatingBar
import com.google.accompanist.flowlayout.FlowRow
import com.kmpalette.palette.graphics.Palette
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.coroutines.launch
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.coil3.CoilImage
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
  navController: NavController,
  movieId: Long,
  modifier: Modifier = Modifier,
  viewModel: MovieDetailViewModel = hiltViewModel(),
  pressOnBack: () -> Unit
) {
  val movie: Movie? by viewModel.movieFlow.collectAsState(initial = null)

  LaunchedEffect(key1 = movieId) {
    Timber.tag("HTTP").e("fetchMovieDetails %s",movieId)
    viewModel.fetchMovieDetailsById(movieId)
  }

  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .background(background800)
      .fillMaxSize(),
  ) {

    AppBarWithArrow(movie?.title,showMenu = true, pressOnBack)

    MovieDetailHeader(viewModel)

    MovieDetailVideos(navController,viewModel)

    MovieDetailSummary(viewModel)

    HorizontalDivider(thickness = Dp.Hairline, modifier = Modifier.background(Color.White))

    MovieDetailReviews(viewModel,movieId)

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
    //修改为上传者的头像；
   /* var palette = remember { mutableStateOf<Palette?>(null) }
    NetworkImage(
      networkUrl = Api.getBackdropPath(movie?.backdrop_path),
      circularReveal = 300,
      modifier = Modifier.align(Alignment.CenterHorizontally)
        .height(280.dp),
      palette = palette
    )*/
    CoilImage(
      imageModel = { Api.getAvatarImage(movie?.user_avatar) },
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape),
      previewPlaceholder = painterResource(id = R.drawable.poster),
    )

    Spacer(modifier = Modifier.height(2.dp))

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

    Spacer(modifier = Modifier.height(2.dp))

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

    Spacer(modifier = Modifier.height(2.dp))

    RatingBar(
      rating = (movie?.vote_average ?: 0f) / 2f,
   //   color = Color(MaterialTheme.colorScheme.primary),
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
  val scope = rememberCoroutineScope()
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

        itemsIndexed(items = videos) {index, video ->
      //    TODO "这里可以判断，如果是是视频，还是图片。图片也需要进入，然后弹出一个类似视频的播放窗口，可以实现图片的阅览，轮询，放大的功能；"
          VideoImageThumbnail(navController,video,
            onclick = {
              //    selectPoster(MainScreenHomeTab.MOVIE, movie.id)
              //TODO : TEST :
              if (video.type == "video") {
                scope.launch {

                  val videoId = video.site
                    .substringAfter("/video/")
                    .substringBefore("?")
                  Log.e("URL", "site is : ${video.site}; videoId is ${videoId}")
                  navController.navigate(
                    ScreenList.VideoScreen.withArgs(videoId,index.toString())
                  )

                }
              } else // "image"
              {
                // TODO("需要整理图片的URL来显示")
                //   var imageUrls = video.key

                val imageUrls = listOf(videos.map { it.site })
                val imageUrlString = imageUrls.joinToString(",")
                navController.navigate(
                  ScreenList.PhotoCarouselScreen.withArgs(imageUrlString)
                )
              }

            }


            )

          Spacer(modifier = Modifier.width(12.dp))
        }
      }
    }
  }

}

@Composable
private fun VideoImageThumbnail(
  navController: NavController,
  video: Video,
  onclick : (Video) -> Unit
) {
  val context = LocalContext.current

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
              onclick(video)
            }
          )
          .constrainAs(thumbnail) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
          },
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
  viewModel: MovieDetailViewModel,
  movieId: Long,
  reviewMedel: ReviewViewModel = hiltViewModel()
) {
  val reviews by viewModel.reviewListFlow.collectAsState(listOf())
  val displayedReviews = remember { mutableStateListOf<Review>().apply { addAll(reviews) } }
  reviewMedel.movieId = movieId
  // Handle updates from the database flow
  LaunchedEffect(reviews) {
    displayedReviews.clear()
    displayedReviews.addAll(reviews)
  }

  var showCommentInput = remember {
    mutableStateOf(false)
  }
  Column {


    Row(Modifier.fillMaxWidth()) {
      StatusAction(
        Icons.Rounded.ThumbUp,
        stringResource(R.string.like),
        modifier = Modifier.weight(1f),
        onclick = {
          TODO("need to implement this ThumbUp function" )
        })

      VerticalDivider(Modifier.height(48.dp), thickness = Dp.Hairline)
      StatusAction(
        Icons.AutoMirrored.Rounded.Comment,
        stringResource(R.string.comment),
        modifier = Modifier.weight(1f), onclick = {
          showCommentInput.value = !showCommentInput.value
          Log.e("showCommentInput",showCommentInput.value.toString())
        })
      VerticalDivider(Modifier.height(48.dp), thickness = Dp.Hairline)
      StatusAction(Icons.Rounded.Share,
        stringResource(R.string.share),
        modifier = Modifier.weight(1f), onclick = {
          TODO("need to implement this Share function" )
        })
    }
    HorizontalDivider(thickness = Dp.Hairline, modifier = Modifier.background(Color.White))
    if (showCommentInput.value) {
   //   VerticalDivider(Modifier.height(48.dp), thickness = Dp.Hairline)
      Row(modifier = Modifier.fillMaxWidth()) {

        CommentInput(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
          onCommentSubmitted = { comment ->
            val newReview = Review(
              id = 0,  // Auto-generated
              author = reviewMedel.userName,  // Replace with actual author name
              content = comment,
              url = "",
              userId = reviewMedel.userID,
              movieId = reviewMedel.movieId// Fill with other required fields
            )
            displayedReviews.add(newReview)
            reviewMedel.sendReview(content = comment, success = {
              showCommentInput.value= false
              viewModel.fetchMovieDetailsById(movieId)
            }, error = {
              showCommentInput.value= false
              Log.e("http","发送失败")
            })
            showCommentInput.value= false
          },
          onCancel = { showCommentInput.value= false}
        )

      }
    }
  }


  displayedReviews.whatIfNotNullOrEmpty {



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

        displayedReviews.forEach {

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
@Composable
private fun StatusAction(
  icon: ImageVector,
  text: String,
  modifier: Modifier = Modifier,
  onclick: () -> Unit
) {
  TextButton(modifier = modifier,
    onClick = {
      onclick()
    },
    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.background)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(icon, contentDescription = text)
      Spacer(Modifier.width(8.dp))
      Text(text)
    }
  }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullScreenImageCarousel(
  imageUrls: List<String>, // List of image URLs
  initialPage: Int = 0,
  pressOnBack: () -> Unit      // Callback to close the full-screen view
) {
  // Remember Pager state
  val pagerState = rememberPagerState(initialPage)

  Box(modifier = Modifier.fillMaxSize()) {
    HorizontalPager(
      state = pagerState,
      count = imageUrls.size,
      modifier = Modifier.fillMaxSize()
    ) { page ->
        AsyncImage(
        model = imageUrls[page],
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxSize()
          .clickable(onClick = {
            pressOnBack()
          }),
        placeholder = painterResource(id = R.drawable.placeholder_image),
      )
    }

    // Close button
    IconButton(
      onClick = { pressOnBack() },
      modifier = Modifier.padding(16.dp)
    ) {
      Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "Close",
        tint = Color.Gray
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInput(
  onCommentSubmitted: (String) -> Unit,
  modifier: Modifier = Modifier,
  onCancel: () ->Unit
) {
  var commentText by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
      .background(Color.Gray)
  ) {
    Text(
      text = "Write a review",
      style = MaterialTheme.typography.headlineMedium,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    // TextField for input
    TextField(
      value = commentText,
      onValueChange = { commentText = it },
      placeholder = { Text("Enter your review here") },
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp),
      maxLines = 5,
      colors = TextFieldDefaults.colors()
    //  colors =TextFieldDefaults. colors
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Submit Button
    Row(
      modifier = Modifier.fillMaxWidth(),  // Fill the width to allow proper alignment
      horizontalArrangement = Arrangement.SpaceBetween

    ) {
      Button(
        onClick = {
          onCancel()
        },
      ) {
        Text("Cancel")
      }
      Button(
        onClick = {
          if (commentText.isNotBlank()) {
            onCommentSubmitted(commentText)
            commentText = "" // Clear input after submission
          }
        },
     //   modifier = Modifier.align(Alignment.End),
        enabled = commentText.isNotBlank() // Button only enabled if text is not blank
      ) {
        Text("Submit")
      }

    }

  }
}