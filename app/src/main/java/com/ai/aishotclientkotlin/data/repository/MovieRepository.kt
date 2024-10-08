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

package com.ai.aishotclientkotlin.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.ai.aishotclientkotlin.data.dao.MovieDao
import com.ai.aishotclientkotlin.data.dao.entity.Review
import com.ai.aishotclientkotlin.data.remote.MovieService
import com.ai.aishotclientkotlin.domain.model.bi.bean.Keyword
import com.ai.aishotclientkotlin.domain.model.bi.bean.Video
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class MovieRepository  @Inject constructor(
  private val movieService: MovieService,
  private val movieDao: MovieDao
) : Repository {

  init {
    Timber.d("Injection MovieRepository")
  }

  @WorkerThread
  fun loadKeywordList(id: Long) = flow<List<Keyword>> {
    val movie = movieDao.getMovie(id)
    var keywords = movie.keywords
    if (keywords.isNullOrEmpty()) {
      val response = movieService.fetchKeywords(id)
      response.suspendOnSuccess {
        keywords = data.keywords
        movie.keywords = keywords
        emit(keywords ?: listOf())
        movieDao.updateMovie(movie)
      }
    } else {
      emit(keywords ?: listOf())
    }
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun loadVideoList(id: Long) = flow<List<Video>> {
    val movie = movieDao.getMovie(id)
    var videos = movie.videos
    if (videos.isNullOrEmpty()) {
      movieService.fetchVideos(id)
        .suspendOnSuccess {
          videos = data.results
          movie.videos = videos
          movieDao.updateMovie(movie)
          emit(videos ?: listOf())
        }
    } else {
      emit(videos ?: listOf())
    }
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun loadReviewsList(id: Long) = flow<List<Review>> {
    val movie = movieDao.getMovie(id)
    Timber.tag("DAO").e("%s",movie)
    var reviews = movie.reviews
    if (reviews.isNullOrEmpty()) {
      Timber.tag("DAO").e("%s","start get the review from server:w")
      movieService.fetchReviews(id)
        .suspendOnSuccess {
          reviews = data.results
          movie.reviews = reviews
          movieDao.updateMovie(movie)
          emit(reviews ?: listOf())
        }
    } else {
      emit(reviews ?: listOf())
    }
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun loadMovieById(id: Long) = flow {
    val movie = movieDao.getMovie(id)
    emit(movie)
  }.flowOn(Dispatchers.IO)
}
