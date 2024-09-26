/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accounts.domain.usecase

import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.appcache.model.AppCacheStatus
import com.algorand.android.module.appcache.usecase.GetAppCacheStatusFlow
import com.algorand.android.module.banner.domain.model.Banner
import com.algorand.android.module.banner.domain.usecase.DismissBanner
import com.algorand.android.module.banner.domain.usecase.GetBannerFlow
import com.algorand.android.module.caching.CacheResult
import com.algorand.android.mapper.AccountPreviewMapper
import com.algorand.android.modules.accounts.domain.mapper.PortfolioValueItemMapper
import com.algorand.android.modules.accounts.domain.model.AccountPreview
import com.algorand.android.modules.accounts.ui.AccountsFragmentDirections
import com.algorand.android.modules.peraconnectivitymanager.ui.PeraConnectivityManager
import com.algorand.android.modules.tutorialdialog.data.model.Tutorial
import com.algorand.android.modules.tutorialdialog.domain.usecase.TutorialUseCase
import com.algorand.android.module.node.domain.usecase.GetActiveNodeAsFlow
import com.algorand.android.module.node.domain.usecase.IsSelectedNodeTestnet
import com.algorand.android.module.notification.domain.usecase.GetAskNotificationPermissionEventFlowUseCase
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.usecase.GetSelectedCurrencyDetailFlow
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.AccountSelection
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Introduction
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Swap
import com.algorand.android.module.swap.component.common.usecase.GetSwapNavigationDestination
import com.algorand.android.utils.Event
import com.algorand.android.utils.combine
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

