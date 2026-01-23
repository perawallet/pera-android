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

import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountdetail.jointaccountdetail.domain.usecase.CreateJointAccountParticipantItem
import com.algorand.android.modules.accountdetail.jointaccountdetail.ui.model.JointAccountParticipantItem
import com.algorand.android.repository.ContactRepository
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessages
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import javax.inject.Inject
import javax.inject.Named

internal class DefaultJointAccountDetailProcessor @Inject constructor(
    private val getJointAccount: GetJointAccount,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val contactRepository: ContactRepository,
    private val createJointAccountParticipantItem: CreateJointAccountParticipantItem,
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    private val getInboxMessages: GetInboxMessages,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    @param:Named(InboxApiRepository.INJECTION_NAME)
    private val inboxApiRepository: InboxApiRepository
) : JointAccountDetailProcessor {

    override suspend fun createContentState(
        jointAccount: LocalAccount.Joint,
        accountAddress: String,
        showActions: Boolean
    ): JointAccountDetailViewModel.ViewState.Content {
        val accountDisplayName = getAccountDisplayName(accountAddress)
        val participants = createParticipantItems(jointAccount.participantAddresses)

        return JointAccountDetailViewModel.ViewState.Content(
            accountDisplayName = accountDisplayName.primaryDisplayName,
            accountAddressShortened = accountAddress.toShortenedAddress(),
            numberOfAccounts = jointAccount.participantAddresses.size,
            threshold = jointAccount.threshold,
            participants = participants,
            showActions = showActions
        )
    }

    override suspend fun createContentStateFromInvitation(
        participantAddresses: List<String>,
        threshold: Int,
        accountAddress: String
    ): JointAccountDetailViewModel.ViewState.Content {
        val participants = createParticipantItems(participantAddresses)

        return JointAccountDetailViewModel.ViewState.Content(
            accountDisplayName = "",
            accountAddressShortened = accountAddress.toShortenedAddress(),
            numberOfAccounts = participantAddresses.size,
            threshold = threshold,
            participants = participants,
            showActions = true
        )
    }

    override suspend fun fetchInvitationFromInbox(
        accountAddress: String
    ): JointAccountDetailProcessor.InvitationResult {
        val inboxMessages = getInboxMessages() ?: return JointAccountDetailProcessor.InvitationResult.NotFound
        return parseInvitationFromInboxMessages(inboxMessages, accountAddress)
    }

    override suspend fun createParticipantItems(
        participantAddresses: List<String>
    ): List<JointAccountParticipantItem> {
        val localAccountAddresses = getLocalAccountsAddresses()
        return participantAddresses.map { address ->
            createJointAccountParticipantItem(address, localAccountAddresses)
        }
    }

    override suspend fun deleteInboxNotification(accountAddress: String) {
        val deviceId = getSelectedNodeDeviceId()?.toLongOrNull() ?: return
        inboxApiRepository.deleteJointInvitationNotification(deviceId, accountAddress)
    }

    override suspend fun isJointAccountExists(accountAddress: String): Boolean {
        return getJointAccount(accountAddress) != null
    }

    override suspend fun getContactEditInfo(address: String): JointAccountDetailProcessor.ContactEditInfo? {
        val contact = contactRepository.getContactByAddress(address) ?: return null
        return JointAccountDetailProcessor.ContactEditInfo(
            contactName = contact.name,
            contactPublicKey = contact.publicKey,
            contactDatabaseId = contact.contactDatabaseId,
            contactProfileImageUri = contact.imageUriAsString
        )
    }

    private fun parseInvitationFromInboxMessages(
        inboxMessages: InboxMessages,
        accountAddress: String
    ): JointAccountDetailProcessor.InvitationResult {
        val jointAccount = inboxMessages.jointAccountImportRequests
            ?.firstOrNull { it.address == accountAddress }
            ?: return JointAccountDetailProcessor.InvitationResult.NotFound

        val participantAddresses = jointAccount.participantAddresses
        val threshold = jointAccount.threshold

        if (participantAddresses.isNullOrEmpty() || threshold == null) {
            return JointAccountDetailProcessor.InvitationResult.NotFound
        }

        return JointAccountDetailProcessor.InvitationResult.Success(
            JointAccountDetailProcessor.InvitationData(
                threshold = threshold,
                participantAddresses = participantAddresses
            )
        )
    }
}
