/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.walletconnect.cards.domain.usecase

import com.algorand.android.modules.walletconnect.cards.domain.repository.WalletConnectCardsRepository
import javax.inject.Inject

internal class CacheWalletConnectRequestPreselectedAccountAddressesUseCase @Inject constructor(
    private val walletConnectCardsRepository: WalletConnectCardsRepository
) : CacheWalletConnectRequestPreselectedAccountAddresses {

    override fun invoke(wcRequestUrl: String) {
        val address = getSelectedAccountAddress(wcRequestUrl)
        if (address != null) {
            walletConnectCardsRepository.cachePreselectedAccountAddresses(listOf(address))
        }
    }

    private fun getSelectedAccountAddress(wcRequestUrl: String): String? {
        return wcRequestUrl.split("?")
            .lastOrNull()
            ?.split("&")
            ?.firstOrNull { it.contains(SELECTED_ACCOUNT_QUERY_KEY) }
            ?.split("=")
            ?.lastOrNull()
    }

    private companion object {
        const val SELECTED_ACCOUNT_QUERY_KEY = "selectedAccount"
    }
}
