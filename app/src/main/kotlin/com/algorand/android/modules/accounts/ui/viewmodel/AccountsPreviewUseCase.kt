/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accounts.ui.viewmodel

import com.algorand.android.mapper.AccountPreviewMapper
import com.algorand.android.modules.accounts.domain.mapper.PortfolioValueItemMapper
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.CurrencyCachingError
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Data
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.EmptyLocalAccounts
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Idle
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Loading
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.modules.accounts.ui.model.AccountPreview
import com.algorand.android.modules.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.modules.peraconnectivitymanager.ui.PeraConnectivityManager
import com.algorand.android.utils.CacheResult
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.banner.domain.usecase.GetBannerFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.privacy.domain.usecase.GetPrivacyModeFlow
import com.algorand.wallet.spotbanner.domain.model.SpotBannerFlowData
import com.algorand.wallet.spotbanner.domain.usecase.GetSpotBannersFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@Suppress("LongParameterList")
class AccountsPreviewUseCase @Inject constructor(
    private val accountPreviewMapper: AccountPreviewMapper,
    private val portfolioValueItemMapper: PortfolioValueItemMapper,
    private val peraConnectivityManager: PeraConnectivityManager,
    private val accountPreviewProcessor: AccountPreviewProcessor,
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val getPrivacyModeFlow: GetPrivacyModeFlow,
    private val getBannerFlow: GetBannerFlow,
    private val getSpotBannersFlow: GetSpotBannersFlow,
    private val getInboxMessagesFlow: GetInboxMessagesFlow,
    private val getLocalAccounts: GetLocalAccounts
) {

    suspend fun getInitialAccountPreview(): AccountPreview {
        val isDeviceConnectedToInternet = peraConnectivityManager.isConnectedToInternet()
        return if (isDeviceConnectedToInternet) {
            accountPreviewMapper.getFullScreenLoadingState()
        } else {
            accountPreviewMapper.getAllAccountsErrorState(
                accountListItems = accountPreviewProcessor.createAccountErrorItemList(getLocalAccounts()),
                errorCode = null,
                errorPortfolioValueItem = portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAccountPreviewFlow(initialState: AccountPreview): Flow<AccountPreview> {
        var lastState: AccountPreview = initialState
        return getAccountLiteCacheFlow().flatMapLatest {
            when (it) {
                is CurrencyCachingError -> getAlgoPriceErrorState(selectedCurrencyDetailCache = it.error, lastState)
                is Data -> getAccountPreviewInitializationFlow(it)
                Loading, Idle -> flowOf(accountPreviewMapper.getFullScreenLoadingState())
                EmptyLocalAccounts -> flowOf(accountPreviewMapper.getEmptyAccountListState())
            }
        }.mapLatest {
            lastState = it
            it
        }
    }

    private suspend fun getAccountPreviewInitializationFlow(accountLiteCacheData: Data): Flow<AccountPreview> {
        return combine(
            getBannerFlow(),
            getSpotBannersFlow(getSpotBannerFlowData(accountLiteCacheData)),
            getPrivacyModeFlow(),
            getInboxMessagesFlow()
        ) { banner, spotBanners, privacyMode, _ ->
            accountPreviewProcessor.prepareAccountPreview(
                accountLiteCacheData.localAccounts,
                accountLiteCacheData.accountLites,
                banner,
                privacyMode,
                spotBanners
            )
        }
    }

    private fun getSpotBannerFlowData(accountLiteCacheData: Data): List<SpotBannerFlowData> {
        return accountLiteCacheData.accountLites.values.map { lite ->
            with(lite) {
                SpotBannerFlowData(address, isBackedUp, cachedInfo?.primaryAccountValue, cachedInfo?.type)
            }
        }
    }

    private suspend fun getAlgoPriceErrorState(
        selectedCurrencyDetailCache: CacheResult.Error<SelectedCurrencyDetail>?,
        previousState: AccountPreview
    ): Flow<AccountPreview> {
        val hasPreviousCachedValue = selectedCurrencyDetailCache?.data != null
        if (hasPreviousCachedValue) return flowOf(previousState)
        val accountErrorListItems = accountPreviewProcessor.createAccountErrorItemList(getLocalAccounts())
        val portfolioValuesError = portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
        val preview = accountPreviewMapper.getAllAccountsErrorState(
            accountListItems = accountErrorListItems,
            errorCode = selectedCurrencyDetailCache?.code,
            errorPortfolioValueItem = portfolioValuesError
        )
        return flowOf(preview)
    }
}
