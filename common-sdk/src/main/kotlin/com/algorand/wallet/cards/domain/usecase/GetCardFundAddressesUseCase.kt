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

package com.algorand.wallet.cards.domain.usecase

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.cards.domain.model.FundAddress
import com.algorand.wallet.cards.domain.repository.CardRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class GetCardFundAddressesUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val cardRepository: CardRepository
) : GetCardFundAddresses {

    override suspend fun invoke(): PeraResult<List<FundAddress>> {
        val addresses = getLocalAccounts().mapNotNull { localAccount ->
            localAccount.algoAddress.takeIf { localAccount !is LocalAccount.NoAuth }
        }
        return cardRepository.getCardFundAddresses(addresses)
    }
}
