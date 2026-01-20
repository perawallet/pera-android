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

package com.algorand.wallet.jointaccount.domain.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountDTO
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestTransactionListResponseDTO

interface JointAccountRepository {

    suspend fun createJointAccount(
        createJointAccountDTO: CreateJointAccountDTO
    ): PeraResult<JointAccountDTO>

    suspend fun proposeSignRequest(
        proposeJointSignRequestDTO: ProposeJointSignRequestDTO
    ): PeraResult<JointSignRequestDTO>

    suspend fun addSignature(
        signRequestId: String,
        signRequestTransactionListResponseDTO: SignRequestTransactionListResponseDTO
    ): PeraResult<JointSignRequestDTO>

    suspend fun searchSignRequests(
        searchSignRequestsDTO: SearchSignRequestsDTO
    ): PeraResult<List<JointSignRequestDTO>>

    companion object {
        const val INJECTION_NAME: String = "jointAccountRepositoryInjectionName"
    }
}
