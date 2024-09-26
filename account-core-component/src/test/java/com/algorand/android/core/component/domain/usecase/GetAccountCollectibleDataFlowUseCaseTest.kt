package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.info.domain.model.*
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.model.*
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.module.account.core.component.collectible.domain.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class GetAccountCollectibleDataFlowUseCaseTest {

    private val getAccountInformation: GetAccountInformation = mock()
    private val getAssetDetail: GetAssetDetail = mock()
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory = mock()

    private val sut = GetAccountCollectibleDataFlowUseCase(
        getAccountInformation,
        getAssetDetail,
        baseOwnedCollectibleDataFactory
    )

    @Test
    fun `EXPECT nothing WHEN account information is null`() = runTest {
        whenever(getAccountInformation(ADDRESS)).thenReturn(null)

        sut(ADDRESS).collect()

        verify(getAssetDetail, never()).invoke(any())
        verify(baseOwnedCollectibleDataFactory, never()).invoke(any<AssetHolding>(), any<AssetDetail>())
    }

    @Test
    fun `EXPECT assets to be mapped to collectibles WHEN assets are cached`() = runTest {
        whenever(getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION)
        whenever(getAssetDetail(ASSET_ID_1)).thenReturn(ASSET_DETAIL_1)
        whenever(getAssetDetail(ASSET_ID_2)).thenReturn(ASSET_DETAIL_2)
        whenever(baseOwnedCollectibleDataFactory(ASSET_HOLDING_1, ASSET_DETAIL_1)).thenReturn(COLLECTIBLE_DATA)

        val result = sut(ADDRESS).first()

        verify(baseOwnedCollectibleDataFactory, never()).invoke(ASSET_HOLDING_2, ASSET_DETAIL_2)
        assertEquals(listOf(COLLECTIBLE_DATA), result)
    }

    private companion object {
        const val ADDRESS = "address"

        const val ASSET_ID_1 = 1L
        const val ASSET_ID_2 = 2L

        val ASSET_HOLDING_1 = fixtureOf<AssetHolding>().copy(assetId = ASSET_ID_1)
        val ASSET_HOLDING_2 = fixtureOf<AssetHolding>().copy(assetId = ASSET_ID_2)

        val ASSET_HOLDINGS = listOf<AssetHolding>(ASSET_HOLDING_1, ASSET_HOLDING_2)

        val ASSET_DETAIL_1 = fixtureOf<AssetDetail>().copy(
            collectible = fixtureOf<Collectible>()
        )
        val ASSET_DETAIL_2 = fixtureOf<AssetDetail>().copy(
            collectible = null
        )
        val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>().copy(
            assetHoldings = ASSET_HOLDINGS
        )
        val COLLECTIBLE_DATA = fixtureOf<BaseOwnedCollectibleData>()
    }
}
