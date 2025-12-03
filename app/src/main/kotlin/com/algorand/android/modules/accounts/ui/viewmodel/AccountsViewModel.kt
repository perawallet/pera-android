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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accounts.domain.model.BasePortfolioValueItem
import com.algorand.android.modules.accounts.ui.model.AccountPreview
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.modules.accounts.ui.view.AccountsFragmentArgs
import com.algorand.android.modules.tutorialdialog.data.model.Tutorial
import com.algorand.android.modules.tutorialdialog.domain.usecase.TutorialUseCase
import com.algorand.android.notification.domain.usecase.GetAskNotificationPermissionEventFlowUseCase
import com.algorand.android.ui.accounts.tracker.AccountsEventTracker
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.utils.coremanager.ParityManager
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.custom.domain.usecase.GetNotBackedUpAccounts
import com.algorand.wallet.banner.domain.usecase.DismissBanner
import com.algorand.wallet.privacy.domain.usecase.TogglePrivacyMode
import com.algorand.wallet.spotbanner.domain.usecase.DismissSpotBanner
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@SuppressWarnings("LongParameterList")
@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountsPreviewUseCase: AccountsPreviewUseCase,
    private val accountsEventTracker: AccountsEventTracker,
    private val parityManager: ParityManager,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase,
    private val getNotBackedUpAccounts: GetNotBackedUpAccounts,
    private val tutorialUseCase: TutorialUseCase,
    private val getAskNotificationPermissionEventFlowUseCase: GetAskNotificationPermissionEventFlowUseCase,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val togglePrivacyMode: TogglePrivacyMode,
    private val dismissBannerById: DismissBanner,
    private val dismissSpotBannerById: DismissSpotBanner,
    savedStateHandle: SavedStateHandle
) : ViewModel(), EventViewModel<AccountsViewModel.ViewEvent> by eventDelegate,
    AccountsEventTracker by accountsEventTracker {

    private val args = AccountsFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val _accountPreviewFlow = MutableStateFlow<AccountPreview?>(null)
    val accountPreviewFlow: Flow<AccountPreview?>
        get() = _accountPreviewFlow.asStateFlow()

    private var tutorialJob: Job? = null

    private var initializationJob: Job? = null

    private var hasPlayedConfetti = false

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
                        Tutorial.PRIVACY_MODE -> ViewEvent.ShowPrivacyTooltip(tutorial.id)
                    }
                    eventDelegate.sendEvent(tutorialEvent)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun refreshCachedAlgoPrice() {
        viewModelScope.launch {
            parityManager.refreshSelectedCurrencyDetailCache()
        }
    }

    fun dismissBanner(bannerId: Long) {
        viewModelScope.launch {
            dismissBannerById(bannerId)
        }
    }

    fun dismissSpotBanner(bannerId: Long) {
        viewModelScope.launch {
            dismissSpotBannerById(bannerId)
        }
    }

    fun dismissTutorial(tutorialId: Int) {
        viewModelScope.launch {
            tutorialUseCase.dismissTutorial(tutorialId)
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

    fun navigateToAddAccount() {
        viewModelScope.launchIO {
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

    fun getPortfolioValueItem(): BasePortfolioValueItem? {
        return _accountPreviewFlow.value?.portfolioValueItem
    }

    fun checkConfettiState() {
        viewModelScope.launchIO {
            if (!hasPlayedConfetti && args.showConfetti) {
                hasPlayedConfetti = true
                eventDelegate.sendEvent(ViewEvent.ShowConfetti)
            }
        }
    }

    fun togglePrivacy() {
        viewModelScope.launch {
            togglePrivacyMode()
        }
    }

    sealed interface ViewEvent {
        data object NavToLoginNavigation : ViewEvent
        data object ShowMaxAccountLimitExceededError : ViewEvent
        data class NavigateToBackupPassphraseInfo(val addresses: Set<String>) : ViewEvent
        data class ShowGiftCardsTutorial(val tutorialId: Int) : ViewEvent
        data class ShowAccountAddressCopyTutorial(val tutorialId: Int) : ViewEvent
        data class ShowSwapTutorial(val tutorialId: Int) : ViewEvent
        data object ShowNotificationPermission : ViewEvent
        data object ShowConfetti : ViewEvent
        data class ShowPrivacyTooltip(val tutorialId: Int) : ViewEvent
    }
}
