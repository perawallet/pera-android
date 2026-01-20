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

import com.algorand.wallet.inbox.domain.model.AssetInboxDTO
import com.algorand.wallet.inbox.domain.model.InboxMessagesDTO
import com.algorand.wallet.inbox.domain.model.InboxSearchDTO
import com.algorand.wallet.inbox.jointaccount.data.model.AssetInboxResponse
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchRequest
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchResponse
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.JointSignRequestDTOMapper
import javax.inject.Inject

class InboxSearchDTOMapper @Inject constructor(
    private val jointAccountDTOMapper: JointAccountDTOMapper,
    private val jointSignRequestDTOMapper: JointSignRequestDTOMapper
) {

    fun mapToInboxSearchRequest(dto: InboxSearchDTO): InboxSearchRequest {
        return InboxSearchRequest(
            addresses = dto.addresses
        )
    }

    fun mapToInboxMessagesDTO(response: InboxSearchResponse?): InboxMessagesDTO? {
        return response?.let {
            InboxMessagesDTO(
                jointAccountImportRequests = it.jointAccountImportRequests?.mapNotNull { account ->
                    jointAccountDTOMapper.mapToJointAccountDTO(account)
                },
                jointAccountSignRequests = it.jointAccountSignRequests?.mapNotNull { signRequest ->
                    jointSignRequestDTOMapper.mapToJointSignRequestDTO(signRequest)
                },
                assetInboxes = it.asaInboxes?.mapNotNull { assetInbox ->
                    mapToAssetInboxDTO(assetInbox)
                }
            )
        }
    }

    private fun mapToAssetInboxDTO(response: AssetInboxResponse): AssetInboxDTO? {
        val address = response.address ?: return null
        return AssetInboxDTO(
            address = address,
            inboxAddress = response.inboxAddress,
            requestCount = response.requestCount ?: 0
        )
    }
}
