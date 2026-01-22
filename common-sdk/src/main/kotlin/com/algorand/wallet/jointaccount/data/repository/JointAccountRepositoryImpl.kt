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
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountInput
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.transaction.data.mapper.JointSignRequestMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.CreateSignRequestInputMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.SearchSignRequestsInputMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.AddSignatureInputMapper
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.domain.model.AddSignatureInput
import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsInput
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.TransactionListWithFullSignature
import javax.inject.Inject

internal class JointAccountRepositoryImpl @Inject constructor(
    private val jointAccountApiService: JointAccountApiService,
    private val createJointAccountDTOMapper: CreateJointAccountDTOMapper,
    private val jointAccountDTOMapper: JointAccountDTOMapper,
    private val createSignRequestInputMapper: CreateSignRequestInputMapper,
    private val jointSignRequestDTOMapper: JointSignRequestMapper,
    private val addSignatureInputMapper: AddSignatureInputMapper,
    private val searchSignRequestsInputMapper: SearchSignRequestsInputMapper,
    private val peraApiErrorHandler: PeraRetrofitErrorHandler
) : JointAccountRepository {

    override suspend fun createJointAccount(
        createJointAccount: CreateJointAccountInput
    ): PeraResult<JointAccount> {
        val request = createJointAccountDTOMapper.mapToCreateJointAccountRequest(createJointAccount)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.createJointAccount(request)
        }.mapToJointAccount()
    }

    private fun PeraResult<JointAccountResponse>.mapToJointAccount(): PeraResult<JointAccount> {
        return when (this) {
            is PeraResult.Success -> {
                val dto = jointAccountDTOMapper.mapToJointAccountDTO(data)
                if (dto != null) PeraResult.Success(dto) else PeraResult.Error(Exception("Failed to map joint account"))
            }

            is PeraResult.Error -> this
        }
    }

    override suspend fun proposeSignRequest(
        createSignRequestInput: CreateSignRequestInput
    ): PeraResult<JointSignRequest> {
        val request = createSignRequestInputMapper.mapToProposeJointSignRequestRequest(
            createSignRequestInput
        )
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.proposeSignRequest(request)
        }.mapToJointSignRequest()
    }

    override suspend fun addSignature(
        signRequestId: String,
        signRequestTransactionListResponseDTO: AddSignatureInput
    ): PeraResult<JointSignRequest> {
        val request = addSignatureInputMapper.mapToSignRequestTransactionListResponseRequest(
            signRequestTransactionListResponseDTO
        )
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.addSignature(signRequestId, signRequestTransactionListResponseDTO.address, request)
        }.mapToJointSignRequest()
    }

    private fun PeraResult<JointSignRequestResponse>.mapToJointSignRequest(): PeraResult<JointSignRequest> {
        return when (this) {
            is PeraResult.Success -> {
                val dto = jointSignRequestDTOMapper.mapToJointSignRequest(data)
                if (dto != null) PeraResult.Success(dto) else PeraResult.Error(Exception("Failed to map sign request"))
            }

            is PeraResult.Error -> this
        }
    }

    override suspend fun getSignRequestWithSignatures(
        deviceId: Long,
        signRequestId: String
    ): PeraResult<SignRequestWithFullSignature> {
        val searchInput = SearchSignRequestsInput(
            deviceId = deviceId,
            signRequestId = signRequestId
        )
        val request = searchSignRequestsInputMapper.mapToSearchSignRequestsRequest(searchInput)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.searchSignRequests(request)
        }.let { result ->
            when (result) {
                is PeraResult.Success -> {
                    val signRequests = result.data.results?.mapNotNull { response ->
                        jointSignRequestDTOMapper.mapToJointSignRequest(response)
                    } ?: emptyList()
                    val signRequest = signRequests.firstOrNull { it.id == signRequestId }
                    if (signRequest != null) {
                        PeraResult.Success(mapToSignRequestWithFullSignature(signRequest))
                    } else {
                        PeraResult.Error(Exception("Sign request not found"))
                    }
                }
                is PeraResult.Error -> result
            }
        }
    }

    private fun mapToSignRequestWithFullSignature(signRequest: JointSignRequest): SignRequestWithFullSignature {
        return SignRequestWithFullSignature(
            id = signRequest.id?.toLongOrNull(),
            type = signRequest.type,
            jointAccount = signRequest.jointAccount,
            proposerAddress = signRequest.proposerAddress,
            lastValidExpectedDatetime = signRequest.expectedExpireDatetime,
            transactionLists = signRequest.transactionLists?.map { transactionList ->
                TransactionListWithFullSignature(
                    rawTransactions = transactionList.rawTransactions,
                    firstValidBlock = transactionList.firstValidBlock?.toLongOrNull(),
                    lastValidBlock = transactionList.lastValidBlock?.toLongOrNull(),
                    responses = transactionList.responses?.mapNotNull { response ->
                        val address = response.address ?: return@mapNotNull null
                        val type = response.response ?: return@mapNotNull null
                        ParticipantSignature(
                            address = address,
                            signatures = response.signatures ?: emptyList(),
                            type = type
                        )
                    },
                    lastValidExpectedDatetime = transactionList.expectedExpireDatetime
                )
            },
            status = signRequest.status
        )
    }
}
