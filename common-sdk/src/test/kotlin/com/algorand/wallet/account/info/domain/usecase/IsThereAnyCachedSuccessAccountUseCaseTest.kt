package com.algorand.wallet.account.info.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class IsThereAnyCachedSuccessAccountUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = IsThereAnyCachedSuccessAccountUseCase(accountInformationRepository, getLocalAccounts)

    @Test
    fun `EXPECT true WHEN there are cached accounts and excludeNoAuthAccounts is false`() = runTest {
        val successAccounts = listOf(NO_AUTH_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getAllSuccessfullyCachedAccountAddresses()).thenReturn(successAccounts)

        val result = sut(excludeNoAuthAccounts = false)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN there are auth cached accounts and excludeNoAuthAccounts is true`() = runTest {
        val successAccounts = listOf(ALGO_25_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getAllSuccessfullyCachedAccountAddresses()).thenReturn(successAccounts)

        val result = sut(excludeNoAuthAccounts = true)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN there are no cached accounts and excludeNoAuthAccounts is false`() = runTest {
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getAllSuccessfullyCachedAccountAddresses()).thenReturn(emptyList())

        val result = sut(excludeNoAuthAccounts = false)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN there are no cached accounts and excludeNoAuthAccounts is true`() = runTest {
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getAllSuccessfullyCachedAccountAddresses()).thenReturn(emptyList())

        val result = sut(excludeNoAuthAccounts = false)

        assertFalse(result)
    }

    private companion object {
        val NO_AUTH_ACCOUNT = peraFixture<LocalAccount.NoAuth>()
        val ALGO_25_ACCOUNT = peraFixture<LocalAccount.Algo25>()
    }
}
