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

package com.algorand.wallet.wealth.wallet.data.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.wallet.data.api.WalletWealthApiService
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthMapper
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealth
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import com.algorand.wallet.wealth.wallet.domain.repository.WalletWealthRepository
import javax.inject.Inject

internal class DefaultWalletWealthRepository @Inject constructor(
    private val walletWealthApiService: WalletWealthApiService,
    private val walletWealthMapper: WalletWealthMapper,
    private val periodRequestMapper: WalletWealthPeriodRequestMapper
) : WalletWealthRepository {

    override suspend fun getWalletWealth(
        addresses: List<String>,
        period: WalletWealthPeriod
    ): PeraResult<WalletWealth> {
        return try {
            fetchWalletWealth(addresses, period)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private suspend fun fetchWalletWealth(
        addresses: List<String>,
        period: WalletWealthPeriod
    ): PeraResult<WalletWealth> {
        val results = walletWealthApiService.getWalletWealth(
            addresses = addresses.joinToString(","),
            period = periodRequestMapper(period)
        )
        return PeraResult.Success(walletWealthMapper.map(results))
    }
}
