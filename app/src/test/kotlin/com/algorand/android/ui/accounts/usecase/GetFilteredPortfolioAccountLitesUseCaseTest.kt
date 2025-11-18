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

package com.algorand.android.ui.accounts.usecase

import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.test.peraFixture
import com.algorand.wallet.account.detail.domain.model.AccountType
import kotlin.test.assertEquals
import org.junit.Test

class GetFilteredPortfolioAccountLitesUseCaseTest {

    private val sut = GetFilteredPortfolioAccountLitesUseCase()

    @Test
    fun `EXPECT accounts with no auth type or null type to be filtered out`() {
        val result = sut(ACCOUNT_LITES)

        val expected = mapOf(
            ALGO_25_ACCOUNT_LITE.address to ALGO_25_ACCOUNT_LITE,
            LEDGER_BLE_ACCOUNT_LITE.address to LEDGER_BLE_ACCOUNT_LITE,
            REKEYED_ACCOUNT_LITE.address to REKEYED_ACCOUNT_LITE,
            REKEYED_AUTH_ACCOUNT_LITE.address to REKEYED_AUTH_ACCOUNT_LITE,
            HD_KEY_ACCOUNT_LITE.address to HD_KEY_ACCOUNT_LITE
        )
        assertEquals(expected, result)
    }

    private companion object {
        val ALGO_25_ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.Algo25)
        )
        val LEDGER_BLE_ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.LedgerBle)
        )
        val REKEYED_ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.Rekeyed)
        )
        val REKEYED_AUTH_ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.RekeyedAuth)
        )
        val NO_AUTH_ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.NoAuth)
        )
        val HD_KEY_ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.HdKey)
        )
        val NULL_TYPE_ACCOUNT_LITE = peraFixture<AccountLite>().copy(cachedInfo = null)

        val ACCOUNT_LITES = mapOf(
            ALGO_25_ACCOUNT_LITE.address to ALGO_25_ACCOUNT_LITE,
            LEDGER_BLE_ACCOUNT_LITE.address to LEDGER_BLE_ACCOUNT_LITE,
            REKEYED_ACCOUNT_LITE.address to REKEYED_ACCOUNT_LITE,
            REKEYED_AUTH_ACCOUNT_LITE.address to REKEYED_AUTH_ACCOUNT_LITE,
            NO_AUTH_ACCOUNT_LITE.address to NO_AUTH_ACCOUNT_LITE,
            HD_KEY_ACCOUNT_LITE.address to HD_KEY_ACCOUNT_LITE,
            NULL_TYPE_ACCOUNT_LITE.address to NULL_TYPE_ACCOUNT_LITE
        )
    }
}
