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

package com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountdetail.jointaccountdetail.ui.model.JointAccountParticipantItem
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JointAccountDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getJointAccount: GetJointAccount,
    private val processor: JointAccountDetailProcessor
) : ViewModel(),
    StateViewModel<JointAccountDetailViewModel.ViewState> by stateDelegate,
    EventViewModel<JointAccountDetailViewModel.ViewEvent> by eventDelegate {

    val accountAddress: String = savedStateHandle.getOrThrow(ACCOUNT_ADDRESS_KEY)
    private val thresholdArg: Int = savedStateHandle.get<Int>(THRESHOLD_KEY) ?: 0
    private val participantAddressesArg: List<String> =
        (savedStateHandle.get<Array<String>>(PARTICIPANT_ADDRESSES_KEY))?.toList().orEmpty()
    private val isFromInvitation: Boolean = thresholdArg > 0 || participantAddressesArg.isNotEmpty()

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        loadJointAccountInfo()
    }

    fun refreshParticipants() {
        viewModelScope.launch {
            stateDelegate.onState<ViewState.Content> { contentState ->
                if (contentState.participants.isNotEmpty()) {
                    val participantAddresses = contentState.participants.map { it.address }
                    val updatedParticipants = processor.createParticipantItems(participantAddresses)
                    stateDelegate.updateState {
                        contentState.copy(participants = updatedParticipants)
                    }
                }
            }
        }
    }

    fun onIgnoreClick() {
        viewModelScope.launch {
            processor.deleteInboxNotification(accountAddress)
            eventDelegate.sendEvent(ViewEvent.NavigateBack)
        }
    }

    fun onAddClick() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            if (contentState.threshold > 0 && contentState.participants.isNotEmpty()) {
                viewModelScope.launch {
                    processor.deleteInboxNotification(accountAddress)

                    if (processor.isJointAccountExists(accountAddress)) {
                        eventDelegate.sendEvent(ViewEvent.NavigateBack)
                    } else {
                        val participantAddresses = contentState.participants.map { it.address }
                        eventDelegate.sendEvent(
                            ViewEvent.NavigateToNameJointAccount(
                                threshold = contentState.threshold,
                                participantAddresses = participantAddresses
                            )
                        )
                    }
                }
            }
        }
    }

    fun onEditContactClick(address: String) {
        viewModelScope.launch {
            val contactInfo = processor.getContactEditInfo(address) ?: return@launch
            eventDelegate.sendEvent(
                ViewEvent.NavigateToEditContact(
                    contactName = contactInfo.contactName,
                    contactPublicKey = contactInfo.contactPublicKey,
                    contactDatabaseId = contactInfo.contactDatabaseId,
                    contactProfileImageUri = contactInfo.contactProfileImageUri
                )
            )
        }
    }

    private fun loadJointAccountInfo() {
        viewModelScope.launch {
            val localJointAccount = getJointAccount(accountAddress)

            when {
                localJointAccount != null && !isFromInvitation -> {
                    loadLocalAccountWithoutActions(localJointAccount)
                }
                localJointAccount != null && isFromInvitation -> {
                    loadLocalAccountWithActions(localJointAccount)
                }
                else -> {
                    loadInvitationInfo()
                }
            }
        }
    }

    private suspend fun loadLocalAccountWithoutActions(jointAccount: LocalAccount.Joint) {
        val contentState = processor.createContentState(jointAccount, accountAddress, showActions = false)
        stateDelegate.updateState { contentState }
    }

    private suspend fun loadLocalAccountWithActions(jointAccount: LocalAccount.Joint) {
        val contentState = processor.createContentState(jointAccount, accountAddress, showActions = true)
        stateDelegate.updateState { contentState }
    }

    private suspend fun loadInvitationInfo() {
        when (val result = getInvitationData()) {
            is JointAccountDetailProcessor.InvitationResult.Success -> {
                val invitation = result.data
                val contentState = processor.createContentStateFromInvitation(
                    participantAddresses = invitation.participantAddresses,
                    threshold = invitation.threshold,
                    accountAddress = accountAddress
                )
                stateDelegate.updateState { contentState }
            }
            is JointAccountDetailProcessor.InvitationResult.NotFound -> {
                stateDelegate.updateState { ViewState.Error(ErrorType.INVITATION_NOT_FOUND) }
            }
            is JointAccountDetailProcessor.InvitationResult.NetworkError -> {
                stateDelegate.updateState { ViewState.Error(ErrorType.NETWORK_ERROR) }
            }
        }
    }

    private suspend fun getInvitationData(): JointAccountDetailProcessor.InvitationResult {
        if (participantAddressesArg.isNotEmpty() && thresholdArg > 0) {
            return JointAccountDetailProcessor.InvitationResult.Success(
                JointAccountDetailProcessor.InvitationData(
                    threshold = thresholdArg,
                    participantAddresses = participantAddressesArg
                )
            )
        }

        return processor.fetchInvitationFromInbox(accountAddress)
    }

    enum class ErrorType {
        INVITATION_NOT_FOUND,
        NETWORK_ERROR
    }

    sealed interface ViewState {
        data object Loading : ViewState

        data class Content(
            val accountDisplayName: String,
            val accountAddressShortened: String,
            val numberOfAccounts: Int,
            val threshold: Int,
            val participants: List<JointAccountParticipantItem>,
            val showActions: Boolean
        ) : ViewState

        data class Error(val type: ErrorType) : ViewState
    }

    sealed interface ViewEvent {
        data object NavigateBack : ViewEvent
        data class NavigateToNameJointAccount(
            val threshold: Int,
            val participantAddresses: List<String>
        ) : ViewEvent

        data class NavigateToEditContact(
            val contactName: String?,
            val contactPublicKey: String?,
            val contactDatabaseId: Int,
            val contactProfileImageUri: String?
        ) : ViewEvent
    }

    companion object {
        const val ACCOUNT_ADDRESS_KEY = "accountAddress"
        const val THRESHOLD_KEY = "threshold"
        const val PARTICIPANT_ADDRESSES_KEY = "participantAddresses"
    }
}
