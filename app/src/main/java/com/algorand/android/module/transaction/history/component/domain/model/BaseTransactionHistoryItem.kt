package com.algorand.android.module.transaction.history.component.domain.model

import androidx.annotation.StringRes
import java.math.BigInteger
import java.time.ZonedDateTime

sealed class BaseTransactionHistoryItem {

    data class TransactionDateTitle(val title: String) : BaseTransactionHistoryItem()

    data class PendingTransactionTitle(@StringRes val stringRes: Int) : BaseTransactionHistoryItem()

    data class BaseTransactionHistory(
        val id: String?,
        val signature: String?,
        val senderAddress: String?,
        val receiverAddress: String?,
        val zonedDateTime: ZonedDateTime?,
        val isPending: Boolean,
        val type: BaseTransactionType,
        val roundTimeAsTimestamp: Long
    ) : BaseTransactionHistoryItem() {

        sealed class BaseTransactionType {

            sealed class Pay : BaseTransactionType() {

                abstract val amount: BigInteger

                data class Send(override val amount: BigInteger) : Pay()

                data class Receive(override val amount: BigInteger) : Pay()

                data class Self(override val amount: BigInteger) : Pay()
            }

            sealed class AssetTransfer : BaseTransactionType() {

                abstract val assetId: Long
                abstract val amount: BigInteger

                sealed class BaseSend : AssetTransfer() {

                    data class Send(override val assetId: Long, override val amount: BigInteger) : BaseSend()

                    data class SendOptOut(
                        override val assetId: Long,
                        override val amount: BigInteger,
                        val closeToAddress: String
                    ) : BaseSend()
                }

                sealed class BaseReceive : AssetTransfer() {

                    data class Receive(override val assetId: Long, override val amount: BigInteger) : BaseReceive()

                    data class ReceiveOptOut(override val assetId: Long, override val amount: BigInteger) :
                        BaseReceive()
                }

                data class OptOut(
                    override val assetId: Long,
                    override val amount: BigInteger,
                    val closeToAddress: String
                ) : AssetTransfer()

                sealed class BaseSelf : AssetTransfer() {

                    data class Self(override val assetId: Long, override val amount: BigInteger) : BaseSelf()

                    data class SelfOptIn(override val assetId: Long, override val amount: BigInteger) : BaseSelf()
                }
            }

            data class AssetConfiguration(val assetId: Long?) : BaseTransactionType()

            data class ApplicationCall(
                val applicationId: Long?,
                val innerTransactionCount: Int,
                val foreignAssetIds: List<Long>?
            ) : BaseTransactionType()

            sealed class KeyReg : BaseTransactionType() {

                data class Online(
                    val voteKey: String,
                    val selectionKey: String,
                    val stateProofKey: String,
                    val voteFirstValidRound: Long,
                    val voteLastValidRound: Long,
                    val voteKeyDilution: Long
                ) : KeyReg()

                data class Offline(val nonParticipating: Boolean) : KeyReg()
            }

            data object Undefined : BaseTransactionType()
        }
    }
}
