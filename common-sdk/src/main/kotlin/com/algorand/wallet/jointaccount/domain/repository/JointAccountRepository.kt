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
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountInput
import com.algorand.wallet.jointaccount.creation.domain.model.IsJointAccountResult
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.transaction.domain.model.AddSignatureInput
import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature

interface JointAccountRepository {

    suspend fun getJointAccountDetail(accountAddress: String): PeraResult<JointAccount>

    suspend fun createJointAccount(
        createJointAccount: CreateJointAccountInput
    ): PeraResult<JointAccount>

    suspend fun proposeSignRequest(
        createSignRequestInput: CreateSignRequestInput
    ): PeraResult<JointSignRequest>

    suspend fun addSignatures(
        signRequestId: String,
        addSignatureInputs: List<AddSignatureInput>
    ): PeraResult<JointSignRequest>

    suspend fun getSignRequestWithSignatures(
        deviceId: Long,
        signRequestId: String
    ): PeraResult<SignRequestWithFullSignature>

    suspend fun getSignRequestWithFullSignatures(
        deviceId: String,
        signRequestId: String
    ): PeraResult<SignRequestWithFullSignature>

    suspend fun markSignRequestsConfirmed(deviceId: String, signRequestIds: List<String>): PeraResult<Unit>

    suspend fun checkIsJointAccount(addresses: List<String>): PeraResult<List<IsJointAccountResult>>
}
