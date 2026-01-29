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

package com.algorand.android.modules.inbox.allaccounts.ui.usecase

import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxPreviewMapper
import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxPreviewParams
import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxViewStateMapper
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxPreview
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewState
import com.algorand.android.modules.inbox.data.local.InboxLastOpenedTimeLocalSource
import com.algorand.android.utils.parseFormattedDate
import com.algorand.wallet.inbox.asset.domain.model.AssetInboxRequest
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class InboxPreviewUseCase @Inject constructor(
    private val inboxPreviewMapper: InboxPreviewMapper,
    private val inboxViewStateMapper: InboxViewStateMapper,
    private val getInboxValidAddresses: GetInboxValidAddresses,
    private val getInboxMessagesFlow: GetInboxMessagesFlow,
    private val refreshInboxCache: RefreshInboxCache,
    private val inboxLastOpenedTimeLocalSource: InboxLastOpenedTimeLocalSource
) {

    fun getInitialPreview(): InboxPreview {
        return inboxPreviewMapper.getInitialPreview()
    }

    fun setLastOpenedTime(zonedDateTime: ZonedDateTime) {
        val lastOpenedTimeAsString = zonedDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
        inboxLastOpenedTimeLocalSource.saveData(lastOpenedTimeAsString)
    }

    fun getLastOpenedTime(): ZonedDateTime? {
        val lastOpenedTimeAsString = inboxLastOpenedTimeLocalSource.getDataOrNull()
        return lastOpenedTimeAsString?.parseFormattedDate(DateTimeFormatter.ISO_DATE_TIME)
    }

    fun getInboxPreview(filterAccountAddress: String? = null): Flow<InboxPreview> {
        return getInboxMessagesFlow().map { inboxMessages ->
            val allAccountAddresses = getInboxValidAddresses()
            val lastOpenedTime = getLastOpenedTime()

            if (allAccountAddresses.isEmpty()) {
                return@map createInboxPreview(
                    emptyList(), allAccountAddresses, null, lastOpenedTime,
                    filterAccountAddress, allAccountAddresses
                )
            }

            // Apply local filtering if filterAccountAddress is provided
            val filteredInboxMessages = filterInboxMessages(inboxMessages, filterAccountAddress)
            val assetInboxRequests = parseAssetInboxes(filteredInboxMessages)

            val displayAddresses = if (filterAccountAddress != null) {
                listOf(filterAccountAddress)
            } else {
                allAccountAddresses
            }

            createInboxPreview(
                assetInboxRequests, displayAddresses, filteredInboxMessages,
                lastOpenedTime, filterAccountAddress, allAccountAddresses
            )
        }
    }

    suspend fun refreshInbox() {
        refreshInboxCache()
    }

    private fun filterInboxMessages(
        inboxMessages: InboxMessages?,
        filterAccountAddress: String?
    ): InboxMessages? {
        if (filterAccountAddress == null || inboxMessages == null) return inboxMessages

        return InboxMessages(
            jointAccountImportRequests = inboxMessages.jointAccountImportRequests?.filter { jointAccount ->
                // Joint account invitations are sent to participants
                jointAccount.participantAddresses?.contains(filterAccountAddress) == true
            },
            jointAccountSignRequests = inboxMessages.jointAccountSignRequests?.filter { signRequest ->
                // Sign requests should show:
                // 1. On the joint account itself
                // 2. On participant accounts (so they can see/sign the request)
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

    private fun parseAssetInboxes(
        inboxMessages: InboxMessages?
    ): List<AssetInboxRequest> {
        return inboxMessages?.assetInboxes?.map { assetInbox ->
            AssetInboxRequest(
                address = assetInbox.address,
                requestCount = assetInbox.requestCount
            )
        } ?: emptyList()
    }

    private suspend fun createInboxPreview(
        assetInboxList: List<AssetInboxRequest>,
        addresses: List<String>,
        inboxMessages: InboxMessages?,
        lastOpenedTime: ZonedDateTime?,
        filterAccountAddress: String?,
        localAccountAddresses: List<String>
    ): InboxPreview {
        val hasAssetInboxRequests = assetInboxList.any { it.requestCount > 0 }
        val hasSignatureRequests = inboxMessages?.jointAccountSignRequests?.isNotEmpty() == true
        val hasJointAccountInvitations = inboxMessages?.jointAccountImportRequests?.isNotEmpty() == true

        return inboxPreviewMapper(
            InboxPreviewParams(
                assetInboxList = assetInboxList,
                addresses = addresses,
                inboxMessages = inboxMessages,
                isLoading = false,
                isEmptyStateVisible = !hasAssetInboxRequests && !hasSignatureRequests && !hasJointAccountInvitations,
                lastOpenedTime = lastOpenedTime,
                filterAccountAddress = filterAccountAddress,
                localAccountAddresses = localAccountAddresses
            )
        )
    }

    fun getInboxViewState(filterAccountAddress: String? = null): Flow<InboxViewState> {
        return getInboxMessagesFlow().map { inboxMessages ->
            val allAccountAddresses = getInboxValidAddresses()
            val lastOpenedTime = getLastOpenedTime()

            if (allAccountAddresses.isEmpty()) {
                return@map InboxViewState.Empty
            }

            val filteredInboxMessages = filterInboxMessages(inboxMessages, filterAccountAddress)
            val assetInboxRequests = parseAssetInboxes(filteredInboxMessages)

            val displayAddresses = if (filterAccountAddress != null) {
                listOf(filterAccountAddress)
            } else {
                allAccountAddresses
            }

            inboxViewStateMapper.mapToViewState(
                assetInboxList = assetInboxRequests,
                addresses = displayAddresses,
                inboxMessages = filteredInboxMessages,
                lastOpenedTime = lastOpenedTime,
                filterAccountAddress = filterAccountAddress,
                localAccountAddresses = allAccountAddresses
            )
        }
    }

    fun getJointAccountInboxCountFlow(): Flow<Int> {
        return getInboxMessagesFlow().map { inboxMessages ->
            val signRequestCount = inboxMessages?.jointAccountSignRequests?.size ?: 0
            val importRequestCount = inboxMessages?.jointAccountImportRequests?.size ?: 0
            signRequestCount + importRequestCount
        }
    }
}
