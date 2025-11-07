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

package com.algorand.android.ui.transaction.history.model

import java.math.BigDecimal

sealed interface TransactionHistoryItem {

    data class Date(val date: String) : TransactionHistoryItem

    data class Receive(
        val id: String,
        val senderAddress: String,
        val formattedAmount: String,
    ) : TransactionHistoryItem

    data class Send(
        val id: String,
        val amount: BigDecimal,
        val receiverAddress: String,
        val formattedAmount: String
    ) : TransactionHistoryItem

    data class Swap(
        val id: String,
        val formattedAmountIn: String,
        val formattedAmountOut: String
    ) : TransactionHistoryItem

    data class OptIn(val id: String, val formattedFee: String) : TransactionHistoryItem

    data class SendOptOut(val id: String, val formattedAmount: String) : TransactionHistoryItem

    data class ReceiveOptOut(val id: String, val formattedAmount: String) : TransactionHistoryItem

    data class OptOut(val id: String, val formattedFee: String) : TransactionHistoryItem

    data class Self(val id: String, val formattedAmount: String) : TransactionHistoryItem

    data class ApplicationCall(val id: String, val formattedFee: String) : TransactionHistoryItem

    data class AssetConfiguration(val id: String, val formattedFee: String) : TransactionHistoryItem

    data class Heartbeat(val id: String, val formattedFee: String) : TransactionHistoryItem

    data class KeyRegistration(val id: String, val formattedFee: String) : TransactionHistoryItem

    data object Separator : TransactionHistoryItem
}
