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

class IsThereAnyCachedErrorAccountUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = IsThereAnyCachedErrorAccountUseCase(accountInformationRepository, getLocalAccounts)

    @Test
    fun `EXPECT true WHEN there are failed accounts and excludeNoAuthAccounts is false`() = runTest {
        val failedAccounts = listOf(NO_AUTH_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(failedAccounts)

        val result = sut(excludeNoAuthAccounts = false)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN there are auth failed accounts and excludeNoAuthAccounts is true`() = runTest {
        val failedAccounts = listOf(ALGO_25_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(failedAccounts)

        val result = sut(excludeNoAuthAccounts = true)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN there are no failed accounts and excludeNoAuthAccounts is false`() = runTest {
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(emptyList())

        val result = sut(excludeNoAuthAccounts = false)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN there are failed accounts but excludeNoAuthAccounts is true`() = runTest {
        val failedAccounts = listOf(NO_AUTH_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(failedAccounts)

        val result = sut(excludeNoAuthAccounts = true)

        assertFalse(result)
    }

    private companion object {
        val NO_AUTH_ACCOUNT = peraFixture<LocalAccount.NoAuth>()
        val ALGO_25_ACCOUNT = peraFixture<LocalAccount.Algo25>()
    }

}
