/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.assetdetail.component.assetabout.domain.usecase

import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.caching.CacheResult
import kotlinx.coroutines.flow.Flow

fun interface CacheAssetDetailToAsaProfile {
    suspend operator fun invoke(assetId: Long)
}

fun interface GetAssetFlowFromAsaProfileCache {
    suspend operator fun invoke(): Flow<CacheResult<Asset>?>
}

fun interface ClearAsaProfileCache {
    suspend operator fun invoke()
}
