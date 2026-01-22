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

package com.algorand.wallet.inbox.jointaccount.data.mapper

import com.algorand.wallet.inbox.domain.model.AssetInbox
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.model.InboxSearchInput
import com.algorand.wallet.inbox.jointaccount.data.model.AssetInboxResponse
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchRequest
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchResponse
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.JointSignRequestMapper
import javax.inject.Inject

internal class InboxSearchDTOMapper @Inject constructor(
    private val jointAccountDTOMapper: JointAccountDTOMapper,
    private val jointSignRequestMapper: JointSignRequestMapper
) {

    fun mapToInboxSearchRequest(input: InboxSearchInput): InboxSearchRequest {
        return InboxSearchRequest(
            addresses = input.addresses
        )
    }

    fun mapToInboxMessages(response: InboxSearchResponse?): InboxMessages? {
        return response?.let {
            InboxMessages(
                jointAccountImportRequests = it.jointAccountImportRequests?.mapNotNull { account ->
                    jointAccountDTOMapper.mapToJointAccountDTO(account)
                },
                jointAccountSignRequests = it.jointAccountSignRequests?.mapNotNull { signRequest ->
                    jointSignRequestMapper.mapToJointSignRequest(signRequest)
                },
                assetInboxes = it.asaInboxes?.mapNotNull { assetInbox ->
                    mapToAssetInbox(assetInbox)
                }
            )
        }
    }

    private fun mapToAssetInbox(response: AssetInboxResponse): AssetInbox? {
        val address = response.address ?: return null
        return AssetInbox(
            address = address,
            inboxAddress = response.inboxAddress,
            requestCount = response.requestCount ?: 0
        )
    }
}
