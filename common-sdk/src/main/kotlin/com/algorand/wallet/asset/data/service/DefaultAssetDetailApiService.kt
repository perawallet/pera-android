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

package com.algorand.wallet.asset.data.service

import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.model.GetAssetsByIdsRequestBody
import com.algorand.wallet.asset.data.utils.toQueryString
import com.algorand.wallet.foundation.network.model.Pagination
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import javax.inject.Inject

internal class DefaultAssetDetailApiService @Inject constructor(
    private val apiService: AssetDetailRetrofitApiService,
    private val errorLogger: PeraErrorLogger,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled
) : AssetDetailApiService {

    override suspend fun getAssetsByIds(
        assetIds: List<Long>,
        deviceId: String?,
        includeDeleted: Boolean?
    ): Pagination<AssetResponse> {
        val safeDeviceId = deviceId?.toLongOrNull()
        val assetIdsQueryString = assetIds.toQueryString()
        return if (safeDeviceId == null || !isFeatureToggleEnabled(FeatureToggle.ASSET_DETAIL_V2_ENDPOINTS.key)) {
            apiService.getAssetsByIds(assetIdsQueryString, includeDeleted)
        } else {
            try {
                val assetIdsQuery = assetIds.map { it.toString() }
                val requestBody = GetAssetsByIdsRequestBody(safeDeviceId, assetIdsQuery, includeDeleted)
                apiService.getAssetsByIdsV2(requestBody)
            } catch (e: Exception) {
                errorLogger.logError(e)
                apiService.getAssetsByIds(assetIdsQueryString, includeDeleted)
            }
        }
    }

    override suspend fun getAssetDetail(assetId: Long, deviceId: String?): AssetResponse {
        val safeDeviceId = deviceId?.toLongOrNull()
        return if (safeDeviceId == null || !isFeatureToggleEnabled(FeatureToggle.ASSET_DETAIL_V2_ENDPOINTS.key)) {
            apiService.getAssetDetail(assetId)
        } else {
            try {
                apiService.getAssetDetailV2(safeDeviceId, assetId)
            } catch (e: Exception) {
                errorLogger.logError(e)
                apiService.getAssetDetail(assetId)
            }
        }
    }
}
