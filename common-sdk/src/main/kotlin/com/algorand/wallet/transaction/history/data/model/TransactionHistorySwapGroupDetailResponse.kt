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

internal data class TransactionHistorySwapGroupDetailResponse(
    @SerializedName("swap_id")
    val swapId: String?,
    @SerializedName("provider")
    val provider: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("asset_in")
    val assetIn: TransactionHistoryAssetSummaryResponse?,
    @SerializedName("asset_out")
    val assetOut: TransactionHistoryAssetSummaryResponse?,
    @SerializedName("amount_in")
    val amountIn: String?,
    @SerializedName("amount_out")
    val amountOut: String?,
    @SerializedName("amount_in_with_slippage")
    val amountInWithSlippage: String?,
    @SerializedName("amount_out_with_slippage")
    val amountOutWithSlippage: String?,
    @SerializedName("transaction_count")
    val transactionCount: Int?,
    @SerializedName("transactions")
    val transactions: List<TransactionHistoryDetailResponse>?,
    @SerializedName("group_id")
    val groupId: String?,
    @SerializedName("confirmed_round")
    val confirmedRound: Long?,
    @SerializedName("round_time")
    val roundTime: Long?
)
