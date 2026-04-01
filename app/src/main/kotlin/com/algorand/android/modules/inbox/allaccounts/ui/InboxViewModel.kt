/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.modules.inbox.allaccounts.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.inbox.allaccounts.domain.usecase.GetInboxViewState
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewEvent
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewState
import com.algorand.android.utils.launchIO
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountInboxEventTracker
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.inbox.domain.usecase.SetInboxLastOpenedTime
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val getInboxViewState: GetInboxViewState,
    private val getInboxMessagesFlow: GetInboxMessagesFlow,
    private val refreshInboxCache: RefreshInboxCache,
    private val setInboxLastOpenedTime: SetInboxLastOpenedTime,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val stateDelegate: StateDelegate<InboxViewState>,
    private val eventDelegate: EventDelegate<InboxViewEvent>,
    private val jointAccountInboxEventTracker: JointAccountInboxEventTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateViewModel<InboxViewState> by stateDelegate,
    EventViewModel<InboxViewEvent> by eventDelegate {

    private val filterAccountAddress: String? = savedStateHandle[FILTER_ACCOUNT_ADDRESS_KEY]

    private var refreshJob: Job? = null
    private var jointAccountAddressToOpen: String? = null
    private var isJointAccountImportHandled = false

    init {
        stateDelegate.setDefaultState(InboxViewState.Loading)
    }

    fun initializePreview(jointAccountAddress: String? = null) {
        jointAccountAddressToOpen = jointAccountAddress
        refreshJob?.cancel()
        refreshJob = viewModelScope.launchIO {
            setInboxLastOpenedTime(ZonedDateTime.now())
            refreshInboxCache()
            fetchInboxPreview()
        }
    }

    private suspend fun fetchInboxPreview() {
        getInboxMessagesFlow()
            .map { inboxMessages -> getInboxViewState(inboxMessages, filterAccountAddress) }
            .map { viewState -> applyFeatureToggle(viewState) }
            .collectLatest { viewState ->
                stateDelegate.updateState { viewState }
                handleJointAccountDeepLinkIfNeeded(viewState)
            }
    }

    private fun applyFeatureToggle(viewState: InboxViewState): InboxViewState {
        val isJointAccountEnabled = isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)
        return if (isJointAccountEnabled) {
            viewState
        } else {
            when (viewState) {
                is InboxViewState.Content -> {
                    val filtered = viewState.copy(
                        signatureRequestList = emptyList(),
                        jointAccountInvitationList = emptyList()
                    )
                    if (filtered.inboxWithAccountList.isEmpty()) {
                        InboxViewState.Empty
                    } else {
                        filtered
                    }
                }

                else -> viewState
            }
        }
    }

    private suspend fun handleJointAccountDeepLinkIfNeeded(viewState: InboxViewState) {
        if (isJointAccountImportHandled) return

        val addressToOpen = jointAccountAddressToOpen ?: return
        if (viewState !is InboxViewState.Content) return

        isJointAccountImportHandled = true

        val invitation = viewState.jointAccountInvitationList.firstOrNull {
            it.accountAddress == addressToOpen
        }

        if (invitation != null) {
            eventDelegate.sendEvent(
                InboxViewEvent.NavigateToJointAccountDetail(
                    accountAddress = invitation.accountAddress,
                    threshold = invitation.threshold,
                    participantAddresses = invitation.participantAddresses
                )
            )
        } else {
            eventDelegate.sendEvent(InboxViewEvent.NavigateToJointAccountDetail(addressToOpen))
        }
    }

    fun logInviteClick() {
        viewModelScope.launchIO {
            jointAccountInboxEventTracker.logInboxJointAccountInvitePress()
        }
    }

    fun logPendingTxClick() {
        viewModelScope.launchIO {
            jointAccountInboxEventTracker.logInboxJointAccountPendingTxPress()
        }
    }

    private companion object {
        const val FILTER_ACCOUNT_ADDRESS_KEY = "filterAccountAddress"
    }
}
