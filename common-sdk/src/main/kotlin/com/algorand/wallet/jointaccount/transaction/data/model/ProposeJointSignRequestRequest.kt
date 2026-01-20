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

import com.google.gson.annotations.SerializedName

data class ProposeJointSignRequestRequest(
    @SerializedName("joint_account_address")
    val jointAccountAddress: String,
    @SerializedName("proposer_address")
    val proposerAddress: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("raw_transaction_lists")
    val rawTransactionLists: List<List<String>>,
    @SerializedName("transaction_signature_lists")
    val transactionSignatureLists: List<List<String?>>
)
