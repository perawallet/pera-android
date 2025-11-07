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
    @SerializedName("group_id")
    val groupId: String?,
    @SerializedName("tx_type")
    val txType: TransactionTypeResponse?,
    @SerializedName("sender")
    val sender: String?,
    @SerializedName("confirmed_round")
    val confirmedRound: Long?,
    @SerializedName("round_time")
    val roundTime: Long?,
    @SerializedName("receiver")
    val receiver: String?,
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("asset_id")
    val assetId: Long?,
    @SerializedName("asset_unit_name")
    val assetUnitName: String?,
    @SerializedName("asset_decimals")
    val assetDecimals: Int?,
    @SerializedName("application_id")
    val applicationId: Long?,
    @SerializedName("close_to_address") // TODO Update when it is implemented in the API
    val closeToAddress: String?,
    @SerializedName("fee")
    val fee: String?, // TODO Update when it is implemented in the API
    @SerializedName("swap_metadata")
    val swapMetadata: TransactionHistoryItemSwapMetadataResponse?
)
