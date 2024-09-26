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

package com.algorand.android.modules.transaction.detail.domain.mapper

import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.modules.transaction.common.domain.model.TransactionDTO
import com.algorand.android.modules.transaction.detail.domain.model.BaseTransactionDetail
import com.algorand.android.modules.transaction.detail.domain.model.BaseTransactionDetail.BaseKeyRegTransaction.OfflineKeyRegTransaction
import com.algorand.android.modules.transaction.detail.domain.model.BaseTransactionDetail.BaseKeyRegTransaction.OnlineKeyRegTransaction
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.getAllNestedTransactions
import java.math.BigInteger
import javax.inject.Inject

class BaseTransactionDetailMapper @Inject constructor() {

    fun mapToPaymentTransactionDetail(
        transactionDTO: TransactionDTO
    ): BaseTransactionDetail.PaymentTransaction {
        return with(transactionDTO) {
            BaseTransactionDetail.PaymentTransaction(
                id = id,
                signature = signature?.signatureKey,
                senderAccountAddress = senderAddress,
                receiverAccountAddress = payment?.receiverAddress ?: assetTransfer?.receiverAddress
                ?: assetFreezeTransaction?.receiverAddress.orEmpty(),
                closeToAccountAddress = payment?.closeToAddress ?: assetTransfer?.closeTo,
                roundTimeAsTimestamp = roundTimeAsTimestamp,
                confirmedRound = confirmedRound,
                transactionAmount = payment?.amount ?: assetTransfer?.amount ?: BigInteger.ZERO,
                transactionCloseAmount = closeAmount,
                fee = fee?.toBigInteger() ?: BigInteger.valueOf(MIN_FEE),
                noteInBase64 = noteInBase64
            )
        }
    }

    fun mapToAssetTransferTransactionDetail(
        transactionDTO: TransactionDTO
    ): BaseTransactionDetail.AssetTransferTransaction {
        return with(transactionDTO) {
            BaseTransactionDetail.AssetTransferTransaction(
                id = id,
                signature = signature?.signatureKey,
                senderAccountAddress = senderAddress,
                receiverAccountAddress = payment?.receiverAddress ?: assetTransfer?.receiverAddress
                ?: assetFreezeTransaction?.receiverAddress.orEmpty(),
                roundTimeAsTimestamp = roundTimeAsTimestamp,
                confirmedRound = confirmedRound,
                transactionAmount = payment?.amount ?: assetTransfer?.amount ?: BigInteger.ZERO,
                fee = fee?.toBigInteger() ?: BigInteger.valueOf(MIN_FEE),
                noteInBase64 = noteInBase64,
                assetId = assetTransfer?.assetId ?: assetFreezeTransaction?.assetId ?: assetConfiguration?.assetId
                ?: applicationCall?.foreignAssets?.firstNotNullOfOrNull { it } ?: ALGO_ASSET_ID,
                transactionCloseAmount = closeAmount,
                closeToAccountAddress = payment?.closeToAddress ?: assetTransfer?.closeTo
            )
        }
    }

    fun mapToAssetConfigurationTransactionDetail(
        transactionDTO: TransactionDTO
    ): BaseTransactionDetail.AssetConfigurationTransaction {
        return with(transactionDTO) {
            BaseTransactionDetail.AssetConfigurationTransaction(
                id = id,
                signature = signature?.signatureKey,
                senderAccountAddress = senderAddress,
                receiverAccountAddress = payment?.receiverAddress ?: assetTransfer?.receiverAddress
                ?: assetFreezeTransaction?.receiverAddress.orEmpty(),
                roundTimeAsTimestamp = roundTimeAsTimestamp,
                confirmedRound = confirmedRound,
                fee = fee?.toBigInteger() ?: BigInteger.valueOf(MIN_FEE),
                noteInBase64 = noteInBase64,
                assetId = createdAssetIndex ?: assetConfiguration?.assetId,
                closeToAccountAddress = null, // Asset Configuration Transaction does not contain close to address
                transactionAmount = null, // Asset Configuration Transaction does not contain amount
                transactionCloseAmount = closeAmount,
                name = assetConfiguration?.name,
                unitName = assetConfiguration?.unitName
            )
        }
    }

