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

class IsAccountOptedInToAnyAssetUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mockk()

    private val sut = IsAccountOptedInToAnyAssetUseCase(accountInformationRepository)

    @Test
    fun `EXPECT false WHEN account is not cached`() = runTest {
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns null

        val result = sut(ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN account is cached and opted in assets and created asset count is zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAssetsCount = 0,
            totalCreatedAssetsCount = 0
        )
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns assetAndAppsCount

        val result = sut(ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN account is cached and opted in assets is greater than zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAssetsCount = 1,
            totalCreatedAssetsCount = 0
        )
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns assetAndAppsCount

        val result = sut(ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN account is cached and created assets is greater than zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAssetsCount = 0,
            totalCreatedAssetsCount = 1
        )
        coEvery { accountInformationRepository.getAccountAssetsAndAppsCount(ADDRESS) } returns assetAndAppsCount

        val result = sut(ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN account is cached and opted in assets and created assets are greater than zero`() = runTest {
        val assetAndAppsCount = ACCOUNT_ASSET_AND_APPS_COUNT.copy(
            optedInAssetsCount = 1,
            totalCreatedAssetsCount = 1
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
