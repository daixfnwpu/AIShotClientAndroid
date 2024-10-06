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
import com.ai.aishotclientkotlin.data.dao.entity.Shop


@Dao
interface ShopDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertShop(shops: List<Shop>)

  @Update
  suspend fun updateShop(shop: Shop)

  @Query("SELECT * FROM Shop WHERE id = :id_")
  suspend fun getShop(id_: Long): Shop?

  @Query("SELECT * FROM Shop WHERE page = :page_")
  suspend fun getShopList(page_: Int): List<Shop>
}
