package com.algorand.android.core.component.caching.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.accountinfo.component.domain.usecase.InitializeAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.InitializeAssets
import com.algorand.android.parity.domain.usecase.InitializeParityCache
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

internal class InitializeAppCacheUseCaseTest {

    private val initializeAccountInformation: InitializeAccountInformation = mock()
    private val initializeAssets: InitializeAssets = mock()
    private val initializeParityCache: InitializeParityCache = mock()
    private val appCacheStatusManager: AppCacheStatusManager = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = InitializeAppCacheUseCase(
        initializeAccountInformation,
        initializeAssets,
        initializeParityCache,
        appCacheStatusManager,
        getLocalAccounts
    )

    @Test
    fun `EXPECT app cache to be initialized and status updated`() = runTest {
        val localAccount = LocalAccount.NoAuth("address")
        val assetList = listOf(1L, 2L, 3L)
        val cachedAccountAssets = mapOf("address" to assetList)
        whenever(initializeAccountInformation(listOf("address"))).thenReturn(cachedAccountAssets)
        whenever(getLocalAccounts()).thenReturn(listOf(localAccount))

        sut()

        verify(initializeAssets).invoke(assetList)
        verify(initializeParityCache, times(1)).invoke()
        verify(appCacheStatusManager, times(1)).setStatus(LOADING)
        verify(appCacheStatusManager, times(1)).setStatus(INITIALIZED)
    }
}