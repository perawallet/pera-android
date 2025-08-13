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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import com.algorand.wallet.swap.domain.repository.SwapRepository
import javax.inject.Inject

internal class GetPreselectedSwapAddressUseCase @Inject constructor(
    private val swapRepository: SwapRepository,
    private val getAccountsDetails: GetAccountsDetails
) : GetPreselectedSwapAddress {

    override suspend fun invoke(): String? {
        val sortedAuthAccountDetails = getSortedAuthAccountDetails()
        val lastUsedAddress = swapRepository.getLastUsedSwapAddress()
        val isLastUsedAddressValid = sortedAuthAccountDetails.any { it.address == lastUsedAddress }

        return if (isLastUsedAddressValid) {
            lastUsedAddress
        } else {
            val firstValidAddress = sortedAuthAccountDetails.firstOrNull()?.address
            if (firstValidAddress != null) swapRepository.setLastUsedSwapAddress(firstValidAddress)
            firstValidAddress
        }
    }

    private suspend fun getSortedAuthAccountDetails(): List<AccountDetail> {
        return getAccountsDetails()
            .filter { it.canSignTransaction() && it.customAccountInfo != null }
            .sortedBy { it.customAccountInfo!!.orderIndex }
    }
}
