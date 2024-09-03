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

import androidx.annotation.WorkerThread
import com.ai.aishotclientkotlin.data.dao.MovieDao
import com.ai.aishotclientkotlin.data.dao.ShopDao
import com.ai.aishotclientkotlin.data.remote.TheDiscoverService
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import timber.log.Timber
import javax.inject.Inject

class DiscoverRepository @Inject constructor(
  private val discoverService: TheDiscoverService,
  private val movieDao: MovieDao,
  private val shopDao: ShopDao
) : Repository {

  init {
    Timber.d("Injection DiscoverRepository")
  }

  @WorkerThread
  fun loadMovies(page: Int, success: () -> Unit, error: () -> Unit) = flow {
    var movies = movieDao.getMovieList(page)
    if (movies.isEmpty()) {
      val response = discoverService.fetchDiscoverMovie(page)
      response.suspendOnSuccess {
        movies = data.results
        movies.forEach { it.page = page }
        movieDao.insertMovieList(movies)
        emit(movies)
      }.onError {
        error()
      }.onException { error() }
    } else {
      emit(movies)
    }
  }.onCompletion { success() }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun loadShops(page: Int, success: () -> Unit, error: () -> Unit) = flow {
    var shops = shopDao.getShopList(page)
    if (shops.isEmpty()) {
      val response = discoverService.fetchDiscoverShop(page)
      response.suspendOnSuccess {
        shops = data.results
        shops.forEach { it.page = page }
        shopDao.insertShop(shops)
        emit(shops)
      }.onError {
        error()
      }.onException { error() }
    } else {
      emit(shops)
    }
  }.onCompletion { success() }.flowOn(Dispatchers.IO)
}
