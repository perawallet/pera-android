package com.algorand.android.assetdetail.component.domain.usecase

import com.algorand.android.assetdetail.component.domain.repository.AssetDetailRepository
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class InitializeAssetsUseCaseTest {

    private val assetDetailRepository: AssetDetailRepository = mock()

    private val sut =
        InitializeAssetDetailsUseCase(assetDetailRepository)

    @Test
    fun `EXPECT cache to be cleared and asset details to be fetched and cached`() = runTest {
        val accountAssets = fixtureOf<Map<String, List<Long>>>()

        sut(accountAssets)

        inOrder(assetDetailRepository) {
            verify(assetDetailRepository).clearCache()
            verify(assetDetailRepository).fetchAndCacheAssetDetails(any(), false)
        }
    }

    @Test
    fun `EXPECT duplicate asset ids to be removed`() = runTest {
        val accountAssets = mapOf("Address" to listOf<Long>(1L, 2L, 1L))

        sut(accountAssets)

        val expectedAssetIds = listOf(1L, 2L)
        verify(assetDetailRepository).fetchAndCacheAssetDetails(expectedAssetIds, false)
    }
}