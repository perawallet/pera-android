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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class JointAccountInvitationDetailViewModel @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val inboxApiRepository: InboxApiRepository,
    private val refreshInboxCache: RefreshInboxCache,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs: JointAccountInvitationDetailNavArgs =
        checkNotNull(savedStateHandle[INVITATION_NAV_ARGS_KEY])

    private val invitation = createInvitation()

    private val _viewStateFlow = MutableStateFlow(JointAccountInvitationDetailViewState(invitation = invitation))
    val viewStateFlow: StateFlow<JointAccountInvitationDetailViewState> = _viewStateFlow.asStateFlow()

    init {
        loadAccountDetails()
    }

    private fun createInvitation(): JointAccountInvitationInboxItem {
        val creationTime = ZonedDateTime.now()
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
            _viewStateFlow.value = _viewStateFlow.value.copy(
                accountDisplayNames = displayNames,
                accountIcons = icons,
                isLoading = false
            )
        }
    }

    suspend fun rejectInvitation(): Boolean {
        return try {
            val deviceId = getSelectedNodeDeviceId()?.toLongOrNull()
            if (deviceId != null) {
                inboxApiRepository.deleteJointInvitationNotification(deviceId, navArgs.accountAddress)
            }
            refreshInboxCache()
            true
        } catch (e: Exception) {
            false
        }
    }

    private companion object {
        const val INVITATION_NAV_ARGS_KEY = "invitationNavArgs"
    }
}
