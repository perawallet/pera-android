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

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.algorand.android.banner.domain.model.BannerType
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.accounts.ui.model.AccountPreview
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.modules.tracking.accounts.AccountsEventTracker
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.modules.tracking.core.PeraEvent
import com.algorand.android.modules.tutorialdialog.data.model.Tutorial
import com.algorand.android.modules.tutorialdialog.domain.usecase.TutorialUseCase
import com.algorand.android.notification.domain.usecase.GetAskNotificationPermissionEventFlowUseCase
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.utils.coremanager.ParityManager
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.custom.domain.usecase.GetNotBackedUpAccounts
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountsPreviewUseCase: AccountsPreviewUseCase,
    private val accountsEventTracker: AccountsEventTracker,
    private val parityManager: ParityManager,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase,
    private val peraEventTracker: PeraEventTracker,
    private val getNotBackedUpAccounts: GetNotBackedUpAccounts,
    private val tutorialUseCase: TutorialUseCase,
    private val getAskNotificationPermissionEventFlowUseCase: GetAskNotificationPermissionEventFlowUseCase,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), EventViewModel<AccountsViewModel.ViewEvent> by eventDelegate {

    private val _accountPreviewFlow = MutableStateFlow<AccountPreview?>(null)
    val accountPreviewFlow: Flow<AccountPreview?>
        get() = _accountPreviewFlow.asStateFlow()

    private var tutorialJob: Job? = null

    private var initializationJob: Job? = null

    init {
        initializeAccountPreviewFlow()
    }

    private fun initializeTutorials() {
        if (tutorialJob != null) return
        tutorialJob = viewModelScope.launch {
            combine(
                tutorialUseCase.getTutorial(),
                getAskNotificationPermissionEventFlowUseCase.invoke()
            ) { tutorial, notificationPermission ->
                if (notificationPermission?.data != null) {
                    eventDelegate.sendEvent(ViewEvent.ShowNotificationPermission)
                }
                if (tutorial != null) {
                    val tutorialEvent = when (tutorial) {
                        Tutorial.GIFT_CARDS -> ViewEvent.ShowGiftCardsTutorial(tutorial.id)
                        Tutorial.ACCOUNT_ADDRESS_COPY -> ViewEvent.ShowAccountAddressCopyTutorial(tutorial.id)
                        Tutorial.SWAP -> ViewEvent.ShowSwapTutorial(tutorial.id)
                    }
                    eventDelegate.sendEvent(tutorialEvent)
                }
            }
        }
    }

    fun refreshCachedAlgoPrice() {
        viewModelScope.launch {
            parityManager.refreshSelectedCurrencyDetailCache()
        }
    }

    fun dismissBanner(bannerId: Long) {
        viewModelScope.launch {
            accountsPreviewUseCase.dismissBanner(bannerId)
        }
    }

    fun logNotificationClick() {
        viewModelScope.launch {
            logEvent(PeraClickEvent.TAP_HOME_SCREEN_NOTIFICATION)
        }
    }

    fun logQrScanClick() {
        viewModelScope.launch {
            logEvent(PeraEvent.HOME_SCREEN_QR_SCAN)
        }
    }

    fun logSortClick() {
        viewModelScope.launch {
            logEvent(PeraClickEvent.TAP_HOME_SCREEN_SORT)
        }
    }

    fun logAlgoBuyClick() {
        viewModelScope.launch {
            accountsEventTracker.logAccountsFragmentAlgoBuyTapEvent()
        }
    }

    fun logBannerClick(bannerType: BannerType) {
        viewModelScope.launch {
            val eventName = when (bannerType) {
                BannerType.GOVERNANCE -> PeraClickEvent.TAP_HOME_BANNER_GOVERNANCE
                BannerType.STAKING -> PeraClickEvent.TAP_HOME_BANNER_STAKING
                BannerType.CARD -> PeraClickEvent.TAP_HOME_BANNER_CARD
                BannerType.GENERIC -> PeraClickEvent.TAP_HOME_BANNER_GENERIC
            }
            peraEventTracker.logEvent(eventName)
        }
    }

    fun dismissTutorial(tutorialId: Int) {
        viewModelScope.launch {
            tutorialUseCase.dismissTutorial(tutorialId)
        }
    }

    fun onSendTapEvent() {
        viewModelScope.launch {
            logEvent(PeraClickEvent.TAP_HOME_SCREEN_SEND)
        }
    }

    fun onSwapTapEvent() {
        viewModelScope.launch {
            logEvent(PeraClickEvent.TAP_HOME_SCREEN_SWAP)
            updatePreviewForSwapNavigation()
        }
    }

    fun onSwapClickFromTutorialDialog() {
        viewModelScope.launch {
            accountsEventTracker.logSwapTutorialTrySwapClickEvent()
            updatePreviewForSwapNavigation()
        }
    }

    fun logSwapLaterClick() {
        viewModelScope.launch {
            accountsEventTracker.logSwapLaterClickEvent()
        }
    }

    fun navigateToBackUpPassphraseInfo() {
        viewModelScope.launch {
            val notBackedUpAccounts = getNotBackedUpAccounts()
            if (notBackedUpAccounts.isNotEmpty()) {
                eventDelegate.sendEvent(ViewEvent.NavigateToBackupPassphraseInfo(notBackedUpAccounts))
            }
        }
    }

    fun onAddAccountClick() {
        viewModelScope.launchIO {
            accountsEventTracker.logAddAccountTapEvent()
            eventDelegate.sendEvent(
                if (isAccountLimitExceedUseCase.isAccountLimitExceed()) {
                    ViewEvent.ShowMaxAccountLimitExceededError
                } else {
                    ViewEvent.NavToLoginNavigation
                }
            )
        }
    }

    fun initializeAccountPreviewFlow() {
        if (initializationJob?.isActive == true) {
            initializationJob?.cancel()
        }
        initializationJob = viewModelScope.launchIO {
            val initialAccountPreview = accountsPreviewUseCase.getInitialAccountPreview()
            _accountPreviewFlow.emit(initialAccountPreview)
            accountsPreviewUseCase.getAccountPreviewFlow(initialAccountPreview).collectLatest {
                if (it.accountListItems.any { it is BaseAccountListItem.AccountSuccessItem }) {
                    initializeTutorials()
                }
                _accountPreviewFlow.emit(it)
            }
        }
    }

    private suspend fun updatePreviewForSwapNavigation() {
        accountsPreviewUseCase.getSwapNavigationDirection()?.let { navDirections ->
            eventDelegate.sendEvent(ViewEvent.NavigateToSwap(navDirections))
        }
    }

    sealed interface ViewEvent {
        data object NavToLoginNavigation : ViewEvent
        data object ShowMaxAccountLimitExceededError : ViewEvent
        data class NavigateToSwap(val navDirections: NavDirections) : ViewEvent
        data class NavigateToBackupPassphraseInfo(val addresses: Set<String>) : ViewEvent
        data class ShowGiftCardsTutorial(val tutorialId: Int) : ViewEvent
        data class ShowAccountAddressCopyTutorial(val tutorialId: Int) : ViewEvent
        data class ShowSwapTutorial(val tutorialId: Int) : ViewEvent
        data object ShowNotificationPermission : ViewEvent
    }
}
