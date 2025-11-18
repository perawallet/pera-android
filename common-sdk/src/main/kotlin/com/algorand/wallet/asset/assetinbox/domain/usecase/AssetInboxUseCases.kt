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

package com.algorand.wallet.asset.assetinbox.domain.usecase

import com.algorand.wallet.asset.assetinbox.domain.model.AssetInboxRequest
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.flow.Flow

fun interface GetAssetInboxRequests {
    suspend operator fun invoke(addresses: List<String>): PeraResult<List<AssetInboxRequest>>
}

fun interface CacheAssetInboxRequests {
    suspend operator fun invoke(requests: List<AssetInboxRequest>)
}

fun interface ClearAssetInboxCache {
    suspend operator fun invoke()
}

fun interface GetAssetInboxValidAddresses {
    suspend operator fun invoke(): List<String>
}

fun interface GetAssetInboxRequestCountFlow {
    suspend operator fun invoke(): Flow<Int>
}

fun interface GetAssetInboxRequest {
    suspend operator fun invoke(address: String): AssetInboxRequest?
}
