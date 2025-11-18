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

package com.algorand.wallet.wealth.asset.data.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.asset.data.api.AssetBalanceHistoryApiService
import com.algorand.wallet.wealth.asset.data.mapper.AssetBalanceHistoryMapper
import com.algorand.wallet.wealth.asset.domain.model.AssetBalanceHistory
import com.algorand.wallet.wealth.asset.domain.repository.AssetBalanceHistoryRepository
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import javax.inject.Inject

internal class DefaultAssetBalanceHistoryRepository @Inject constructor(
    private val assetBalanceHistoryApiService: AssetBalanceHistoryApiService,
    private val periodRequestMapper: WalletWealthPeriodRequestMapper,
    private val assetBalanceHistoryMapper: AssetBalanceHistoryMapper
) : AssetBalanceHistoryRepository {

    override suspend fun getAssetBalanceHistory(
        address: String,
        assetId: Long,
        period: WalletWealthPeriod,
        currency: String
    ): PeraResult<AssetBalanceHistory> {
        return try {
            fetchAssetBalanceHistory(address, assetId, period, currency)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private suspend fun fetchAssetBalanceHistory(
        address: String,
        assetId: Long,
        period: WalletWealthPeriod,
        currency: String
    ): PeraResult<AssetBalanceHistory> {
        val results = assetBalanceHistoryApiService.getAssetBalanceHistory(
            address,
            assetId,
            periodRequestMapper(period),
            currency
        )
        return PeraResult.Success(assetBalanceHistoryMapper.map(results))
    }
}
