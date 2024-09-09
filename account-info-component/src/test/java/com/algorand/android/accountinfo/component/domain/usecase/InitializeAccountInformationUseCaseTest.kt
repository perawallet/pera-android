package com.algorand.android.account.accountinformation.domain.usecase

import com.algorand.android.account.accountinformation.domain.mapper.AccountAssetsMapper
import com.algorand.android.account.accountinformation.domain.model.*
import com.algorand.android.account.accountinformation.domain.repository.AccountInformationRepository
import com.algorand.android.account.accountinformation.domain.usecase.implementation.InitializeAccountInformationUseCase
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class InitializeAccountInformationUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mock()
    private val accountInformationRepository: AccountInformationRepository = mock()
    private val accountAssetsMapper: AccountAssetsMapper = mock()

    private val sut = InitializeAccountInformationUseCase(
        getLocalAccounts,
        accountInformationRepository,
        accountAssetsMapper
    )

    @Test
    fun `EXPECT local accounts to be cached and assets lists to be returned`() = runTest {
        whenever(getLocalAccounts()).thenReturn(LOCAL_ACCOUNTS)
        whenever(accountInformationRepository.fetchAndCacheAccountInformation(LOCAL_ACCOUNT_ADDRESSES))
            .thenReturn(CACHED_ACCOUNTS)
        whenever(accountAssetsMapper(any(), any())).thenReturn(ACCOUNT_ASSETS)

        val result = sut()

        val expected = listOf(ACCOUNT_ASSETS)
        verify(accountInformationRepository).fetchAndCacheAccountInformation(LOCAL_ACCOUNT_ADDRESSES)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT cache to be cleared before caching account information`() = runTest {
        whenever(getLocalAccounts()).thenReturn(LOCAL_ACCOUNTS)
        whenever(accountInformationRepository.fetchAndCacheAccountInformation(LOCAL_ACCOUNT_ADDRESSES))
            .thenReturn(CACHED_ACCOUNTS)
        CACHED_ACCOUNTS.forEach {
            whenever(accountAssetsMapper(it.key, it.value.assetHoldings)).thenReturn(ACCOUNT_ASSETS)
        }

        sut()

        inOrder(accountInformationRepository) {
            verify(accountInformationRepository).clearCache()
            verify(accountInformationRepository).fetchAndCacheAccountInformation(LOCAL_ACCOUNT_ADDRESSES)
        }
    }

    @Test
    fun `EXPECT account to be filtered WHEN account information is null`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LocalAccount.NoAuth("address")))
        whenever(accountInformationRepository.fetchAndCacheAccountInformation(listOf("address")))
            .thenReturn(mapOf("address" to null))

        val result = sut()

        assertTrue(result.isEmpty())
        verify(accountAssetsMapper, never()).invoke(any(), any())
    }

    companion object {
        private val LOCAL_ACCOUNTS = fixtureOf<List<LocalAccount>>()
        private val LOCAL_ACCOUNT_ADDRESSES = LOCAL_ACCOUNTS.map { it.address }
        private val CACHED_ACCOUNTS = fixtureOf<Map<String, AccountInformation>>()
        private val ACCOUNT_ASSETS = fixtureOf<AccountAssets>()
    }
}
