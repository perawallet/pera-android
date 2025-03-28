/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.summary.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

class Arc59SendSummaryResponse(
    @SerializedName("is_arc59_opted_in")
    val isArc59OptedIn: Boolean?,

    @SerializedName("minimum_balance_requirement")
    val minimumBalanceRequirement: Long?,

    @SerializedName("inner_tx_count")
    val innerTxCount: Int?,

    @SerializedName("total_protocol_and_mbr_fee")
    val totalProtocolAndMbrFee: BigInteger?,

    @SerializedName("inbox_address")
    val inboxAddress: String?,

    @SerializedName("algo_fund_amount")
    val algoFundAmount: BigInteger?,

    @SerializedName("warning_message")
    val warningMessage: Arc59WarningMessageResponse?
)
