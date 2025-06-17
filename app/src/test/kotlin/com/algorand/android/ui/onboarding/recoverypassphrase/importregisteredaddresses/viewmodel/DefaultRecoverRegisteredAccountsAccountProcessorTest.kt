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

package com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel

import com.algorand.android.modules.currency.domain.model.Currency.USD
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyUsd
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.mapper.RegisteredHdKeyItemMapper
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.model.RegisteredHdKeyItem
import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.info.domain.usecase.GetRegisteredHdKeys
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultRecoverRegisteredAccountsAccountProcessorTest {

    private val aesPlatformManager: AESPlatformManager = mockk(relaxed = true) {
        every { decryptByteArray(ENCRYPTED_ENTROPY) } returns ENTROPY
    }
    private val getRegisteredHdKeys: GetRegisteredHdKeys = mockk {
        coEvery { invoke(entropy = ENTROPY) } returns listOf(REGISTERED_HD_KEY)
    }
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo = mockk(relaxed = true)
    private val isPrimaryCurrencyUsd: IsPrimaryCurrencyUsd = mockk(relaxed = true)
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate = mockk(relaxed = true)
    private val registeredHdKeyItemMapper: RegisteredHdKeyItemMapper = mockk(relaxed = true)
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName = mockk(relaxed = true)

    private val sut = DefaultRecoverRegisteredAccountsAccountProcessor(
        aesPlatformManager,
        getRegisteredHdKeys,
        isPrimaryCurrencyAlgo,
        isPrimaryCurrencyUsd,
        getUsdToPrimaryCurrencyConversionRate,
        registeredHdKeyItemMapper,
        getPrimaryCurrencySymbolOrName
    )

    @Test
    fun `EXPECT account items with usd values WHEN selected currency is usd`() = runTest {
        every { isPrimaryCurrencyAlgo() } returns false
        every { isPrimaryCurrencyUsd() } returns true
        every { registeredHdKeyItemMapper(REGISTERED_HD_KEY, ONE, USD.symbol) } returns REGISTERED_HD_KEY_ITEM

        val result = sut.getRegisteredHdKeyItems(ENCRYPTED_ENTROPY)

        val expected = listOf(REGISTERED_HD_KEY_ITEM)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT account items with usd values WHEN selected currency is algo`() = runTest {
        every { isPrimaryCurrencyAlgo() } returns true
        every { isPrimaryCurrencyUsd() } returns false
        every { registeredHdKeyItemMapper(REGISTERED_HD_KEY, ONE, USD.symbol) } returns REGISTERED_HD_KEY_ITEM

        val result = sut.getRegisteredHdKeyItems(ENCRYPTED_ENTROPY)

        val expected = listOf(REGISTERED_HD_KEY_ITEM)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT account items with selected currency value WHEN selected currency is not usd or algo`() = runTest {
        every { isPrimaryCurrencyAlgo() } returns false
        every { isPrimaryCurrencyUsd() } returns false
        every { getUsdToPrimaryCurrencyConversionRate() } returns TEN
        every { getPrimaryCurrencySymbolOrName() } returns "SYMBOL"
        every { registeredHdKeyItemMapper(REGISTERED_HD_KEY, TEN, "SYMBOL") } returns REGISTERED_HD_KEY_ITEM

        val result = sut.getRegisteredHdKeyItems(ENCRYPTED_ENTROPY)

        val expected = listOf(REGISTERED_HD_KEY_ITEM)
        assertEquals(expected, result)
    }

    private companion object {
        val ENCRYPTED_ENTROPY = byteArrayOf(1, 2, 3)
        val ENTROPY = byteArrayOf(4, 5, 6)
        val REGISTERED_HD_KEY = peraFixture<RegisteredHdKey>()
        val REGISTERED_HD_KEY_ITEM = peraFixture<RegisteredHdKeyItem>()
    }
}
