package com.algorand.android.module.account.core.component.polling.domain.usecase

import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.info.domain.model.*
import com.algorand.android.module.account.info.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.implementation.UpdateAccountCacheUseCase
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class UpdateAccountCacheUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mock()
    private val fetchAndCacheAccountInformation: FetchAndCacheAccountInformation = mock()
    private val fetchAndCacheAssets: FetchAndCacheAssets = mock()

    private val sut = UpdateAccountCacheUseCase(
        getLocalAccounts,
        fetchAndCacheAccountInformation,
        fetchAndCacheAssets
    )

    @Test
    fun `EXPECT fetch and cache account information & asset details WHEN repository returns account info`() = runTest {
        whenever(getLocalAccounts()).thenReturn(LOCAL_ACCOUNTS)
        whenever(fetchAndCacheAccountInformation(listOf(ADDRESS_1, ADDRESS_2))).thenReturn(ACCOUNT_INFORMATION_MAP)

        sut()

        verify(fetchAndCacheAssets).invoke(listOf(1, 2, 3, 4), false)
    }

    @Test
    fun `EXPECT accounts to be filtered out WHEN they are null`() = runTest {
        val accountInformationMap = mapOf(
            ADDRESS_1 to null,
            ADDRESS_2 to ACCOUNT_INFORMATION_2
        )
        whenever(getLocalAccounts()).thenReturn(LOCAL_ACCOUNTS)
        whenever(fetchAndCacheAccountInformation(listOf(ADDRESS_1, ADDRESS_2))).thenReturn(accountInformationMap)

        sut()

        verify(fetchAndCacheAssets).invoke(listOf(3, 4), false)

    }

    companion object {
        private const val ADDRESS_1 = "ADDRESS_1"
        private const val ADDRESS_2 = "ADDRESS_2"

        private val LOCAL_ACCOUNT_1 = fixtureOf<LocalAccount.Algo25>().copy(address = ADDRESS_1)
        private val LOCAL_ACCOUNT_2 = fixtureOf<LocalAccount.Algo25>().copy(address = ADDRESS_2)
        private val LOCAL_ACCOUNTS = listOf(LOCAL_ACCOUNT_1, LOCAL_ACCOUNT_2)

        private val ACCOUNT_1_ASSET_HOLDINGS = listOf(
            fixtureOf<AssetHolding>().copy(assetId = 1),
            fixtureOf<AssetHolding>().copy(assetId = 2)
        )
        private val ACCOUNT_INFORMATION_1 = fixtureOf<AccountInformation>().copy(
            assetHoldings = ACCOUNT_1_ASSET_HOLDINGS
        )
        private val ACCOUNT_2_ASSET_HOLDINGS = listOf(
            fixtureOf<AssetHolding>().copy(assetId = 3),
            fixtureOf<AssetHolding>().copy(assetId = 4)
        )
        private val ACCOUNT_INFORMATION_2 = fixtureOf<AccountInformation>().copy(
            assetHoldings = ACCOUNT_2_ASSET_HOLDINGS
        )
        private val ACCOUNT_INFORMATION_MAP = mapOf(
            ADDRESS_1 to ACCOUNT_INFORMATION_1,
            ADDRESS_2 to ACCOUNT_INFORMATION_2
        )
    }
}

/*
EXPECT to fetch and cache account information WHEN UpdateAccountCacheUseCase is invoked
EXPECT to fetch and cache asset details WHEN UpdateAccountCacheUseCase is invoked
EXPECT to return local account addresses WHEN UpdateAccountCacheUseCase is invoked
EXPECT to return asset IDs WHEN UpdateAccountCacheUseCase is invoked
EXPECT to handle null values in asset holdings WHEN UpdateAccountCacheUseCase is invoked
EXPECT to handle exceptions WHEN fetching and caching account information fails
EXPECT to handle exceptions WHEN fetching and caching asset details fails
 */