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

package com.algorand.wallet.inbox.data.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import com.algorand.wallet.foundation.network.utils.requestWithPeraApiErrorHandler
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.model.InboxSearchInput
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchMapper
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchResponse
import com.algorand.wallet.inbox.jointaccount.data.service.InboxApiService
import javax.inject.Inject

internal class InboxApiRepositoryImpl @Inject constructor(
    private val inboxApiService: InboxApiService,
    private val inboxSearchMapper: InboxSearchMapper,
    private val peraApiErrorHandler: PeraRetrofitErrorHandler
) : InboxApiRepository {

    override suspend fun getInboxMessages(
        deviceId: Long,
        inboxSearchInput: InboxSearchInput
    ): PeraResult<InboxMessages> {
        val request = inboxSearchMapper.mapToInboxSearchRequest(inboxSearchInput)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            inboxApiService.getInboxMessages(deviceId, request)
        }.mapToInboxMessages()
    }

    private fun PeraResult<InboxSearchResponse>.mapToInboxMessages(): PeraResult<InboxMessages> {
        return when (this) {
            is PeraResult.Success -> {
                val inboxMessages = inboxSearchMapper.mapToInboxMessages(data)
                if (inboxMessages != null) {
                    PeraResult.Success(inboxMessages)
                } else {
                    PeraResult.Error(Exception("Failed to map inbox messages"))
                }
            }
            is PeraResult.Error -> this
        }
    }

    override suspend fun deleteJointInvitationNotification(
        deviceId: Long,
        jointAddress: String
    ): PeraResult<Unit> {
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            inboxApiService.deleteInboxJointInvitationNotification(deviceId, jointAddress)
        }
    }
}
