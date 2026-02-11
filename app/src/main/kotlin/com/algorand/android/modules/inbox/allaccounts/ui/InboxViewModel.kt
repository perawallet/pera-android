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
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewEvent
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewState
import com.algorand.android.utils.launchIO
import com.algorand.wallet.inbox.asset.domain.model.AssetInboxRequest
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
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
    private val dependencies: InboxViewModelDependencies,
    private val stateDelegate: StateDelegate<InboxViewState>,
    private val eventDelegate: EventDelegate<InboxViewEvent>,
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
            dependencies.setInboxLastOpenedTime(ZonedDateTime.now())
            dependencies.refreshInboxCache()
            fetchInboxPreview()
        }
    }

    private suspend fun fetchInboxPreview() {
        dependencies.getInboxMessagesFlow()
            .map { inboxMessages -> mapToViewState(inboxMessages) }
            .map { viewState -> applyFeatureToggle(viewState) }
            .collectLatest { viewState ->
                stateDelegate.updateState { viewState }
                handleJointAccountDeepLinkIfNeeded(viewState)
            }
    }

    private suspend fun mapToViewState(inboxMessages: InboxMessages?): InboxViewState {
        val allAccountAddresses = dependencies.getInboxValidAddresses()
        val lastOpenedTime = dependencies.getInboxLastOpenedTime()

        if (allAccountAddresses.isEmpty()) {
            return InboxViewState.Empty
        }

        val filteredInboxMessages = filterInboxMessages(inboxMessages, filterAccountAddress)
        val assetInboxRequests = parseAssetInboxes(filteredInboxMessages)

        val displayAddresses = if (filterAccountAddress != null) {
            listOf(filterAccountAddress)
        } else {
            allAccountAddresses
        }

        return dependencies.inboxViewStateMapper.mapToViewState(
            assetInboxList = assetInboxRequests,
            addresses = displayAddresses,
            inboxMessages = filteredInboxMessages,
            lastOpenedTime = lastOpenedTime,
            filterAccountAddress = filterAccountAddress,
            localAccountAddresses = allAccountAddresses
        )
    }

    private fun filterInboxMessages(
        inboxMessages: InboxMessages?,
        filterAccountAddress: String?
    ): InboxMessages? {
        if (filterAccountAddress == null || inboxMessages == null) return inboxMessages

        return InboxMessages(
            jointAccountImportRequests = inboxMessages.jointAccountImportRequests?.filter { jointAccount ->
                jointAccount.participantAddresses?.contains(filterAccountAddress) == true
            },
            jointAccountSignRequests = inboxMessages.jointAccountSignRequests?.filter { signRequest ->
                val isJointAccount = signRequest.jointAccount?.address == filterAccountAddress
                val isParticipant =
                    signRequest.jointAccount?.participantAddresses?.contains(filterAccountAddress) == true
                isJointAccount || isParticipant
            },
            assetInboxes = inboxMessages.assetInboxes?.filter { assetInbox ->
                assetInbox.address == filterAccountAddress
            }
        )
    }

    private fun parseAssetInboxes(inboxMessages: InboxMessages?): List<AssetInboxRequest> {
        return inboxMessages?.assetInboxes?.map { assetInbox ->
            AssetInboxRequest(
                address = assetInbox.address,
                requestCount = assetInbox.requestCount
            )
        } ?: emptyList()
    }

    private fun applyFeatureToggle(viewState: InboxViewState): InboxViewState {
        val isJointAccountEnabled = dependencies.isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)
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
            eventDelegate.sendEvent(InboxViewEvent.NavigateToJointAccountInvitation(invitation))
        } else {
            eventDelegate.sendEvent(InboxViewEvent.NavigateToJointAccountDetail(addressToOpen))
        }
    }

    private companion object {
        const val FILTER_ACCOUNT_ADDRESS_KEY = "filterAccountAddress"
    }
}
