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

package com.algorand.wallet.asset.pricehistory.data.repository

import com.algorand.wallet.asset.data.utils.AssetIdQueryNormalizer
import com.algorand.wallet.asset.pricehistory.data.mapper.AssetPriceHistoryMapper
import com.algorand.wallet.asset.pricehistory.data.service.AssetPriceHistoryApiService
import com.algorand.wallet.asset.pricehistory.domain.model.AssetPriceHistory
import com.algorand.wallet.asset.pricehistory.domain.repository.AssetPriceHistoryRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import javax.inject.Inject

internal class DefaultAssetPriceHistoryRepository @Inject constructor(
    private val historyApiService: AssetPriceHistoryApiService,
    private val assetPriceHistoryMapper: AssetPriceHistoryMapper,
    private val walletWealthPeriodRequestMapper: WalletWealthPeriodRequestMapper
) : AssetPriceHistoryRepository {

    override suspend fun getAssetPriceHistory(
        assetId: Long,
        period: WalletWealthPeriod
    ): PeraResult<List<AssetPriceHistory>> {
        return try {
            fetchAssetPriceHistory(assetId, period)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private suspend fun fetchAssetPriceHistory(
        assetId: Long,
        period: WalletWealthPeriod
    ): PeraResult<List<AssetPriceHistory>> {
        val id = AssetIdQueryNormalizer.getSafeAssetIdForRequest(assetId)
        val response = historyApiService.getAssetPriceHistory(id, walletWealthPeriodRequestMapper(period))
        val assetPriceHistoryList = response.mapNotNull { assetPriceHistoryMapper(it) }
        return PeraResult.Success(assetPriceHistoryList)
    }
}
