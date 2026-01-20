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

package com.algorand.android.modules.addaccount.joint.creation.domain.usecase

import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.joint.core.domain.repository.JointAccountRepository
import com.algorand.android.modules.addaccount.joint.creation.domain.exception.JointAccountValidationException
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountDTO
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import javax.inject.Inject
import javax.inject.Named

internal class CreateJointAccountUseCase @Inject constructor(
    @param:Named(JointAccountRepository.INJECTION_NAME)
    private val jointAccountRepository: JointAccountRepository
) : CreateJointAccount {

    override suspend operator fun invoke(
        participantAddresses: List<String>,
        threshold: Int,
        version: Int
    ): Result<JointAccountDTO> {
        val validationResult = validateJointAccountCreation(participantAddresses, threshold)
        if (validationResult != null) {
            return Result.Error(validationResult)
        }

        val createJointAccountDTO = CreateJointAccountDTO(
            participantAddresses = participantAddresses,
            threshold = threshold,
            version = version
        )
        return jointAccountRepository.createJointAccount(createJointAccountDTO)
    }

    private fun validateJointAccountCreation(
        participantAddresses: List<String>,
        threshold: Int
    ): JointAccountValidationException? {
        if (participantAddresses.size < JointAccountValidationException.MIN_PARTICIPANTS) {
            return JointAccountValidationException.InsufficientParticipants()
        }

        if (threshold < 1 || threshold > participantAddresses.size) {
            return JointAccountValidationException.InvalidThreshold(
                participantCount = participantAddresses.size,
                threshold = threshold
            )
        }

        return null
    }
}
