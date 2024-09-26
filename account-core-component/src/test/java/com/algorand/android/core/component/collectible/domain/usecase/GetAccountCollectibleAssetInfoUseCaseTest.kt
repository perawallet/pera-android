package com.algorand.android.module.account.core.component.collectible.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.*
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.module.account.core.component.collectible.domain.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class GetAccountCollectibleAssetInfoUseCaseTest {

    private val getAccountInformation: GetAccountInformation = mock()
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory = mock()

    private val sut = GetAccountCollectibleDetailUseCase(getAccountInformation, baseOwnedCollectibleDataFactory)

    @Test
    fun `EXPECT null WHEN account information returns null`() = runTest {
        whenever(getAccountInformation(ADDRESS)).thenReturn(null)

        val result = sut(ADDRESS, BASE_COLLECTIBLE_DETAIL)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN account information does not have collectible`() = runTest {
        val accountInformation = ACCOUNT_INFORMATION.copy(
            assetHoldings = emptyList()
        )
        whenever(getAccountInformation(ADDRESS)).thenReturn(accountInformation)

        val result = sut(ADDRESS, BASE_COLLECTIBLE_DETAIL)

        assertNull(result)
    }

    @Test
    fun `EXPECT BaseOwnedCollectibleData WHEN account information is cached and has collectible`() = runTest {
        whenever(getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION)
        whenever(baseOwnedCollectibleDataFactory(ASSET_HOLDING, BASE_COLLECTIBLE_DETAIL))
            .thenReturn(BASE_OWNED_COLLECTIBLE_DATA)

        val result = sut(ADDRESS, BASE_COLLECTIBLE_DETAIL)

        assertEquals(BASE_OWNED_COLLECTIBLE_DATA, result)
    }

    private companion object {
        const val ADDRESS = "address"
        val BASE_COLLECTIBLE_DETAIL = fixtureOf<BaseCollectibleDetail>()
        val ASSET_HOLDING = fixtureOf<AssetHolding>().copy(
            assetId = BASE_COLLECTIBLE_DETAIL.assetId
        )
        val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>().copy(
            assetHoldings = listOf(ASSET_HOLDING)
        )
        val BASE_OWNED_COLLECTIBLE_DATA = fixtureOf<BaseOwnedCollectibleData>()
    }
}
