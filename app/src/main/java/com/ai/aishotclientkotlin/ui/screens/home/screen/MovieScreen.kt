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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.skydoves.landscapist.coil3.CoilImage

//import com.skydoves.landscapist.palette.BitmapPalette


@Composable
fun MovieScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val networkState: NetworkState by viewModel.movieLoadingState
    val movies by viewModel.movies
    var showUploadDialog by remember { mutableStateOf(false) } // 控制上传对话框的状态

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showUploadDialog = true // 点击按钮显示上传电影的对话框
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Upload Movie",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 确保内容不会与悬浮按钮重叠
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                networkState.onLoading {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // 上传电影的对话框
            if (showUploadDialog) {
                UploadMovieDialog(
                    onDismiss = { showUploadDialog = false },
                    onUpload = { details, imageList, video ->
                        /* 上传电影逻辑 */ showUploadDialog = false
                    }
                )
            }

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
                        ScreenList.MovieDetailScreen.withArgs(movie.id.toString())
                    )

                }
            ),
        color = MaterialTheme.colorScheme.onBackground
    ) {

        ConstraintLayout {
            val (image, box, card) = createRefs()

            var palette = remember { mutableStateOf<Palette?>(null) }
            NetworkImage(
                networkUrl = Api.getBackdropPath(movie.backdrop_path),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // 240
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
                targetState = palette,
                modifier = Modifier
                    .height(130.dp)
                    .background(Color.Transparent)
                    .alpha(0.5f)
                    .constrainAs(box) {
                        top.linkTo(image.bottom)
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
            // 使用Card显示头像、作者、发表时间、标题
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .alpha(0.5f)
                    .background(Color.Transparent)
                    .constrainAs(card) {
                        top.linkTo(box.top)
                        bottom.linkTo(box.bottom)

                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 头像
                    CoilImage(
                        imageModel = { Api.getAvatarImage(movie.poster_path) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 作者信息和标题
                    Column(
                        modifier = Modifier.fillMaxWidth().background(Color.Transparent),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "作者: ${movie.author}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "发表时间: ${movie.release_date}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 标题
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

    }
}
