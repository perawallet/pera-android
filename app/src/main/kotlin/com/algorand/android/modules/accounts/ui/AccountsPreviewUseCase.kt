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

package com.algorand.android.modules.accounts.ui

import androidx.navigation.NavDirections
import com.algorand.android.banner.domain.model.BaseBanner
import com.algorand.android.banner.domain.usecase.BannersUseCase
import com.algorand.android.mapper.AccountPreviewMapper
import com.algorand.android.modules.accounts.domain.mapper.PortfolioValueItemMapper
import com.algorand.android.modules.accounts.domain.model.AccountPreview
import com.algorand.android.modules.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.modules.peraconnectivitymanager.ui.PeraConnectivityManager
import com.algorand.android.modules.swap.utils.SwapNavigationDestinationHelper
import com.algorand.android.modules.tutorialdialog.data.model.Tutorial
import com.algorand.android.modules.tutorialdialog.domain.usecase.TutorialUseCase
import com.algorand.android.notification.domain.usecase.GetAskNotificationPermissionEventFlowUseCase
import com.algorand.android.usecase.NodeSettingsUseCase
import com.algorand.android.utils.CacheResult
import com.algorand.android.utils.Event
import com.algorand.android.utils.combine
import com.algorand.wallet.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequestCountFlow
import com.algorand.wallet.cache.domain.model.AppCacheStatus
import com.algorand.wallet.cache.domain.usecase.GetAppCacheStatusFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@Suppress("LongParameterList")
class AccountsPreviewUseCase @Inject constructor(
    private val parityUseCase: ParityUseCase,
    private val accountPreviewMapper: AccountPreviewMapper,
    private val bannersUseCase: BannersUseCase,
    private val getAssetInboxRequestCountFlow: GetAssetInboxRequestCountFlow,
    private val nodeSettingsUseCase: NodeSettingsUseCase,
    private val portfolioValueItemMapper: PortfolioValueItemMapper,
    private val tutorialUseCase: TutorialUseCase,
    private val swapNavigationDestinationHelper: SwapNavigationDestinationHelper,
    private val getAskNotificationPermissionEventFlowUseCase: GetAskNotificationPermissionEventFlowUseCase,
    private val peraConnectivityManager: PeraConnectivityManager,
    private val getAppCacheStatusFlow: GetAppCacheStatusFlow,
    private val accountPreviewProcessor: AccountPreviewProcessor,
    private val getLocalAccounts: GetLocalAccounts,
    private val getAllAccountInformationFlow: GetAllAccountInformationFlow
) {

    suspend fun dismissTutorial(tutorialId: Int) {
        tutorialUseCase.dismissTutorial(tutorialId)
    }

    suspend fun getInitialAccountPreview(): AccountPreview {
        val isTestnetBadgeVisible = false
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
            getAllAccountInformationFlow(),
            parityUseCase.getSelectedCurrencyDetailCacheFlow(),
            getAppCacheStatusFlow(),
            bannersUseCase.getBanner(),
            getAssetInboxRequestCountFlow(),
            tutorialUseCase.getTutorial(),
            getAskNotificationPermissionEventFlowUseCase.invoke(),
            nodeSettingsUseCase.getAllNodeAsFlow()
        ) { _, selectedCurrencyParityCache, appCacheStatusFlow, banner, assetInboxCount, tutorial,
            notificationPermissionEvent, _ ->
            val isTestnetBadgeVisible = false
            if (getLocalAccounts().isEmpty()) {
                return@combine accountPreviewMapper.getEmptyAccountListState(isTestnetBadgeVisible)
            }
            when (selectedCurrencyParityCache) {
                is CacheResult.Success -> {
                    processAccountsAndAssets(
                        appCacheStatusFlow,
                        banner = banner,
                        assetInboxCount = assetInboxCount,
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
        bannersUseCase.dismissBanner(bannerId)
    }

    suspend fun getSwapNavigationUpdatedPreview(previousState: AccountPreview): AccountPreview {
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
        return swapNavDirection?.let { direction ->
            previousState.copy(swapNavigationDestinationEvent = Event(direction))
        } ?: previousState
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
        val hasPreviousCachedValue = selectedCurrencyDetailCache?.data != null
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

    private suspend fun processAccountsAndAssets(
        cacheStatus: AppCacheStatus,
        banner: BaseBanner?,
        assetInboxCount: Int,
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
                notificationPermissionEvent = notificationPermissionEvent,
                assetInboxCount = assetInboxCount
            )
        }
    }
}
