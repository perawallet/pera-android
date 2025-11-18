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

package com.algorand.wallet.cards.data.repository

import com.algorand.wallet.cards.data.mapper.FundAddressMapper
import com.algorand.wallet.cards.data.service.CardApiService
import com.algorand.wallet.cards.domain.model.FundAddress
import com.algorand.wallet.cards.domain.repository.CardRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultCardRepository @Inject constructor(
    private val cardApiService: CardApiService,
    private val fundAddressMapper: FundAddressMapper
) : CardRepository {

    override suspend fun getCardFundAddresses(addresses: List<String>): PeraResult<List<FundAddress>> {
        return try {
            val addressesQuery = addresses.toAddressQuery()
            val fundAddresses = cardApiService.getFundAddresses(addressesQuery).results.mapNotNull {
                fundAddressMapper.map(it)
            }
            PeraResult.Success(fundAddresses)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun isCountryWaitlisted(addresses: List<String>): PeraResult<Boolean> {
        return try {
            val addressesQuery = addresses.toAddressQuery()
            val isWaitlisted = cardApiService.isCountryAvailable(addressesQuery).isWaitlisted == true
            PeraResult.Success(isWaitlisted)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private fun List<String>.toAddressQuery(): String = joinToString(",")
}
