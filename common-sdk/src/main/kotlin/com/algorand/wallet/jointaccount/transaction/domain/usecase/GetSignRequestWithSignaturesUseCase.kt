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

package com.algorand.wallet.jointaccount.transaction.domain.usecase

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignatureDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignatureDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.TransactionListWithFullSignatureDTO
import javax.inject.Inject
import javax.inject.Named

fun interface GetSignRequestWithSignatures {
    suspend operator fun invoke(deviceId: Long, signRequestId: String): PeraResult<SignRequestWithFullSignatureDTO>
}

internal class GetSignRequestWithSignaturesUseCase @Inject constructor(
    @param:Named(JointAccountRepository.INJECTION_NAME)
    private val jointAccountRepository: JointAccountRepository
) : GetSignRequestWithSignatures {
    override suspend fun invoke(deviceId: Long, signRequestId: String): PeraResult<SignRequestWithFullSignatureDTO> {
        val searchDTO = SearchSignRequestsDTO(
            deviceId = deviceId,
            signRequestId = signRequestId
        )
        return when (val result = jointAccountRepository.searchSignRequests(searchDTO)) {
            is PeraResult.Success -> {
                val signRequest = result.data.firstOrNull { it.id == signRequestId }
                if (signRequest != null) {
                    PeraResult.Success(mapToSignRequestWithFullSignatureDTO(signRequest))
                } else {
                    PeraResult.Error(Exception("Sign request not found"))
                }
            }

            is PeraResult.Error -> result
        }
    }

    private fun mapToSignRequestWithFullSignatureDTO(dto: JointSignRequestDTO): SignRequestWithFullSignatureDTO {
        return SignRequestWithFullSignatureDTO(
            id = dto.id?.toLongOrNull(),
            type = dto.type,
            jointAccount = dto.jointAccount,
            proposerAddress = dto.proposerAddress,
            lastValidExpectedDatetime = dto.expectedExpireDatetime,
            transactionLists = dto.transactionLists?.map { transactionList ->
                TransactionListWithFullSignatureDTO(
                    rawTransactions = transactionList.rawTransactions,
                    firstValidBlock = transactionList.firstValidBlock?.toLongOrNull(),
                    lastValidBlock = transactionList.lastValidBlock?.toLongOrNull(),
                    responses = transactionList.responses?.mapNotNull { response ->
                        val address = response.address ?: return@mapNotNull null
                        val type = response.response ?: return@mapNotNull null
                        ParticipantSignatureDTO(
                            address = address,
                            signatures = response.signatures ?: emptyList(),
                            type = type
                        )
                    },
                    lastValidExpectedDatetime = transactionList.expectedExpireDatetime
                )
            },
            status = dto.status
        )
    }
}
