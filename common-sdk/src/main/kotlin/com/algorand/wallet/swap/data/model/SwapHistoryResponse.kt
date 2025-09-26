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

package com.algorand.wallet.swap.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

internal data class SwapHistoryResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("provider")
    val provider: String?,
    @SerializedName("status")
    val status: SwapHistoryStatusResponse?,
    @SerializedName("completed_datetime")
    val completedDatetime: String?,
    @SerializedName("asset_in")
    val assetIn: SwapQuoteAssetDetailResponse?,
    @SerializedName("asset_out")
    val assetOut: SwapQuoteAssetDetailResponse?,
    @SerializedName("amount_in")
    val amountIn: BigDecimal?,
    @SerializedName("amount_out")
    val amountOut: BigDecimal?,
    @SerializedName("amount_in_usd_value")
    val amountInUsdValue: BigDecimal?,
    @SerializedName("amount_out_usd_value")
    val amountOutUsdValue: BigDecimal?,
    @SerializedName("transaction_group_id")
    val txnGroupId: String?
)
