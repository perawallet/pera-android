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
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestDTO
import javax.inject.Inject
import javax.inject.Named

fun interface ProposeJointSignRequest {
    suspend operator fun invoke(
        jointAccountAddress: String,
        proposerAddress: String,
        type: String,
        rawTransactionLists: List<List<String>>,
        transactionSignatureLists: List<List<String?>>
    ): PeraResult<JointSignRequestDTO>
}

internal class ProposeJointSignRequestUseCase @Inject constructor(
    @param:Named(JointAccountRepository.INJECTION_NAME)
    private val jointAccountRepository: JointAccountRepository
) : ProposeJointSignRequest {
    override suspend fun invoke(
        jointAccountAddress: String,
        proposerAddress: String,
        type: String,
        rawTransactionLists: List<List<String>>,
        transactionSignatureLists: List<List<String?>>
    ): PeraResult<JointSignRequestDTO> {
        val proposeJointSignRequestDTO = ProposeJointSignRequestDTO(
            jointAccountAddress = jointAccountAddress,
            proposerAddress = proposerAddress,
            type = type,
            rawTransactionLists = rawTransactionLists,
            transactionSignatureLists = transactionSignatureLists
        )
        return jointAccountRepository.proposeSignRequest(proposeJointSignRequestDTO)
    }
}
