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

package com.ai.aishotclientkotlin.data.dao.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import com.ai.aishotclientkotlin.domain.model.bi.bean.Keyword
import com.ai.aishotclientkotlin.domain.model.bi.bean.Video
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ai.aishotclientkotlin.data.dao.converters.ShopListConverter

@Immutable
@Entity
@TypeConverters(ShopListConverter::class)
data class Shop(
  @PrimaryKey(autoGenerate = true)
  val id: Long,
  var page: Int,
  var keywords: List<Keyword>? = emptyList(),
  var videos: List<Video>? = emptyList(),
  var reviews: List<Review>? = emptyList(),
  val poster_path: String?,
  val popularity: Float,
  val backdrop_path: String?,
  val vote_average: Float,
  val overview: String,
  val first_air_date: String?,
  val origin_country: List<String>,
  val genre_ids: List<Int>,
  val original_language: String,
  val vote_count: Int,
  val name: String,
  val original_name: String
)