    fun mapToApplicationCallTransactionDetail(
        transactionDTO: TransactionDTO,
        innerTransactions: List<BaseTransactionDetail>
    ): BaseTransactionDetail.ApplicationCallTransaction {
        return with(transactionDTO) {
            BaseTransactionDetail.ApplicationCallTransaction(
                id = id,
                signature = signature?.signatureKey,
                senderAccountAddress = senderAddress,
                receiverAccountAddress = payment?.receiverAddress ?: assetTransfer?.receiverAddress
                ?: assetFreezeTransaction?.receiverAddress.orEmpty(),
                roundTimeAsTimestamp = roundTimeAsTimestamp,
                confirmedRound = confirmedRound,
                fee = fee?.toBigInteger() ?: BigInteger.valueOf(MIN_FEE),
                noteInBase64 = noteInBase64,
                onCompletion = applicationCall?.onCompletion,
                applicationId = applicationCall?.applicationId,
                innerTransactions = innerTransactions,
                innerTransactionCount = getAllNestedTransactions(this).count(),
                foreignAssetIds = applicationCall?.foreignAssets
            )
        }
    }

    fun mapToKeyRegTransactionDetail(transactionDto: TransactionDTO): BaseTransactionDetail.BaseKeyRegTransaction {
        return with(transactionDto) {
            val isOnlineKeyReg: Boolean = keyRegTransactionDTO?.run {
                voteKey != null && selectionKey != null && stateProofKey != null && validFirstRound != null &&
                    validLastRound != null && voteKeyDilution != null
            } ?: false
            if (isOnlineKeyReg) {
                mapToOnlineKeyRegTransactionDetail(transactionDto)
            } else {
                mapToOfflineKeyRegTransactionDetail(transactionDto)
            }
        }
    }

    fun mapToUndefinedTransactionDetail(transactionDTO: TransactionDTO): BaseTransactionDetail.UndefinedTransaction {
        return with(transactionDTO) {
            BaseTransactionDetail.UndefinedTransaction(
                id = id,
                signature = signature?.signatureKey,
                senderAccountAddress = senderAddress,
                receiverAccountAddress = payment?.receiverAddress ?: assetTransfer?.receiverAddress
                ?: assetFreezeTransaction?.receiverAddress.orEmpty(),
                roundTimeAsTimestamp = roundTimeAsTimestamp,
                confirmedRound = confirmedRound,
                fee = fee?.toBigInteger() ?: BigInteger.valueOf(MIN_FEE),
                noteInBase64 = noteInBase64
            )
        }
    }

    private fun mapToOnlineKeyRegTransactionDetail(transactionDto: TransactionDTO): OnlineKeyRegTransaction {
        return with(transactionDto) {
            OnlineKeyRegTransaction(
                id = id,
                signature = signature?.signatureKey,
                senderAccountAddress = senderAddress,
                receiverAccountAddress = null,
                closeToAccountAddress = null,
                roundTimeAsTimestamp = roundTimeAsTimestamp,
                confirmedRound = confirmedRound,
                transactionAmount = null,
                transactionCloseAmount = null,
                fee = fee?.toBigInteger() ?: BigInteger.valueOf(MIN_FEE),
                noteInBase64 = noteInBase64,
                voteKey = keyRegTransactionDTO?.voteKey.orEmpty(),
                selectionKey = keyRegTransactionDTO?.selectionKey.orEmpty(),
                stateProofKey = keyRegTransactionDTO?.stateProofKey.orEmpty(),
                validFirstRound = keyRegTransactionDTO?.validFirstRound ?: 0,
                validLastRound = keyRegTransactionDTO?.validLastRound ?: 0,
                voteKeyDilution = keyRegTransactionDTO?.voteKeyDilution ?: 0
            )
        }
    }

    private fun mapToOfflineKeyRegTransactionDetail(transactionDto: TransactionDTO): OfflineKeyRegTransaction {
        return with(transactionDto) {
            OfflineKeyRegTransaction(
                id = id,
                signature = signature?.signatureKey,
                senderAccountAddress = senderAddress,
                receiverAccountAddress = null,
                closeToAccountAddress = null,
                roundTimeAsTimestamp = roundTimeAsTimestamp,
                confirmedRound = confirmedRound,
                transactionAmount = null,
                transactionCloseAmount = null,
                fee = fee?.toBigInteger() ?: BigInteger.valueOf(MIN_FEE),
                noteInBase64 = noteInBase64,
                isParticipating = keyRegTransactionDTO?.nonParticipation == false
            )
        }
    }
}
