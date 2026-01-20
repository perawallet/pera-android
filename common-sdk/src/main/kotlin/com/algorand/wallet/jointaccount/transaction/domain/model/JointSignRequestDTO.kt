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

package com.algorand.wallet.jointaccount.transaction.domain.model

import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO

data class JointSignRequestDTO(
    val id: String?,
    val jointAccount: JointAccountDTO?,
    val proposerAddress: String?,
    val type: String?,
    val rawTransactionLists: List<List<String>>?,
    val transactionLists: List<JointSignRequestTransactionListDTO>?,
    val expectedExpireDatetime: String?,
    val status: SignRequestStatus?
)

data class JointSignRequestTransactionListDTO(
    val id: String?,
    val rawTransactions: List<String>?,
    val firstValidBlock: String?,
    val lastValidBlock: String?,
    val responses: List<JointSignRequestTransactionListResponseDTO>?,
    val expectedExpireDatetime: String?
)

data class JointSignRequestTransactionListResponseDTO(
    val address: String?,
    val response: SignRequestResponseType?,
    val signatures: List<String>?
)
