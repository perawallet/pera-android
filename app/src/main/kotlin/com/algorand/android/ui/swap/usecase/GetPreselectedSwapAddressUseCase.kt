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

package com.algorand.android.ui.swap.usecase

import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheData
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.swap.domain.usecase.GetLastUsedSwapAddress
import com.algorand.wallet.swap.domain.usecase.SetLastUsedSwapAddress
import javax.inject.Inject

internal class GetPreselectedSwapAddressUseCase @Inject constructor(
    private val getLastUsedSwapAddress: GetLastUsedSwapAddress,
    private val getAccountLiteCacheData: GetAccountLiteCacheData,
    private val setLastUsedSwapAddress: SetLastUsedSwapAddress
) : GetPreselectedSwapAddress {

    override suspend fun invoke(): String? {
        val sortedAuthAddresses = getSortedAuthAddresses()
        val lastUsedAddress = getLastUsedSwapAddress()
        val isLastUsedAddressValid = sortedAuthAddresses.any { it == lastUsedAddress }
        return if (isLastUsedAddressValid) {
            lastUsedAddress
        } else {
            val firstValidAddress = sortedAuthAddresses.firstOrNull()
            if (firstValidAddress != null) setLastUsedSwapAddress(firstValidAddress)
            firstValidAddress
        }
    }

    private fun getSortedAuthAddresses(): List<String> {
        return getAccountLiteCacheData()?.accountLites?.mapNotNull { (address, accountLite) ->
            address.takeIf { accountLite.cachedInfo?.type?.canSignTransaction() == true }
        }.orEmpty()
    }
}
