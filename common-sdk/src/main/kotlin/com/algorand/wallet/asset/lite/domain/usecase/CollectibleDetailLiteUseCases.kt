/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.lite.domain.usecase
import com.algorand.wallet.asset.lite.domain.model.CollectibleDetailLite
import kotlinx.coroutines.flow.Flow

fun interface GetCollectibleDetailsLiteFlow {
    operator fun invoke(collectibleIds: List<Long>): Flow<Map<Long, CollectibleDetailLite?>>
}

fun interface GetCollectibleDetailLiteFlow {
    operator fun invoke(collectibleId: Long): Flow<CollectibleDetailLite?>
}

fun interface GetCollectibleDetailsLite {
    operator fun invoke(collectibleIds: List<Long>): Map<Long, CollectibleDetailLite?>
}

fun interface GetCollectibleDetailLite {
    operator fun invoke(collectibleId: Long): CollectibleDetailLite?
}
