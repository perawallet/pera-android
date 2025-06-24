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

package com.algorand.android.modules.accounts.lite.domain.usecase

import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.test.peraFixture
import com.algorand.wallet.account.detail.domain.model.AccountType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Test

class IsThereAnyAuthAddressWithBalanceUseCaseTest {

    private val getAccountLiteCacheData: GetAccountLiteCacheData = mockk()

    private val sut = IsThereAnyAuthAddressWithBalanceUseCase(getAccountLiteCacheData)

    @Test
    fun `EXPECT false WHEN cache data is null`() = runTest {
        coEvery { getAccountLiteCacheData() } returns null

        val result = sut()

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN there are only no auth address with balance`() = runTest {
        val authAccountLite = peraFixture<AccountLite>().copy(
            cachedInfo = AUTH_CACHED_INFO.copy(primaryAccountValue = BigDecimal.ZERO)
        )
        val noAuthAccountLite = peraFixture<AccountLite>().copy(
            cachedInfo = NO_AUTH_CACHED_INFO.copy(primaryAccountValue = BigDecimal.TEN)
        )
        val accountLites = mapOf("auth" to authAccountLite, "noAuth" to noAuthAccountLite)
        val accountLiteData = AccountLiteCacheStatus.Data(emptyList(), accountLites = accountLites)
        every { getAccountLiteCacheData() } returns accountLiteData

        val result = sut()

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN cached info is null`() = runTest {
        val accountLite = peraFixture<AccountLite>().copy(cachedInfo = null)
        val accountLites = mapOf("account" to accountLite)
        val accountLiteData = AccountLiteCacheStatus.Data(emptyList(), accountLites = accountLites)
        every { getAccountLiteCacheData() } returns accountLiteData

        val result = sut()

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN there are auth addresses with balance`() = runTest {
        val authAccountLite = peraFixture<AccountLite>().copy(
            cachedInfo = AUTH_CACHED_INFO.copy(primaryAccountValue = BigDecimal.TEN)
        )
        val noAuthAccountLite = peraFixture<AccountLite>().copy(
            cachedInfo = NO_AUTH_CACHED_INFO.copy(primaryAccountValue = BigDecimal.ZERO)
        )
        val accountLites = mapOf("auth" to authAccountLite, "noAuth" to noAuthAccountLite)
        val accountLiteData = AccountLiteCacheStatus.Data(emptyList(), accountLites = accountLites)
        every { getAccountLiteCacheData() } returns accountLiteData

        val result = sut()

        assert(result)
    }

    private companion object {
        val AUTH_CACHED_INFO = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.Algo25)
        val NO_AUTH_CACHED_INFO = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.NoAuth)
    }
}
