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

package com.algorand.android.modules.inbox.allaccounts.domain.usecase

import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxViewStateMapper
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewState
import com.algorand.wallet.inbox.asset.domain.model.AssetInboxRequest
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.usecase.GetInboxLastOpenedTime
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import javax.inject.Inject

internal class GetInboxViewStateUseCase @Inject constructor(
    private val inboxViewStateMapper: InboxViewStateMapper,
    private val getInboxValidAddresses: GetInboxValidAddresses,
    private val getInboxLastOpenedTime: GetInboxLastOpenedTime
) : GetInboxViewState {

    override suspend operator fun invoke(
        inboxMessages: InboxMessages?,
        filterAccountAddress: String?
    ): InboxViewState {
        val allAccountAddresses = getInboxValidAddresses()
        val lastOpenedTime = getInboxLastOpenedTime()

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

        return inboxViewStateMapper.mapToViewState(
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
}
