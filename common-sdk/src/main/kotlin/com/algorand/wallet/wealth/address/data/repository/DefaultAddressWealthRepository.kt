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

package com.algorand.wallet.wealth.address.data.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.address.data.api.AddressWealthApiService
import com.algorand.wallet.wealth.address.data.mapper.AddressWealthMapper
import com.algorand.wallet.wealth.address.domain.model.AddressWealth
import com.algorand.wallet.wealth.address.domain.repository.AddressWealthRepository
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import javax.inject.Inject

internal class DefaultAddressWealthRepository @Inject constructor(
    private val addressWealthApiService: AddressWealthApiService,
    private val periodRequestMapper: WalletWealthPeriodRequestMapper,
    private val addressWealthMapper: AddressWealthMapper
) : AddressWealthRepository {

    override suspend fun getAddressWealth(
        address: String,
        period: WalletWealthPeriod,
        currency: String
    ): PeraResult<AddressWealth> {
        return try {
            fetchAddressWealth(address, period, currency)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private suspend fun fetchAddressWealth(
        address: String,
        period: WalletWealthPeriod,
        currency: String
    ): PeraResult<AddressWealth> {
        val results = addressWealthApiService.getAddressWealth(address, periodRequestMapper(period), currency)
        return PeraResult.Success(addressWealthMapper.map(results))
    }
}
