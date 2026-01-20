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

package com.algorand.android.modules.addaccount.joint.core.data.usecase

import com.algorand.android.exceptions.RetrofitErrorHandler
import com.algorand.android.models.Result
import com.algorand.android.network.requestWithPeraApiErrorHandler
import com.algorand.wallet.inbox.domain.model.InboxMessagesDTO
import com.algorand.wallet.inbox.domain.model.InboxSearchDTO
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchDTOMapper
import com.algorand.wallet.inbox.jointaccount.data.service.InboxApiService
import javax.inject.Inject

class JointAccountInboxApiUseCase @Inject constructor(
    private val inboxApiService: InboxApiService,
    private val peraApiErrorHandler: RetrofitErrorHandler,
    private val inboxSearchDTOMapper: InboxSearchDTOMapper
) {

    suspend fun getInboxMessages(
        deviceId: Long,
        inboxSearchDTO: InboxSearchDTO
    ): Result<InboxMessagesDTO> {
        val request = inboxSearchDTOMapper.mapToInboxSearchRequest(inboxSearchDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            inboxApiService.getInboxMessages(deviceId, request)
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val dto = inboxSearchDTOMapper.mapToInboxMessagesDTO(result.data)
                    if (dto != null) Result.Success(dto) else Result.Error(Exception("Failed to map"))
                }
                is Result.Error -> result
            }
        }
    }

    suspend fun deleteInboxJointInvitationNotification(
        deviceId: Long,
        jointAddress: String
    ): Result<Unit> {
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            inboxApiService.deleteInboxJointInvitationNotification(deviceId, jointAddress)
        }
    }
}
