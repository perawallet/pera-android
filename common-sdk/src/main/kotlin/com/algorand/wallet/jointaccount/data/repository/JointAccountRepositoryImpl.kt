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

package com.algorand.wallet.jointaccount.data.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import com.algorand.wallet.foundation.network.utils.requestWithPeraApiErrorHandler
import com.algorand.wallet.inbox.domain.model.InboxMessagesDTO
import com.algorand.wallet.inbox.domain.model.InboxSearchDTO
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchDTOMapper
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchResponse
import com.algorand.wallet.inbox.jointaccount.data.service.InboxApiService
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountDTO
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.transaction.data.mapper.JointSignRequestDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.ProposeJointSignRequestDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.SearchSignRequestsDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.SignRequestTransactionListResponseDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestTransactionListResponseDTO
import javax.inject.Inject

internal class JointAccountRepositoryImpl @Inject constructor(
    private val jointAccountApiService: JointAccountApiService,
    private val inboxApiService: InboxApiService,
    private val createJointAccountDTOMapper: CreateJointAccountDTOMapper,
    private val jointAccountDTOMapper: JointAccountDTOMapper,
    private val proposeJointSignRequestDTOMapper: ProposeJointSignRequestDTOMapper,
    private val jointSignRequestDTOMapper: JointSignRequestDTOMapper,
    private val signRequestTransactionListResponseDTOMapper: SignRequestTransactionListResponseDTOMapper,
    private val searchSignRequestsDTOMapper: SearchSignRequestsDTOMapper,
    private val inboxSearchDTOMapper: InboxSearchDTOMapper,
    private val peraApiErrorHandler: PeraRetrofitErrorHandler
) : JointAccountRepository {

    override suspend fun createJointAccount(
        createJointAccountDTO: CreateJointAccountDTO
    ): PeraResult<JointAccountDTO> {
        val request = createJointAccountDTOMapper.mapToCreateJointAccountRequest(createJointAccountDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.createJointAccount(request)
        }.mapToJointAccountDTO()
    }

    private fun PeraResult<JointAccountResponse>.mapToJointAccountDTO(): PeraResult<JointAccountDTO> {
        return when (this) {
            is PeraResult.Success -> {
                val dto = jointAccountDTOMapper.mapToJointAccountDTO(data)
                if (dto != null) PeraResult.Success(dto) else PeraResult.Error(Exception("Failed to map joint account"))
            }

            is PeraResult.Error -> this
        }
    }

    override suspend fun proposeSignRequest(
        proposeJointSignRequestDTO: ProposeJointSignRequestDTO
    ): PeraResult<JointSignRequestDTO> {
        val request = proposeJointSignRequestDTOMapper.mapToProposeJointSignRequestRequest(
            proposeJointSignRequestDTO
        )
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.proposeSignRequest(request)
        }.mapToJointSignRequestDTO()
    }

    override suspend fun addSignature(
        signRequestId: String,
        signRequestTransactionListResponseDTO: SignRequestTransactionListResponseDTO
    ): PeraResult<JointSignRequestDTO> {
        val request = signRequestTransactionListResponseDTOMapper.mapToSignRequestTransactionListResponseRequest(
            signRequestTransactionListResponseDTO
        )
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.addSignature(signRequestId, signRequestTransactionListResponseDTO.address, request)
        }.mapToJointSignRequestDTO()
    }

    private fun PeraResult<JointSignRequestResponse>.mapToJointSignRequestDTO(): PeraResult<JointSignRequestDTO> {
        return when (this) {
            is PeraResult.Success -> {
                val dto = jointSignRequestDTOMapper.mapToJointSignRequestDTO(data)
                if (dto != null) PeraResult.Success(dto) else PeraResult.Error(Exception("Failed to map sign request"))
            }

            is PeraResult.Error -> this
        }
    }

    override suspend fun searchSignRequests(
        searchSignRequestsDTO: SearchSignRequestsDTO
    ): PeraResult<List<JointSignRequestDTO>> {
        val request = searchSignRequestsDTOMapper.mapToSearchSignRequestsRequest(searchSignRequestsDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.searchSignRequests(request)
        }.map { paginatedResponse ->
            paginatedResponse.results?.mapNotNull { response ->
                jointSignRequestDTOMapper.mapToJointSignRequestDTO(response).also { dto ->
                    if (dto == null) {
                        android.util.Log.w(
                            "JointAccountRepository",
                            "Failed to map sign request: id=${response.id}"
                        )
                    }
                }
            } ?: emptyList()
        }
    }

    override suspend fun getInboxMessages(
        deviceId: Long,
        inboxSearchDTO: InboxSearchDTO
    ): PeraResult<InboxMessagesDTO> {
        val request = inboxSearchDTOMapper.mapToInboxSearchRequest(inboxSearchDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            inboxApiService.getInboxMessages(deviceId, request)
        }.mapToInboxMessagesDTO()
    }

    private fun PeraResult<InboxSearchResponse>.mapToInboxMessagesDTO(): PeraResult<InboxMessagesDTO> {
        return when (this) {
            is PeraResult.Success -> {
                val dto = inboxSearchDTOMapper.mapToInboxMessagesDTO(data)
                if (dto != null) PeraResult.Success(dto) else PeraResult.Error(Exception("Failed to map inbox messages"))
            }

            is PeraResult.Error -> this
        }
    }

    override suspend fun deleteInboxJointInvitationNotification(
        deviceId: Long,
        jointAddress: String
    ): PeraResult<Unit> {
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            inboxApiService.deleteInboxJointInvitationNotification(deviceId, jointAddress)
        }
    }
}
