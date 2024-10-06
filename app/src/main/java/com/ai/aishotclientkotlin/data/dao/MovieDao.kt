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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ai.aishotclientkotlin.data.dao.entity.Movie
import dagger.Provides
import javax.inject.Singleton

@Dao
interface MovieDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMovieList(movies: List<Movie>)

  @Update
  suspend fun updateMovie(movie: Movie)

  @Query("SELECT * FROM MOVIE WHERE id = :id_")
  suspend fun getMovie(id_: Long): Movie

  @Query("SELECT * FROM Movie WHERE page = :page_")
  suspend fun getMovieList(page_: Int): List<Movie>
}
