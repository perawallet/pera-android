package com.algorand.wallet.account.info.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.model.AccountAssetAndAppsCount
import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsAccountOptedInToAnyAppUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mockk()

    private val sut = IsAccountOptedInToAnyAppUseCase(accountInformationRepository)

    @Test
    fun `EXPECT false WHEN account is not cached`() = runTest {
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns null

        val result = sut(ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN account is cached and opted in apps and created app count is zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAppsCount = 0,
            totalCreatedAppsCount = 0
        )
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns assetAndAppsCount

        val result = sut(ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN account is cached and opted in apps is greater than zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAppsCount = 1,
            totalCreatedAppsCount = 0
        )
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns assetAndAppsCount

        val result = sut(ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN account is cached and created apps is greater than zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAppsCount = 0,
            totalCreatedAppsCount = 1
        )
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns assetAndAppsCount

        val result = sut(ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN account is cached and opted in apps and created apps are greater than zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAppsCount = 1,
            totalCreatedAppsCount = 1
        )
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns assetAndAppsCount

        val result = sut(ADDRESS)

        assertTrue(result)
    }

    private companion object {
        const val ADDRESS = "address"
        val ACCOUNT_ASSET_AND_APPS_COUNT = peraFixture<AccountAssetAndAppsCount>()
    }
}
