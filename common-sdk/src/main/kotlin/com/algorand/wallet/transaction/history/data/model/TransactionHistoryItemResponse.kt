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

package com.algorand.wallet.transaction.history.data.model

import com.google.gson.annotations.SerializedName

internal data class TransactionHistoryItemResponse(
    @SerializedName("id")
    val transactionId: String?,
    @SerializedName("tx_type")
    val txType: TransactionTypeResponse?,
    @SerializedName("sender")
    val sender: String?,
    @SerializedName("receiver")
    val receiver: String?,
    @SerializedName("confirmed_round")
    val confirmedRound: Long?,
    @SerializedName("round_time")
    val roundTime: Long?,
    @SerializedName("swap_group_detail")
    val swapGroupDetail: TransactionHistorySwapGroupDetailResponse?,
    @SerializedName("interpreted_meaning")
    val interpretedMeaning: TransactionHistoryInterpretedMeaning?,
    @SerializedName("fee")
    val fee: String?,
    @SerializedName("group_id")
    val groupId: String?,
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("close_to")
    val closeToAddress: String?,
    @SerializedName("asset")
    val asset: TransactionHistoryAssetSummaryResponse?,
    @SerializedName("application_id")
    val applicationId: Long?,
    @SerializedName("inner_transaction_count")
    val innerTransactionCount: Int?
)
