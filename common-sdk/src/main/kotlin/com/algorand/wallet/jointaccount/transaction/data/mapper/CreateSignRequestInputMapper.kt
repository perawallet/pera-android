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

package com.algorand.wallet.jointaccount.transaction.data.mapper

import com.algorand.wallet.jointaccount.transaction.data.model.ProposeJointSignRequestRequest
import com.algorand.wallet.jointaccount.transaction.data.model.ProposeJointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
import javax.inject.Inject

internal class CreateSignRequestInputMapper @Inject constructor() {

    fun mapToProposeJointSignRequestRequest(
        dto: CreateSignRequestInput
    ): ProposeJointSignRequestRequest {
        val responses = dto.responses.map { responseInput ->
            ProposeJointSignRequestResponse(
                address = responseInput.address,
                response = responseInput.responseType.value,
                signatures = responseInput.signatures,
                deviceId = responseInput.deviceId
            )
        }
        return ProposeJointSignRequestRequest(
            jointAccountAddress = dto.jointAccountAddress,
            proposerAddress = dto.proposerAddress,
            type = dto.type.value,
            rawTransactionLists = dto.rawTransactionLists,
            responses = responses
        )
    }
}
