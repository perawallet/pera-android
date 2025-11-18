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

package com.algorand.android.modules.transactionhistory.domain.model

import androidx.annotation.StringRes
import java.math.BigInteger
import java.time.ZonedDateTime

sealed class BaseTransaction {

    data class TransactionDateTitle(val title: String) : BaseTransaction()

    data class PendingTransactionTitle(@param:StringRes val stringRes: Int) : BaseTransaction()

    sealed class Transaction : BaseTransaction() {

        abstract val id: String?
        abstract val signature: String?
        abstract val senderAddress: String?
        abstract val receiverAddress: String?
        abstract val zonedDateTime: ZonedDateTime?
        abstract val isPending: Boolean

        sealed class Pay : Transaction() {

            abstract val amount: BigInteger

            data class Send(
                override val id: String?,
                override val signature: String?,
                override val senderAddress: String?,
                override val receiverAddress: String?,
                override val zonedDateTime: ZonedDateTime?,
                override val isPending: Boolean,
                override val amount: BigInteger
            ) : Pay()

            data class Receive(
                override val id: String?,
                override val signature: String?,
                override val senderAddress: String?,
                override val receiverAddress: String?,
                override val zonedDateTime: ZonedDateTime?,
                override val isPending: Boolean,
                override val amount: BigInteger
            ) : Pay()

            data class Self(
                override val id: String?,
                override val signature: String?,
                override val senderAddress: String?,
                override val receiverAddress: String?,
                override val zonedDateTime: ZonedDateTime?,
                override val isPending: Boolean,
                override val amount: BigInteger
            ) : Pay()
        }

        sealed class AssetTransfer : Transaction() {

            abstract val assetId: Long
            abstract val amount: BigInteger

            sealed class BaseSend : AssetTransfer() {
                data class Send(
                    override val id: String?,
                    override val signature: String?,
                    override val senderAddress: String?,
                    override val receiverAddress: String?,
                    override val zonedDateTime: ZonedDateTime?,
                    override val isPending: Boolean,
                    override val amount: BigInteger,
                    override val assetId: Long
                ) : BaseSend()

                data class SendOptOut(
                    override val id: String?,
                    override val signature: String?,
                    override val senderAddress: String?,
                    override val receiverAddress: String?,
                    override val zonedDateTime: ZonedDateTime?,
                    override val isPending: Boolean,
                    override val amount: BigInteger,
                    override val assetId: Long,
                    val closeToAddress: String
                ) : BaseSend()
            }

            sealed class BaseReceive : AssetTransfer() {

                data class Receive(
                    override val id: String?,
                    override val signature: String?,
                    override val senderAddress: String?,
                    override val receiverAddress: String?,
                    override val zonedDateTime: ZonedDateTime?,
                    override val isPending: Boolean,
                    override val amount: BigInteger,
                    override val assetId: Long
                ) : BaseReceive()

                data class ReceiveOptOut(
                    override val id: String?,
                    override val signature: String?,
                    override val senderAddress: String?,
                    override val receiverAddress: String?,
                    override val zonedDateTime: ZonedDateTime?,
                    override val isPending: Boolean,
                    override val amount: BigInteger,
                    override val assetId: Long
                ) : BaseReceive()
            }

            data class OptOut(
                override val id: String?,
                override val signature: String?,
                override val senderAddress: String?,
                override val receiverAddress: String?,
                override val zonedDateTime: ZonedDateTime?,
                override val isPending: Boolean,
                override val assetId: Long,
                override val amount: BigInteger,
                val closeToAddress: String
            ) : AssetTransfer()

            sealed class BaseSelf : AssetTransfer() {
                data class Self(
                    override val id: String?,
                    override val signature: String?,
                    override val senderAddress: String?,
                    override val receiverAddress: String?,
                    override val zonedDateTime: ZonedDateTime?,
                    override val isPending: Boolean,
                    override val assetId: Long,
                    override val amount: BigInteger
                ) : BaseSelf()

                data class SelfOptIn(
                    override val id: String?,
                    override val signature: String?,
                    override val senderAddress: String?,
                    override val receiverAddress: String?,
                    override val zonedDateTime: ZonedDateTime?,
                    override val isPending: Boolean,
                    override val assetId: Long,
                    override val amount: BigInteger
                ) : BaseSelf()
            }
        }

        data class AssetConfiguration(
            override val id: String?,
            override val signature: String?,
            override val senderAddress: String?,
            override val receiverAddress: String?,
            override val zonedDateTime: ZonedDateTime?,
            override val isPending: Boolean,
            val assetId: Long?
        ) : Transaction()

        data class ApplicationCall(
            override val id: String?,
            override val signature: String?,
            override val senderAddress: String?,
            override val receiverAddress: String?,
            override val zonedDateTime: ZonedDateTime?,
            override val isPending: Boolean,
            val applicationId: Long?,
            val innerTransactionCount: Int,
            val foreignAssetIds: List<Long>?
        ) : Transaction()

        sealed class KeyReg : Transaction() {

            data class Online(
                override val id: String?,
                override val signature: String?,
                override val senderAddress: String?,
                override val receiverAddress: String?,
                override val zonedDateTime: ZonedDateTime?,
                override val isPending: Boolean,
                val voteKey: String,
                val selectionKey: String,
                val stateProofKey: String,
                val voteFirstValidRound: Long,
                val voteLastValidRound: Long,
                val voteKeyDilution: Long
            ) : KeyReg()

            data class Offline(
                override val id: String?,
                override val signature: String?,
                override val senderAddress: String?,
                override val receiverAddress: String?,
                override val zonedDateTime: ZonedDateTime?,
                override val isPending: Boolean,
                val nonParticipating: Boolean
            ) : KeyReg()
        }

        data class Heartbeat(
            override val id: String?,
            override val signature: String?,
            override val senderAddress: String?,
            override val receiverAddress: String?,
            override val zonedDateTime: ZonedDateTime?,
            override val isPending: Boolean
        ) : Transaction()

        data class Undefined(
            override val id: String? = null,
            override val signature: String? = null,
            override val senderAddress: String?,
            override val receiverAddress: String?,
            override val zonedDateTime: ZonedDateTime? = null,
            override val isPending: Boolean = false,
        ) : Transaction()
    }
}
