package com.algorand.android.module.account.core.component.polling.data.repository

import com.algorand.android.module.account.core.component.caching.data.service.MobileAlgorandApi
import com.algorand.android.module.account.core.component.polling.accountdetail.data.model.*
import com.algorand.android.module.account.core.component.polling.accountdetail.data.repository.AccountBlockPollingRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*
import java.io.IOException

class AccountBlockPollingRepositoryImplTest {

    private val mobileAlgorandApi: MobileAlgorandApi = mock()

    private val sut = AccountBlockPollingRepositoryImpl(mobileAlgorandApi)

    @Test
    fun `EXPECT true WHEN api call is successful and response payload is true`() = runTest {
        whenever(mobileAlgorandApi.shouldRefreshAccountCache(ACCOUNT_CACHE_REFRESH_BODY))
            .thenReturn(AccountCacheRefreshResponse(true))

        val result = sut.isAccountUpdateRequired(LOCAL_ACCOUNT_ADDRESSES, LAST_KNOWN_ROUND)

        assertTrue(result.getOrNull()!!)
    }

    @Test
    fun `EXPECT false WHEN api call is successful and response payload is false`() = runTest {
        whenever(mobileAlgorandApi.shouldRefreshAccountCache(ACCOUNT_CACHE_REFRESH_BODY))
            .thenReturn(AccountCacheRefreshResponse(false))

        val result = sut.isAccountUpdateRequired(LOCAL_ACCOUNT_ADDRESSES, LAST_KNOWN_ROUND)

        assertTrue(!result.getOrNull()!!)
    }

    @Test
    fun `EXPECT failure WHEN api call is successful and response payload is null`() = runTest {
        whenever(mobileAlgorandApi.shouldRefreshAccountCache(ACCOUNT_CACHE_REFRESH_BODY))
            .thenReturn(AccountCacheRefreshResponse(null))

        val result = sut.isAccountUpdateRequired(LOCAL_ACCOUNT_ADDRESSES, LAST_KNOWN_ROUND)

        assertTrue(result.isFailure)
    }

    @Test
    fun `EXPECT failure WHEN api call throws exception`() = runTest {
        whenever(mobileAlgorandApi.shouldRefreshAccountCache(ACCOUNT_CACHE_REFRESH_BODY))
            .doAnswer { throw IOException() }

        val result = sut.isAccountUpdateRequired(LOCAL_ACCOUNT_ADDRESSES, LAST_KNOWN_ROUND)

        assertTrue(result.isFailure)
    }

    companion object {
        private val LOCAL_ACCOUNT_ADDRESSES = listOf("address1", "address2")
        private const val LAST_KNOWN_ROUND = 123L
        private val ACCOUNT_CACHE_REFRESH_BODY = AccountCacheRefreshRequestBody(
            LOCAL_ACCOUNT_ADDRESSES,
            LAST_KNOWN_ROUND
        )
    }
}
