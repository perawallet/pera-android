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

package com.algorand.wallet.jointaccount.transaction.data.model

import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.google.gson.annotations.SerializedName

data class JointSignRequestResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("joint_account")
    val jointAccount: JointAccountResponse?,
    @SerializedName("proposer_address")
    val proposerAddress: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("raw_transaction_lists")
    val rawTransactionLists: List<List<String>>?,
    @SerializedName("transaction_lists")
    val transactionLists: List<SignRequestTransactionListResponse>?,
    @SerializedName("expected_expire_datetime")
    val expectedExpireDatetime: String?,
    @SerializedName("status")
    val status: String?
)

data class SignRequestTransactionListResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("raw_transactions")
    val rawTransactions: List<String>?,
    @SerializedName("first_valid_block")
    val firstValidBlock: String?,
    @SerializedName("last_valid_block")
    val lastValidBlock: String?,
    @SerializedName("responses")
    val responses: List<SignRequestTransactionListResponseItem>?,
    @SerializedName("expected_expire_datetime")
    val expectedExpireDatetime: String?
)

data class SignRequestTransactionListResponseItem(
    @SerializedName("address")
    val address: String?,
    @SerializedName("response")
    val response: String?,
    @SerializedName("signatures")
    val signatures: List<String>?
)
