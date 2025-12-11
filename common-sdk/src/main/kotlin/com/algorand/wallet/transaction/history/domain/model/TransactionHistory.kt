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

data class TransactionHistory(
    val id: String,
    val senderAddress: String,
    val block: Long?,
    val time: ZonedDateTime,
    val fee: BigDecimal,
    val type: Type
) {

    sealed interface Type {

        data class Payment(
            val amount: BigDecimal,
            val type: PaymentType
        ) : Type {
            sealed interface PaymentType {
                data object Receive : PaymentType
                data class Send(val receiverAddress: String) : PaymentType
                data object Self : PaymentType
            }
        }

        data class AssetTransfer(
            val assetId: Long,
            val assetUnitName: String,
            val type: AssetTransferType
        ) : Type {

            sealed interface AssetTransferType {
                data class Send(val receiverAddress: String, val amount: BigDecimal) : AssetTransferType
                data class SendOptOut(val amount: BigDecimal) : AssetTransferType
                data class Receive(val amount: BigDecimal) : AssetTransferType
                data class ReceiveOptOut(val amount: BigDecimal) : AssetTransferType
                data class Self(val amount: BigDecimal) : AssetTransferType
                data object OptIn : AssetTransferType
                data object OptOut : AssetTransferType
            }
        }

        data class Swap(
            val groupId: String,
            val assetInId: Long,
            val assetInUnitName: String,
            val assetOutId: Long,
            val assetOutUnitName: String,
            val amountIn: BigDecimal,
            val amountOut: BigDecimal
        ) : Type

        data class AssetConfiguration(val assetId: Long) : Type

        data class ApplicationCall(val applicationId: Long) : Type

        data object KeyRegistration : Type

        data object Heartbeat : Type
    }
}
