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

package com.algorand.android.modules.inbox.jointaccountinvitation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationDetailNavArgs
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationDetailViewState
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.utils.launchIO
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.utils.date.TimeProvider
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JointAccountInvitationDetailViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<JointAccountInvitationDetailViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val inboxApiRepository: InboxApiRepository,
    private val refreshInboxCache: RefreshInboxCache,
    private val timeProvider: TimeProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateViewModel<JointAccountInvitationDetailViewState> by stateDelegate,
    EventViewModel<JointAccountInvitationDetailViewModel.ViewEvent> by eventDelegate {

    private val navArgs: JointAccountInvitationDetailNavArgs =
        checkNotNull(savedStateHandle[INVITATION_NAV_ARGS_KEY])

    private val invitation = createInvitation()

    init {
        stateDelegate.setDefaultState(JointAccountInvitationDetailViewState.Loading)
        loadAccountDetails()
    }

    private fun createInvitation(): JointAccountInvitationInboxItem {
        val creationTime = timeProvider.getZonedDateTimeNow()
        return JointAccountInvitationInboxItem(
            id = "${navArgs.accountAddress}_${creationTime.toInstant().toEpochMilli()}",
            accountAddress = navArgs.accountAddress,
            accountAddressShortened = navArgs.accountAddressShortened,
            creationDateTime = creationTime,
            timeDifference = 0L,
            isRead = false,
            threshold = navArgs.threshold,
            participantAddresses = navArgs.participantAddresses
        )
    }

    private fun loadAccountDetails() {
        viewModelScope.launchIO {
            val allAddresses = listOf(navArgs.accountAddress) + navArgs.participantAddresses
            val displayNames = allAddresses.associateWith { address ->
                getAccountDisplayName(address)
            }
            val icons = allAddresses.associateWith { address ->
                getAccountIconDrawablePreview(address)
            }
            stateDelegate.updateState {
                JointAccountInvitationDetailViewState.Content(
                    invitation = invitation,
                    accountDisplayNames = displayNames,
                    accountIcons = icons
                )
            }
        }
    }

    fun onRejectClick() {
        viewModelScope.launchIO {
            val deviceId = getSelectedNodeDeviceId()?.toLongOrNull()
            if (deviceId != null) {
                inboxApiRepository.deleteJointInvitationNotification(deviceId, navArgs.accountAddress)
            }
            refreshInboxCache()
            eventDelegate.sendEvent(ViewEvent.InvitationIgnored)
        }
    }

    fun onAcceptClick() {
        viewModelScope.launch {
            eventDelegate.sendEvent(
                ViewEvent.NavigateToNameJointAccount(
                    threshold = navArgs.threshold,
                    participantAddresses = navArgs.participantAddresses
                )
            )
        }
    }

    sealed interface ViewEvent {
        data object InvitationIgnored : ViewEvent
        data object ShowError : ViewEvent
        data class NavigateToNameJointAccount(
            val threshold: Int,
            val participantAddresses: List<String>
        ) : ViewEvent
    }

    private companion object {
        const val INVITATION_NAV_ARGS_KEY = "invitationNavArgs"
    }
}
