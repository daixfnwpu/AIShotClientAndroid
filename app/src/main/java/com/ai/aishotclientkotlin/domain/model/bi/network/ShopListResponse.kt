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

package com.ai.aishotclientkotlin.domain.model.bi.network

import androidx.compose.runtime.Immutable
import com.ai.aishotclientkotlin.domain.model.bi.Video

@Immutable
data class ShopListResponse(
  val id: Int,
  val results: List<Video>
) : NetworkResponseModel
