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

package com.ai.aishotclientkotlin.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ai.aishotclientkotlin.domain.model.bi.entity.Movie
import com.ai.aishotclientkotlin.domain.model.bi.entity.Person
import com.ai.aishotclientkotlin.domain.model.bi.entity.Shop
import com.skydoves.moviecompose.persistence.converters.IntegerListConverter
import com.skydoves.moviecompose.persistence.converters.KeywordListConverter
import com.skydoves.moviecompose.persistence.converters.ReviewListConverter
import com.skydoves.moviecompose.persistence.converters.StringListConverter
import com.skydoves.moviecompose.persistence.converters.VideoListConverter

@Database(
  entities = [(Movie::class), (Shop::class), (Person::class)],
  version = 3, exportSchema = false
)
@TypeConverters(
  value = [
    (StringListConverter::class), (IntegerListConverter::class),
    (KeywordListConverter::class), (VideoListConverter::class), (ReviewListConverter::class)
  ]
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun movieDao(): MovieDao
  abstract fun tvDao(): ShopDao
  abstract fun peopleDao(): PeopleDao
}
