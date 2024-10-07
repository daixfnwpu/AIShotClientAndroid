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
import androidx.room.PrimaryKey
import com.ai.aishotclientkotlin.domain.model.bi.bean.Keyword
import com.ai.aishotclientkotlin.domain.model.bi.bean.Video

@Immutable
@Entity()
data class Movie(
  var page: Int?,
  var author: String,
  var userId : Long,
  var user_avatar : String?,
  var keywords: List<Keyword>? = ArrayList(),
  var videos: List<Video>? = ArrayList(),
  var reviews: List<Review>? = ArrayList(),
  val poster_path: String?,
  val adult: Boolean=false,
  val overview: String,
  val release_date: String?,
  val genre_ids: List<Int>? = ArrayList(),
  @PrimaryKey(autoGenerate = true)
  val id: Long,
  val original_title: String?,
  val original_language: String?,
  val title: String,
  val backdrop_path: String?,
  val popularity: Float?=0f,
  val vote_count: Int?=0,
  val video: Boolean=false,
  val vote_average: Float?=0f
)
