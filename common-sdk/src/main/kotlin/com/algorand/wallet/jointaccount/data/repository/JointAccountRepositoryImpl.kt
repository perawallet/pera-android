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
import com.algorand.wallet.jointaccount.creation.data.mapper.IsJointAccountMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.model.IsJointAccountRequest
import com.algorand.wallet.jointaccount.creation.data.model.IsJointAccountResponse
import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountInput
import com.algorand.wallet.jointaccount.creation.domain.model.IsJointAccountResult
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.transaction.data.mapper.AddSignatureInputMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.CreateSignRequestInputMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.JointSignRequestMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.SearchSignRequestsInputMapper
import com.algorand.wallet.jointaccount.transaction.data.model.GetSignRequestWithSignaturesRequest
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.data.model.MarkSignRequestsConfirmedRequest
import com.algorand.wallet.jointaccount.transaction.data.model.SearchSignRequestsResponse
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
    private val isJointAccountMapper: IsJointAccountMapper,
    private val jointAccountDTOMapper: JointAccountDTOMapper,
    private val createSignRequestInputMapper: CreateSignRequestInputMapper,
    private val jointSignRequestMapper: JointSignRequestMapper,
    private val addSignatureInputMapper: AddSignatureInputMapper,
    private val searchSignRequestsInputMapper: SearchSignRequestsInputMapper,
    private val peraApiErrorHandler: PeraRetrofitErrorHandler,
) : JointAccountRepository {

    override suspend fun getJointAccountDetail(accountAddress: String): PeraResult<JointAccount> {
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.getJointAccountDetail(accountAddress)
        }.mapToJointAccount()
    }

    override suspend fun createJointAccount(
        createJointAccount: CreateJointAccountInput
    ): PeraResult<JointAccount> {
        val request = createJointAccountDTOMapper.mapToCreateJointAccountRequest(createJointAccount)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.createJointAccount(request)
        }.mapToJointAccount()
    }

    override suspend fun checkIsJointAccount(addresses: List<String>): PeraResult<List<IsJointAccountResult>> {
        if (addresses.isEmpty()) return PeraResult.Success(emptyList())
        val request = IsJointAccountRequest(accountAddresses = addresses)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.checkIsJointAccount(request)
        }.mapToIsJointAccountResults()
    }

    private fun PeraResult<List<IsJointAccountResponse>>.mapToIsJointAccountResults(): PeraResult<List<IsJointAccountResult>> {
        return when (this) {
            is PeraResult.Success -> {
                val results = data.mapNotNull { isJointAccountMapper.mapToIsJointAccountResult(it) }
                PeraResult.Success(results)
            }
            is PeraResult.Error -> this
        }
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
        val request = createSignRequestInputMapper.mapToProposeJointSignRequestRequest(createSignRequestInput)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.proposeSignRequest(request)
        }.mapToJointSignRequest()
    }

    override suspend fun addSignatures(
        signRequestId: String,
        addSignatureInputs: List<AddSignatureInput>
    ): PeraResult<JointSignRequest> {
        val requests = addSignatureInputs.map { input ->
            addSignatureInputMapper.mapToSignRequestTransactionListResponseRequest(input)
        }
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.addSignature(signRequestId, requests)
        }.mapToJointSignRequest()
    }

    private fun PeraResult<JointSignRequestResponse>.mapToJointSignRequest(): PeraResult<JointSignRequest> {
        return when (this) {
            is PeraResult.Success -> {
                val dto = jointSignRequestMapper.mapToJointSignRequest(data)
                if (dto != null) PeraResult.Success(dto) else PeraResult.Error(Exception("Failed to map sign request"))
            }

            is PeraResult.Error -> this
        }
    }

    override suspend fun markSignRequestsConfirmed(
        deviceId: String,
        signRequestIds: List<String>
    ): PeraResult<Unit> {
        if (signRequestIds.isEmpty()) return PeraResult.Success(Unit)
        val request = MarkSignRequestsConfirmedRequest(
            deviceId = deviceId,
            proposedSignRequestIds = signRequestIds
        )
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.markSignRequestsConfirmed(request)
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
        val result = requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.searchSignRequests(request)
        }
        return mapSearchResultToSignRequestWithFullSignature(result, signRequestId)
    }

    override suspend fun getSignRequestWithFullSignatures(
        deviceId: String,
        signRequestId: String
    ): PeraResult<SignRequestWithFullSignature> {
        val request = GetSignRequestWithSignaturesRequest(
            deviceId = deviceId,
            proposedSignRequestIds = listOf(signRequestId)
        )
        val result = requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.getSignRequestWithSignatures(request)
        }
        return when (result) {
            is PeraResult.Success -> {
                val signRequest = result.data
                    .mapNotNull { jointSignRequestMapper.mapToJointSignRequest(it) }
                    .firstOrNull { it.id == signRequestId }
                if (signRequest != null) {
                    PeraResult.Success(mapToSignRequestWithFullSignature(signRequest))
                } else {
                    PeraResult.Error(Exception("Sign request not found"))
                }
            }
            is PeraResult.Error -> result
        }
    }

    private fun mapSearchResultToSignRequestWithFullSignature(
        result: PeraResult<SearchSignRequestsResponse>,
        signRequestId: String
    ): PeraResult<SignRequestWithFullSignature> {
        return when (result) {
            is PeraResult.Success -> {
                val signRequests = result.data.results?.mapNotNull { response ->
                    jointSignRequestMapper.mapToJointSignRequest(response)
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

    private fun mapToSignRequestWithFullSignature(signRequest: JointSignRequest): SignRequestWithFullSignature {
        return SignRequestWithFullSignature(
            id = signRequest.id,
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
            status = signRequest.status,
            failReasonDisplay = signRequest.failReasonDisplay
        )
    }
}
