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
import com.algorand.wallet.inbox.domain.model.InboxMessagesDTO
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import javax.inject.Inject

internal class DefaultJointAccountDetailProcessor @Inject constructor(
    private val getJointAccount: GetJointAccount,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val contactRepository: ContactRepository,
    private val createJointAccountParticipantItem: CreateJointAccountParticipantItem,
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
            participantAddresses = jointAccount.participantAddresses,
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
            participantAddresses = participantAddresses,
            showActions = true
        )
    }

    override suspend fun fetchInvitationFromInbox(
        accountAddress: String
    ): JointAccountDetailProcessor.InvitationResult {
        val addresses = createJointAccountParticipantItem.getLocalAccountAddresses()

        TODO("Implement this")
        return JointAccountDetailProcessor.InvitationResult.NetworkError
    }

    override suspend fun createParticipantItems(
        participantAddresses: List<String>
    ): List<JointAccountParticipantItem> {
        return createJointAccountParticipantItem.createParticipantItems(participantAddresses)
    }

    override suspend fun deleteInboxNotification(accountAddress: String) {
//        inboxOperationsUseCase.deleteNotification(accountAddress)
        TODO("Implement this")
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
        inboxMessages: InboxMessagesDTO,
        accountAddress: String
    ): JointAccountDetailProcessor.InvitationResult {
        val dto = inboxMessages.jointAccountImportRequests
            ?.firstOrNull { it.address == accountAddress }
            ?: return JointAccountDetailProcessor.InvitationResult.NotFound

        val participantAddresses = dto.participantAddresses
        val threshold = dto.threshold

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
