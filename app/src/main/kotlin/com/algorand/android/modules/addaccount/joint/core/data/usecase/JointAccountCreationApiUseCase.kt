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
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountDTO
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import javax.inject.Inject

class JointAccountCreationApiUseCase @Inject constructor(
    private val jointAccountApiService: JointAccountApiService,
    private val peraApiErrorHandler: RetrofitErrorHandler,
    private val createJointAccountDTOMapper: CreateJointAccountDTOMapper,
    private val jointAccountDTOMapper: JointAccountDTOMapper
) {

    suspend operator fun invoke(createJointAccountDTO: CreateJointAccountDTO): Result<JointAccountDTO> {
        val request = createJointAccountDTOMapper.mapToCreateJointAccountRequest(createJointAccountDTO)
        return requestWithPeraApiErrorHandler(peraApiErrorHandler) {
            jointAccountApiService.createJointAccount(request)
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val dto = jointAccountDTOMapper.mapToJointAccountDTO(result.data)
                    if (dto != null) Result.Success(dto) else Result.Error(Exception("Failed to map"))
                }
                is Result.Error -> result
            }
        }
    }
}
