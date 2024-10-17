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
 *
 *
 */

package com.algorand.android.modules.walletconnect.cards.domain.usecase

import com.algorand.android.modules.walletconnect.cards.domain.helper.WalletConnectUrlSelectedAddressParser
import com.algorand.android.modules.walletconnect.cards.domain.repository.WalletConnectCardsRepository
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CacheWalletConnectRequestPreselectedAccountAddressesUseCaseTest {

    private val walletConnectUrlSelectedAddressParser: WalletConnectUrlSelectedAddressParser = mock()
    private val walletConnectCardsRepository: WalletConnectCardsRepository = mock()

    private val sut = CacheWalletConnectRequestPreselectedAccountAddressesUseCase(
        walletConnectCardsRepository,
        walletConnectUrlSelectedAddressParser
    )

    @Test
    fun `EXPECT address to be cached WHEN address is not null`() {
        whenever(walletConnectUrlSelectedAddressParser(WC_REQUEST_URL)).thenReturn(ADDRESS)

        sut(WC_REQUEST_URL)

        verify(walletConnectCardsRepository).cachePreselectedAccountAddresses(listOf(ADDRESS))
    }

    @Test
    fun `EXPECT address to not be cached WHEN address is null`() {
        whenever(walletConnectUrlSelectedAddressParser(WC_REQUEST_URL)).thenReturn(null)

        sut(WC_REQUEST_URL)

        verify(walletConnectCardsRepository, times(0)).cachePreselectedAccountAddresses(any())
    }

    companion object {
        private const val ADDRESS = "address"
        private const val WC_REQUEST_URL = "wcRequestUrl"
    }
}
