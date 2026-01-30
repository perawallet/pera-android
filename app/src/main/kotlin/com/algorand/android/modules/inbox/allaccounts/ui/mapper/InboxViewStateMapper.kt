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

package com.algorand.android.modules.inbox.allaccounts.ui.mapper

import android.content.Context
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.inbox.allaccounts.domain.model.InboxWithAccount
import com.algorand.android.modules.inbox.allaccounts.domain.model.SignatureRequestInboxItem
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewState
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.wallet.inbox.asset.domain.model.AssetInboxRequest
import com.algorand.wallet.inbox.domain.model.InboxMessages
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import javax.inject.Inject

class InboxViewStateMapper @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val signatureRequestInboxItemMapper: SignatureRequestInboxItemMapper,
    private val jointAccountInvitationInboxItemMapper: JointAccountInvitationInboxItemMapper,
    @ApplicationContext private val context: Context
) {

    suspend fun mapToViewState(
        assetInboxList: List<AssetInboxRequest>,
        addresses: List<String>,
        inboxMessages: InboxMessages?,
        lastOpenedTime: ZonedDateTime?,
        filterAccountAddress: String?,
        localAccountAddresses: List<String>
    ): InboxViewState {
        val inboxWithAccountList = mapToInboxWithAccount(assetInboxList, addresses)
        val signatureRequestList = mapToSignatureRequestList(inboxMessages, lastOpenedTime, localAccountAddresses)
        val jointAccountInvitationList = mapToJointAccountInvitationList(inboxMessages, lastOpenedTime)

        val isEmpty = inboxWithAccountList.isEmpty() &&
            signatureRequestList.isEmpty() &&
            jointAccountInvitationList.isEmpty()

        return if (isEmpty) {
            InboxViewState.Empty
        } else {
            InboxViewState.Content(
                inboxWithAccountList = inboxWithAccountList,
                signatureRequestList = signatureRequestList,
                jointAccountInvitationList = jointAccountInvitationList,
                filterAccountAddress = filterAccountAddress
            )
        }
    }

    private suspend fun mapToInboxWithAccount(
        assetInboxList: List<AssetInboxRequest>,
        addresses: List<String>
    ): List<InboxWithAccount> = assetInboxList.mapNotNull { inbox ->
        if (inbox.requestCount <= 0) return@mapNotNull null
        val address = addresses.firstOrNull { it == inbox.address } ?: return@mapNotNull null
        InboxWithAccount(
            address = inbox.address,
            requestCount = inbox.requestCount,
            accountAddress = address,
            accountDisplayName = getAccountDisplayName(address),
            accountIconDrawablePreview = getAccountIconDrawablePreview(address)
        )
    }

    private suspend fun mapToSignatureRequestList(
        inboxMessages: InboxMessages?,
        lastOpenedTime: ZonedDateTime?,
        localAccountAddresses: List<String>
    ): List<SignatureRequestInboxItem> {
        val signRequests = inboxMessages?.jointAccountSignRequests ?: return emptyList()
        val currentBlockNumber = signatureRequestInboxItemMapper.getCurrentBlockNumber()

        return signRequests.mapNotNull { signRequest ->
            signatureRequestInboxItemMapper.mapToSignatureRequestInboxItem(
                signRequest,
                context.resources,
                lastOpenedTime,
                currentBlockNumber,
                localAccountAddresses
            )
        }
    }

    private fun mapToJointAccountInvitationList(
        inboxMessages: InboxMessages?,
        lastOpenedTime: ZonedDateTime?
    ): List<JointAccountInvitationInboxItem> {
        val importRequests = inboxMessages?.jointAccountImportRequests ?: return emptyList()
        return importRequests.mapNotNull { dto ->
            jointAccountInvitationInboxItemMapper.mapToJointAccountInvitationInboxItem(dto, lastOpenedTime)
        }
    }
}
