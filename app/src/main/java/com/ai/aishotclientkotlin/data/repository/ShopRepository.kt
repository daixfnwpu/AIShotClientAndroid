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
import com.ai.aishotclientkotlin.data.dao.ShopDao
import com.ai.aishotclientkotlin.data.remote.ShopService
import com.ai.aishotclientkotlin.domain.model.bi.Keyword
import com.ai.aishotclientkotlin.domain.model.bi.Review
import com.ai.aishotclientkotlin.domain.model.bi.Video

import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class ShopRepository @Inject constructor(
  private val shopService: ShopService,
  private val shopDao: ShopDao
) : Repository {

  init {
    Timber.d("Injection ShopRepository")
  }

  @WorkerThread
  fun loadKeywordList(id: Long) = flow<List<Keyword>> {
    val shop = shopDao.getShop(id) ?: return@flow
    var keywords = shop.keywords
    if (keywords.isNullOrEmpty()) {
      val response = shopService.fetchKeywords(id)
      response.suspendOnSuccess {
        keywords = data.keywords
        shop.keywords = keywords
        shopDao.updateShop(shop)
        emit(keywords ?: listOf())
      }
    } else {
      emit(keywords ?: listOf())
    }
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun loadVideoList(id: Long) = flow<List<Video>> {
    val shop = shopDao.getShop(id) ?: return@flow
    var videos = shop.videos
    if (videos.isNullOrEmpty()) {
      val response = shopService.fetchShops(id)
      response.suspendOnSuccess {
        videos = data.results
        shop.videos = videos
        shopDao.updateShop(shop)
        emit(videos ?: listOf())
      }
    } else {
      emit(videos ?: listOf())
    }
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun loadReviewsList(id: Long) = flow<List<Review>> {
    val shop = shopDao.getShop(id) ?: return@flow
    var reviews = shop.reviews
    if (reviews.isNullOrEmpty()) {
      val response = shopService.fetchReviews(id)
      response.suspendOnSuccess {
        reviews = data.results
        shop.reviews = reviews
        shopDao.updateShop(shop)
        emit(reviews ?: listOf())
      }
    } else {
      emit(reviews ?: listOf())
    }
  }.flowOn(Dispatchers.IO)

  @WorkerThread
  fun loadShopById(id: Long) = flow {
    val shop = shopDao.getShop(id)
    emit(shop)
  }.flowOn(Dispatchers.IO)
}
