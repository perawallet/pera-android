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

package com.algorand.wallet.transaction.history.domain.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class TransactionHistorySwapGroupDetail(
    val id: String,
    val groupId: String,
    val provider: String,
    val status: String,
    val assetInId: Long,
    val assetInUnitName: String,
    val amountIn: BigDecimal,
    val assetOutId: Long,
    val assetOutUnitName: String,
    val amountOut: BigDecimal,
    val transactions: List<TransactionHistory>,
    val confirmedRound: Long?,
    val time: ZonedDateTime
)
