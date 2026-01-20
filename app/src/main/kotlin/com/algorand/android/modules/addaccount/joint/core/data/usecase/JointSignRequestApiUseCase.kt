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
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
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

class JointSignRequestApiUseCase @Inject constructor(
    private val jointAccountApiService: JointAccountApiService,
    private val peraApiErrorHandler: RetrofitErrorHandler,
    private val proposeJointSignRequestDTOMapper: ProposeJointSignRequestDTOMapper,
    private val jointSignRequestDTOMapper: JointSignRequestDTOMapper,
    private val signRequestTransactionListResponseDTOMapper: SignRequestTransactionListResponseDTOMapper,
    private val searchSignRequestsDTOMapper: SearchSignRequestsDTOMapper
) {

    suspend fun proposeSignRequest(
        proposeJointSignRequestDTO: ProposeJointSignRequestDTO
    ): Result<JointSignRequestDTO> {
        val request = proposeJointSignRequestDTOMapper
            .mapToProposeJointSignRequestRequest(proposeJointSignRequestDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.proposeSignRequest(request)
        }.mapToJointSignRequestDTO()
    }

    suspend fun addSignature(
        signRequestId: String,
        signRequestTransactionListResponseDTO: SignRequestTransactionListResponseDTO
    ): Result<JointSignRequestDTO> {
        val request = signRequestTransactionListResponseDTOMapper
            .mapToSignRequestTransactionListResponseRequest(signRequestTransactionListResponseDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.addSignature(
                signRequestId,
                signRequestTransactionListResponseDTO.address,
                request
            )
        }.mapToJointSignRequestDTO()
    }

    suspend fun searchSignRequests(
        searchSignRequestsDTO: SearchSignRequestsDTO
    ): Result<List<JointSignRequestDTO>> {
        val request = searchSignRequestsDTOMapper.mapToSearchSignRequestsRequest(searchSignRequestsDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.searchSignRequests(request)
        }.map { paginatedResponse ->
            paginatedResponse.results?.mapNotNull { response ->
                jointSignRequestDTOMapper.mapToJointSignRequestDTO(response)
            } ?: emptyList()
        }
    }

    private fun Result<JointSignRequestResponse>.mapToJointSignRequestDTO(): Result<JointSignRequestDTO> {
        return when (this) {
            is Result.Success -> {
                val dto = jointSignRequestDTOMapper.mapToJointSignRequestDTO(data)
                if (dto != null) Result.Success(dto) else Result.Error(Exception("Failed to map"))
            }
            is Result.Error -> this
        }
    }
}
