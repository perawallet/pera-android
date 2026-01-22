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

import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.data.model.SignRequestTransactionListResponse
import com.algorand.wallet.jointaccount.transaction.data.model.SignRequestTransactionListResponseItem
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestTransactionList
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestTransactionListItem
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import javax.inject.Inject

internal class JointSignRequestMapper @Inject constructor(
    private val jointAccountDTOMapper: JointAccountDTOMapper
) {

    fun mapToJointSignRequest(response: JointSignRequestResponse?): JointSignRequest? {
        return response?.let {
            JointSignRequest(
                id = it.id,
                jointAccount = jointAccountDTOMapper.mapToJointAccountDTO(it.jointAccount),
                proposerAddress = it.proposerAddress,
                type = it.type,
                rawTransactionLists = it.rawTransactionLists,
                transactionLists = it.transactionLists?.map { transactionList ->
                    mapToJointSignRequestTransactionList(transactionList)
                },
                expectedExpireDatetime = it.expectedExpireDatetime,
                status = SignRequestStatus.fromValue(it.status)
            )
        }
    }

    private fun mapToJointSignRequestTransactionList(
        response: SignRequestTransactionListResponse
    ): JointSignRequestTransactionList {
        return JointSignRequestTransactionList(
            id = response.id,
            rawTransactions = response.rawTransactions,
            firstValidBlock = response.firstValidBlock,
            lastValidBlock = response.lastValidBlock,
            responses = response.responses?.map { item ->
                mapToJointSignRequestTransactionListItem(item)
            },
            expectedExpireDatetime = response.expectedExpireDatetime
        )
    }

    private fun mapToJointSignRequestTransactionListItem(
        item: SignRequestTransactionListResponseItem
    ): JointSignRequestTransactionListItem {
        return JointSignRequestTransactionListItem(
            address = item.address,
            response = SignRequestResponseType.fromValue(item.response),
            signatures = item.signatures
        )
    }
}
