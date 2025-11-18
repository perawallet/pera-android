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

package com.algorand.wallet.asset.domain.usecase

import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class FetchAndCacheMissingAssetsUseCase @Inject constructor(
    private val assetRepository: AssetRepository,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val errorLogger: PeraErrorLogger
) : FetchAndCacheMissingAssets {

    override suspend fun invoke(assetIds: List<Long>, includeDeleted: Boolean): PeraResult<Unit> {
        val cachedAssetIds = assetRepository.getCachedAssetIds()
        val missingAssetIds = assetIds.filterNot { cachedAssetIds.contains(it) }
        if (missingAssetIds.isEmpty()) return PeraResult.Success(Unit)
        val deviceId = getSelectedNodeDeviceId()
        if (deviceId.isNullOrBlank()) {
            errorLogger.logError("FetchAndCacheMissingAssetsUseCase: Device ID is null or blank.")
        }
        return assetRepository.fetchAndCacheAssets(missingAssetIds, deviceId, includeDeleted)
    }
}
