package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class IsAccountCachedSuccessfullyUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mock()

    private val sut = IsAccountCachedSuccessfullyUseCase(accountInformationRepository)

    @Test
    fun `EXPECT true WHEN address is not failed addresses list`() = runTest {
        whenever(accountInformationRepository.getFailedAccountInformation())
            .thenReturn(SUCCESSFULLY_CACHED_ADDRESSES)

        val result = sut("anotherAddress")

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN address is in failed addresses list`() = runTest {
        whenever(accountInformationRepository.getFailedAccountInformation())
            .thenReturn(SUCCESSFULLY_CACHED_ADDRESSES)

        val result = sut(ADDRESS)

        assertTrue(!result)
    }

    private companion object {
        private const val ADDRESS = "address"
        private val SUCCESSFULLY_CACHED_ADDRESSES = listOf(ADDRESS)
    }
}