// TODO Refactor this class for performance and code quality
@Suppress("LongParameterList")
class AccountsPreviewUseCase @Inject constructor(
    private val getSelectedCurrencyDetailFlow: GetSelectedCurrencyDetailFlow,
    private val accountPreviewMapper: AccountPreviewMapper,
    private val portfolioValueItemMapper: PortfolioValueItemMapper,
    private val tutorialUseCase: TutorialUseCase,
    private val getAskNotificationPermissionEventFlowUseCase: GetAskNotificationPermissionEventFlowUseCase,
    private val peraConnectivityManager: PeraConnectivityManager,
    private val getLocalAccounts: GetLocalAccounts,
    private val getAppCacheStatusFlow: GetAppCacheStatusFlow,
    private val accountPreviewProcessor: AccountPreviewProcessor,
    private val getActiveNodeAsFlow: GetActiveNodeAsFlow,
    private val isSelectedNodeTestnet: IsSelectedNodeTestnet,
    private val getBannerFlow: GetBannerFlow,
    private val dismissBanner: DismissBanner,
    private val getSwapNavigationDestination: GetSwapNavigationDestination
) {

    suspend fun dismissTutorial(tutorialId: Int) {
        tutorialUseCase.dismissTutorial(tutorialId)
    }

    suspend fun getInitialAccountPreview(): AccountPreview {
        val isTestnetBadgeVisible = isSelectedNodeTestnet()
        val isDeviceConnectedToInternet = peraConnectivityManager.isConnectedToInternet()
        return if (isDeviceConnectedToInternet) {
            accountPreviewMapper.getFullScreenLoadingState(isTestnetBadgeVisible)
        } else {
            accountPreviewMapper.getAllAccountsErrorState(
                accountListItems = accountPreviewProcessor.createAccountErrorItemList(),
                errorCode = null,
                isTestnetBadgeVisible = isTestnetBadgeVisible,
                errorPortfolioValueItem = portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
            )
        }
    }

    suspend fun getAccountsPreview(initialState: AccountPreview): Flow<AccountPreview> {
        var lastState: AccountPreview = initialState
        return combine(
            getSelectedCurrencyDetailFlow(),
            getAppCacheStatusFlow(),
            getBannerFlow(),
            tutorialUseCase.getTutorial(),
            getAskNotificationPermissionEventFlowUseCase.invoke(),
            getActiveNodeAsFlow(),
        ) { selectedCurrencyParityCache, cacheStatus, banner, tutorial, notificationPermissionEvent, _ ->
            val isTestnetBadgeVisible = isSelectedNodeTestnet()
            if (getLocalAccounts().isEmpty()) {
                return@combine accountPreviewMapper.getEmptyAccountListState(isTestnetBadgeVisible)
            }
            when (selectedCurrencyParityCache) {
                is CacheResult.Success -> {
                    processSuccessAccountCacheAndOthers(
                        cacheStatus = cacheStatus,
                        banner = banner,
                        isTestnetBadgeVisible = isTestnetBadgeVisible,
                        tutorial = tutorial,
                        notificationPermissionEvent = notificationPermissionEvent?.data
                    )
                }

                is CacheResult.Error -> getAlgoPriceErrorState(
                    selectedCurrencyDetailCache = selectedCurrencyParityCache,
                    previousState = lastState,
                    isTestnetBadgeVisible = isTestnetBadgeVisible
                )

                else -> accountPreviewMapper.getFullScreenLoadingState(isTestnetBadgeVisible)
            }.also { lastState = it }
        }
    }

    suspend fun onCloseBannerClick(bannerId: Long) {
        dismissBanner(bannerId)
    }

    suspend fun getSwapNavigationUpdatedPreview(previousState: AccountPreview): AccountPreview {
        val navDestination = when (val destination = getSwapNavigationDestination(null)) {
            AccountSelection -> AccountsFragmentDirections.actionAccountsFragmentToSwapAccountSelectionNavigation()
            Introduction -> AccountsFragmentDirections.actionAccountsFragmentToSwapIntroductionNavigation()
            is Swap -> AccountsFragmentDirections.actionAccountsFragmentToSwapNavigation(destination.address)
        }
        return previousState.copy(swapNavigationDestinationEvent = Event(navDestination))
    }

    fun getGiftCardsNavigationUpdatedPreview(previousState: AccountPreview): AccountPreview {
        return previousState.copy(
            giftCardsNavigationDestinationEvent = Event(
                AccountsFragmentDirections.actionAccountsFragmentToBidaliNavigation()
            )
        )
    }

    private suspend fun getAlgoPriceErrorState(
        selectedCurrencyDetailCache: CacheResult.Error<SelectedCurrencyDetail>?,
        previousState: AccountPreview,
        isTestnetBadgeVisible: Boolean
    ): AccountPreview {
        val hasPreviousCachedValue = selectedCurrencyDetailCache?.previouslyCachedData != null
        if (hasPreviousCachedValue) return previousState
        val accountErrorListItems = accountPreviewProcessor.createAccountErrorItemList()
        val portfolioValuesError = portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
        return accountPreviewMapper.getAllAccountsErrorState(
            accountListItems = accountErrorListItems,
            errorCode = selectedCurrencyDetailCache?.code,
            isTestnetBadgeVisible = isTestnetBadgeVisible,
            errorPortfolioValueItem = portfolioValuesError
        )
    }

    private suspend fun processSuccessAccountCacheAndOthers(
        cacheStatus: AppCacheStatus,
        banner: Banner?,
        isTestnetBadgeVisible: Boolean,
        tutorial: Tutorial?,
        notificationPermissionEvent: Event<Unit>?
    ): AccountPreview {
        val isCacheNotAvailable = cacheStatus != AppCacheStatus.INITIALIZED
        return if (isCacheNotAvailable) {
            accountPreviewMapper.getFullScreenLoadingState(isTestnetBadgeVisible)
        } else {
            accountPreviewProcessor.prepareAccountPreview(
                banner = banner,
                isTestnetBadgeVisible = isTestnetBadgeVisible,
                tutorial = tutorial,
                notificationPermissionEvent = notificationPermissionEvent
            )
        }
    }
}
