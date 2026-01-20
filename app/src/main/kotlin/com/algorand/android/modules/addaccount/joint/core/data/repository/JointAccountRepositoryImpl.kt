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

package com.algorand.android.modules.addaccount.joint.core.data.repository

import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.joint.core.data.usecase.JointAccountCreationApiUseCase
import com.algorand.android.modules.addaccount.joint.core.data.usecase.JointAccountInboxApiUseCase
import com.algorand.android.modules.addaccount.joint.core.data.usecase.JointSignRequestApiUseCase
import com.algorand.android.modules.addaccount.joint.core.domain.repository.JointAccountRepository
import com.algorand.wallet.inbox.domain.model.InboxMessagesDTO
import com.algorand.wallet.inbox.domain.model.InboxSearchDTO
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountDTO
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestTransactionListResponseDTO
import javax.inject.Inject

class JointAccountRepositoryImpl @Inject constructor(
    private val creationApiUseCase: JointAccountCreationApiUseCase,
    private val signRequestApiUseCase: JointSignRequestApiUseCase,
    private val inboxApiUseCase: JointAccountInboxApiUseCase
) : JointAccountRepository {

    override suspend fun createJointAccount(
        createJointAccountDTO: CreateJointAccountDTO
    ): Result<JointAccountDTO> {
        return creationApiUseCase(createJointAccountDTO)
    }

    override suspend fun proposeSignRequest(
        proposeJointSignRequestDTO: ProposeJointSignRequestDTO
    ): Result<JointSignRequestDTO> {
        return signRequestApiUseCase.proposeSignRequest(proposeJointSignRequestDTO)
    }

    override suspend fun addSignature(
        signRequestId: String,
        signRequestTransactionListResponseDTO: SignRequestTransactionListResponseDTO
    ): Result<JointSignRequestDTO> {
        return signRequestApiUseCase.addSignature(signRequestId, signRequestTransactionListResponseDTO)
    }

    override suspend fun searchSignRequests(
        searchSignRequestsDTO: SearchSignRequestsDTO
    ): Result<List<JointSignRequestDTO>> {
        return signRequestApiUseCase.searchSignRequests(searchSignRequestsDTO)
    }

    override suspend fun getInboxMessages(
        deviceId: Long,
        inboxSearchDTO: InboxSearchDTO
    ): Result<InboxMessagesDTO> {
        return inboxApiUseCase.getInboxMessages(deviceId, inboxSearchDTO)
    }

    override suspend fun deleteInboxJointInvitationNotification(
        deviceId: Long,
        jointAddress: String
    ): Result<Unit> {
        return inboxApiUseCase.deleteInboxJointInvitationNotification(deviceId, jointAddress)
    }
}
