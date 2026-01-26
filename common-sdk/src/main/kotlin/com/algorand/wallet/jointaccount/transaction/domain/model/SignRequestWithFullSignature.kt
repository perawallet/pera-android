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

import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount

data class SignRequestWithFullSignature(
    val id: Long?,
    val type: String?,
    val jointAccount: JointAccount?,
    val proposerAddress: String?,
    val lastValidExpectedDatetime: String?,
    val transactionLists: List<TransactionListWithFullSignature>?,
    val status: SignRequestStatus?
)

data class TransactionListWithFullSignature(
    val rawTransactions: List<String>?,
    val firstValidBlock: Long?,
    val lastValidBlock: Long?,
    val responses: List<ParticipantSignature>?,
    val lastValidExpectedDatetime: String?
)
