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

package com.algorand.android.modules.transactionhistory.domain.mapper

import com.algorand.android.modules.transaction.common.domain.model.TransactionDTO
import com.algorand.android.modules.transaction.common.domain.model.getReceiverAddress
import com.algorand.android.modules.transactionhistory.domain.model.BaseTransaction
import com.algorand.android.utils.getAllNestedTransactions
import com.algorand.android.utils.getZonedDateTimeFromTimeStamp
import java.math.BigInteger
import javax.inject.Inject

class BaseTransactionMapper @Inject constructor() {
    fun mapToPayTransactionSend(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.Pay.Send {
        return with(transactionDTO) {
            BaseTransaction.Transaction.Pay.Send(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = payment?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = payment?.amount ?: BigInteger.ZERO
            )
        }
    }

    fun mapToPayTransactionReceive(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.Pay.Receive {
        return with(transactionDTO) {
            BaseTransaction.Transaction.Pay.Receive(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = payment?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = payment?.amount ?: BigInteger.ZERO
            )
        }
    }

    fun mapToPayTransactionSelf(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.Pay.Self {
        return with(transactionDTO) {
            BaseTransaction.Transaction.Pay.Self(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = payment?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = payment?.amount ?: BigInteger.ZERO
            )
        }
    }

    fun mapToAssetTransactionSend(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.AssetTransfer.BaseSend.Send? {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetTransfer.BaseSend.Send(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = assetTransfer?.amount ?: BigInteger.ZERO,
                assetId = assetTransfer?.assetId ?: return null
            )
        }
    }

    fun mapToAssetTransactionReceive(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.AssetTransfer.BaseReceive.Receive? {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetTransfer.BaseReceive.Receive(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = assetTransfer?.amount ?: BigInteger.ZERO,
                assetId = assetTransfer?.assetId ?: return null
            )
        }
    }

    fun mapToAssetTransactionReceiveOptOut(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.AssetTransfer.BaseReceive.ReceiveOptOut? {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetTransfer.BaseReceive.ReceiveOptOut(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = assetTransfer?.amount ?: BigInteger.ZERO,
                assetId = assetTransfer?.assetId ?: return null
            )
        }
    }

    fun mapToAssetTransactionOptOut(
        transactionDTO: TransactionDTO,
        closeToAddress: String,
    ): BaseTransaction.Transaction.AssetTransfer.OptOut? {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetTransfer.OptOut(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = assetTransfer?.amount ?: BigInteger.ZERO,
                assetId = assetTransfer?.assetId ?: return null,
                closeToAddress = closeToAddress
            )
        }
    }

    fun mapToAssetTransactionSendOptOut(
        transactionDTO: TransactionDTO,
        closeToAddress: String,
    ): BaseTransaction.Transaction.AssetTransfer.BaseSend.SendOptOut? {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetTransfer.BaseSend.SendOptOut(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = assetTransfer?.amount ?: BigInteger.ZERO,
                assetId = assetTransfer?.assetId ?: return null,
                closeToAddress = closeToAddress
            )
        }
    }

    fun mapToAssetTransactionSelf(
        transactionDTO: TransactionDTO,
    ): BaseTransaction.Transaction.AssetTransfer.BaseSelf.Self? {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetTransfer.BaseSelf.Self(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = transactionDTO.getReceiverAddress(),
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = assetTransfer?.amount ?: BigInteger.ZERO,
                assetId = assetTransfer?.assetId ?: return null
            )
        }
    }

    fun mapToAssetTransactionSelfOptIn(
        transactionDTO: TransactionDTO,
    ): BaseTransaction.Transaction.AssetTransfer.BaseSelf.SelfOptIn? {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetTransfer.BaseSelf.SelfOptIn(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = assetTransfer?.receiverAddress,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                amount = assetTransfer?.amount ?: BigInteger.ZERO,
                assetId = assetTransfer?.assetId ?: return null
            )
        }
    }

    fun mapToAssetConfiguration(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.AssetConfiguration {
        return with(transactionDTO) {
            BaseTransaction.Transaction.AssetConfiguration(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = transactionDTO.getReceiverAddress(),
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                assetId = createdAssetIndex ?: assetConfiguration?.assetId
            )
        }
    }

    fun mapToOnlineKeyReg(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.KeyReg.Online {
        return with(transactionDTO) {
            BaseTransaction.Transaction.KeyReg.Online(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = null,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                voteKey = keyRegTransactionDTO?.voteKey.orEmpty(),
                selectionKey = keyRegTransactionDTO?.selectionKey.orEmpty(),
                stateProofKey = keyRegTransactionDTO?.stateProofKey.orEmpty(),
                voteFirstValidRound = keyRegTransactionDTO?.validFirstRound ?: 0,
                voteLastValidRound = keyRegTransactionDTO?.validLastRound ?: 0,
                voteKeyDilution = keyRegTransactionDTO?.voteKeyDilution ?: 0
            )
        }
    }

    fun mapToOfflineKeyReg(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.KeyReg.Offline {
        return with(transactionDTO) {
            BaseTransaction.Transaction.KeyReg.Offline(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = null,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                nonParticipating = keyRegTransactionDTO?.nonParticipation ?: false
            )
        }
    }

    fun mapToApplicationCall(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.ApplicationCall {
        return with(transactionDTO) {
            BaseTransaction.Transaction.ApplicationCall(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = transactionDTO.getReceiverAddress(),
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false,
                applicationId = applicationCall?.applicationId,
                innerTransactionCount = getAllNestedTransactions(this).count(),
                foreignAssetIds = applicationCall?.foreignAssets
            )
        }
    }

    fun mapToHeartbeat(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.Heartbeat {
        return with(transactionDTO) {
            BaseTransaction.Transaction.Heartbeat(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = null,
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false
            )
        }
    }

    fun mapToUndefined(
        transactionDTO: TransactionDTO
    ): BaseTransaction.Transaction.Undefined {
        return with(transactionDTO) {
            BaseTransaction.Transaction.Undefined(
                id = id,
                signature = signature?.signatureKey,
                senderAddress = senderAddress.orEmpty(),
                receiverAddress = transactionDTO.getReceiverAddress(),
                zonedDateTime = roundTimeAsTimestamp?.getZonedDateTimeFromTimeStamp(),
                isPending = false
            )
        }
    }

    fun mapToTransactionDateTitle(date: String): BaseTransaction.TransactionDateTitle {
        return BaseTransaction.TransactionDateTitle(date)
    }
}
