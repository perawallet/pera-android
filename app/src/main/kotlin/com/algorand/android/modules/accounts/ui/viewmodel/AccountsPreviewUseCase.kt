/*
 * Copyright 2025 Pera Wallet, LDA
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

import androidx.navigation.NavDirections
import com.algorand.android.banner.domain.usecase.BannersUseCase
import com.algorand.android.mapper.AccountPreviewMapper
import com.algorand.android.modules.accounts.domain.mapper.PortfolioValueItemMapper
import com.algorand.android.modules.accounts.ui.model.AccountPreview
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLitesFlow
import com.algorand.android.modules.accounts.ui.view.AccountsFragmentDirections
import com.algorand.android.modules.accounts.ui.model.AccountsInitializationStatus.CurrencyDetailError
import com.algorand.android.modules.accounts.ui.model.AccountsInitializationStatus.EmptyAccounts
import com.algorand.android.modules.accounts.ui.model.AccountsInitializationStatus.Loading
import com.algorand.android.modules.accounts.ui.model.AccountsInitializationStatus.ReadyForInitialization
import com.algorand.android.modules.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.modules.peraconnectivitymanager.ui.PeraConnectivityManager
import com.algorand.android.modules.swap.utils.SwapNavigationDestinationHelper
import com.algorand.android.utils.CacheResult
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequestCountFlow
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class AccountsPreviewUseCase @Inject constructor(
    private val accountPreviewMapper: AccountPreviewMapper,
    private val bannersUseCase: BannersUseCase,
    private val portfolioValueItemMapper: PortfolioValueItemMapper,
    private val swapNavigationDestinationHelper: SwapNavigationDestinationHelper,
    private val peraConnectivityManager: PeraConnectivityManager,
    private val accountPreviewProcessor: AccountPreviewProcessor,
    private val getAccountsStateStatusFlow: GetAccountsStateStatusFlow,
    private val getAccountLitesFlow: GetAccountLitesFlow,
    private val getAssetInboxRequestCountFlow: GetAssetInboxRequestCountFlow
) {

    suspend fun getInitialAccountPreview(): AccountPreview {
        val isDeviceConnectedToInternet = peraConnectivityManager.isConnectedToInternet()
        return if (isDeviceConnectedToInternet) {
            accountPreviewMapper.getFullScreenLoadingState()
        } else {
            accountPreviewMapper.getAllAccountsErrorState(
                accountListItems = accountPreviewProcessor.createAccountErrorItemList(),
                errorCode = null,
                errorPortfolioValueItem = portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAccountPreviewFlow(initialState: AccountPreview): Flow<AccountPreview> {
        var lastState: AccountPreview = initialState
        return getAccountsStateStatusFlow().flatMapLatest {
            when (it) {
                Loading -> flowOf(accountPreviewMapper.getFullScreenLoadingState())
                EmptyAccounts -> flowOf(accountPreviewMapper.getEmptyAccountListState())
                is ReadyForInitialization -> getAccountPreviewInitializationFlow(it.accounts)
                is CurrencyDetailError -> getAlgoPriceErrorState(selectedCurrencyDetailCache = it.error, lastState)
            }
        }.map {
            lastState = it
            it
        }
    }

    private suspend fun getAccountPreviewInitializationFlow(localAccounts: List<LocalAccount>): Flow<AccountPreview> {
        return combine(
            getAccountLitesFlow(localAccounts, localAccounts.map { it.algoAddress }),
            bannersUseCase.getBanner(),
            getAssetInboxRequestCountFlow()
        ) { accountLites, banner, assetInboxCount ->
            accountPreviewProcessor.prepareAccountPreview(localAccounts, accountLites, banner, assetInboxCount)
        }
    }

    suspend fun dismissBanner(bannerId: Long) {
        bannersUseCase.dismissBanner(bannerId)
    }

    suspend fun getSwapNavigationDirection(): NavDirections? {
        var swapNavDirection: NavDirections? = null
        swapNavigationDestinationHelper.getSwapNavigationDestination(
            onNavToIntroduction = {
                swapNavDirection = AccountsFragmentDirections.actionAccountsFragmentToSwapIntroductionNavigation()
            },
            onNavToAccountSelection = {
                swapNavDirection = AccountsFragmentDirections.actionAccountsFragmentToSwapAccountSelectionNavigation()
            },
            onNavToSwap = { accountAddress ->
                swapNavDirection = AccountsFragmentDirections.actionAccountsFragmentToSwapNavigation(accountAddress)
            }
        )
        return swapNavDirection
    }

    private suspend fun getAlgoPriceErrorState(
        selectedCurrencyDetailCache: CacheResult.Error<SelectedCurrencyDetail>?,
        previousState: AccountPreview
    ): Flow<AccountPreview> {
        val hasPreviousCachedValue = selectedCurrencyDetailCache?.data != null
        if (hasPreviousCachedValue) return flowOf(previousState)
        val accountErrorListItems = accountPreviewProcessor.createAccountErrorItemList()
        val portfolioValuesError = portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
        val preview = accountPreviewMapper.getAllAccountsErrorState(
            accountListItems = accountErrorListItems,
            errorCode = selectedCurrencyDetailCache?.code,
            errorPortfolioValueItem = portfolioValuesError
        )
        return flowOf(preview)
    }
}
