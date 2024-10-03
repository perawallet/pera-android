package com.algorand.android.module.account.core.component.caching.domain.usecase

import com.algorand.android.module.account.core.component.caching.domain.usecase.implementation.CacheAccountDetailUseCase
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.account.info.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CacheAccountAssetInfoUseCaseTest {

    private val fetchAndCacheAccountInformation: FetchAndCacheAccountInformation = mock()
    private val fetchAndCacheAssets: FetchAndCacheAssets = mock()

    private val sut = CacheAccountDetailUseCase(
        fetchAndCacheAccountInformation,
        fetchAndCacheAssets
    )

    @Test
    fun `EXPECT account and asset details to be cached`() = runTest {
        whenever(fetchAndCacheAccountInformation(listOf(ADDRESS))).thenReturn(ACCOUNT_INFORMATION_MAP)

        sut(ADDRESS)

        verify(fetchAndCacheAccountInformation).invoke(listOf("address"))
        verify(fetchAndCacheAssets).invoke(listOf(ASSET_HOLDING.assetId), false)
    }

    @Test
    fun `EXPECT error WHEN account detail is not cached`() = runTest {
        whenever(fetchAndCacheAccountInformation(listOf(ADDRESS))).thenReturn(mapOf(ADDRESS to null))

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT account info WHEN asset detail is not cached`() = runTest {
        whenever(fetchAndCacheAccountInformation(listOf(ADDRESS))).thenReturn(ACCOUNT_INFORMATION_MAP)
        whenever(fetchAndCacheAssets(listOf(ASSET_HOLDING.assetId), false))
            .thenReturn(PeraResult.Error(Exception()))

        val result = sut(ADDRESS)

        Assert.assertEquals(ACCOUNT_INFORMATION, result.getDataOrNull())
    }

    @Test
    fun `EXPECT success WHEN account and asset details are cached`() {
        runTest {
            whenever(fetchAndCacheAccountInformation(listOf(ADDRESS))).thenReturn(ACCOUNT_INFORMATION_MAP)
            whenever(fetchAndCacheAssets(listOf(ASSET_HOLDING.assetId), false))
                .thenReturn(PeraResult.Success(Unit))

            val result = sut(ADDRESS)

            assertTrue(result.isSuccess)
        }
    }

    companion object {
        private const val ADDRESS = "address"
        private val ASSET_HOLDING = fixtureOf<AssetHolding>()
        private val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>().copy(
            assetHoldings = listOf(ASSET_HOLDING)
        )
        private val ACCOUNT_INFORMATION_MAP = mapOf(
            ADDRESS to ACCOUNT_INFORMATION
        )
    }
}
