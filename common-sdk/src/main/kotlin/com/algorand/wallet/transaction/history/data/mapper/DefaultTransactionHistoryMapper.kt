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

package com.algorand.wallet.transaction.history.data.mapper

import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryDetailResponse
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryInterpretedMeaning
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryItemResponse
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.APP_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.ASSET_CONFIGURATION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.ASSET_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.HEARTBEAT_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.KEYREG_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.PAY_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.UNDEFINED
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type
import com.algorand.wallet.utils.date.TimeProvider
import com.algorand.wallet.utils.formatToBigDecimal
import java.math.BigDecimal.ZERO
import javax.inject.Inject

internal class DefaultTransactionHistoryMapper @Inject constructor(
    private val timeProvider: TimeProvider,
    private val paymentTypeMapper: TransactionHistoryPaymentTypeMapper,
    private val assetTransferTypeMapper: TransactionHistoryAssetTransferTypeMapper
) : TransactionHistoryMapper {

    override fun invoke(address: String, response: TransactionHistoryItemResponse): TransactionHistory? {
        return TransactionHistory(
            id = response.transactionId ?: return null,
            senderAddress = response.sender ?: return null,
            block = response.confirmedRound,
            time = timeProvider.getZonedDateTimeFromSeconds(response.roundTime ?: return null),
            fee = response.fee?.formatToBigDecimal(ALGO_DECIMALS) ?: ZERO,
            type = getTransactionType(address, response) ?: return null
        )
    }

    override fun invoke(address: String, response: TransactionHistoryDetailResponse): TransactionHistory? {
        return TransactionHistory(
            id = response.id ?: return null,
            senderAddress = response.sender ?: return null,
            block = response.confirmedRound,
            time = timeProvider.getZonedDateTimeFromSeconds(response.roundTime ?: return null),
            fee = response.fee?.formatToBigDecimal(ALGO_DECIMALS) ?: ZERO,
            type = getTransactionType(address, response) ?: return null
        )
    }

    private fun getTransactionType(address: String, response: TransactionHistoryDetailResponse): Type? {
        return when (response.txType) {
            PAY_TRANSACTION -> paymentTypeMapper(address, response)
            ASSET_TRANSACTION -> assetTransferTypeMapper(address, response)
            APP_TRANSACTION -> mapAppCallTxn(response.applicationTransaction?.applicationId, response.innerTxns?.size)
            ASSET_CONFIGURATION -> Type.AssetConfiguration(response.assetConfigTransaction?.assetId ?: return null)
            KEYREG_TRANSACTION -> Type.KeyRegistration
            HEARTBEAT_TRANSACTION -> Type.Heartbeat
            UNDEFINED, null -> null
        }
    }

    private fun getTransactionType(address: String, response: TransactionHistoryItemResponse): Type? {
        return if (response.interpretedMeaning != null) {
            getTransactionTypeByInterpretedMeaning(response)
        } else {
            getTransactionTypeByRawType(address, response)
        }
    }

    private fun getTransactionTypeByInterpretedMeaning(response: TransactionHistoryItemResponse): Type? {
        return when (response.interpretedMeaning?.type) {
            TransactionHistoryInterpretedMeaning.Type.SWAP -> mapSwapTransaction(response)
            TransactionHistoryInterpretedMeaning.Type.UNKNOWN, null -> null
        }
    }

    private fun getTransactionTypeByRawType(address: String, response: TransactionHistoryItemResponse): Type? {
        return when (response.txType) {
            PAY_TRANSACTION -> paymentTypeMapper(address, response)
            ASSET_TRANSACTION -> assetTransferTypeMapper(address, response)
            APP_TRANSACTION -> mapAppCallTxn(response.applicationId, response.innerTransactionCount)
            ASSET_CONFIGURATION -> Type.AssetConfiguration(response.asset?.id ?: return null)
            KEYREG_TRANSACTION -> Type.KeyRegistration
            HEARTBEAT_TRANSACTION -> Type.Heartbeat
            UNDEFINED, null -> null
        }
    }

    private fun mapAppCallTxn(appId: Long?, innerTxnCount: Int?): Type? {
        return Type.ApplicationCall(
            applicationId = appId ?: return null,
            txnCount = innerTxnCount ?: DEFAULT_INNER_TXN_COUNT
        )
    }

    private fun mapSwapTransaction(response: TransactionHistoryItemResponse): Type.Swap? {
        return response.swapGroupDetail?.run {
            Type.Swap(
                groupId = groupId ?: return null,
                assetInId = assetIn?.id ?: return null,
                assetInUnitName = assetIn.unitName.orEmpty(),
                assetOutId = assetOut?.id ?: return null,
                assetOutUnitName = assetOut.unitName.orEmpty(),
                amountIn = amountIn.formatToBigDecimal(assetIn.decimals) ?: return null,
                amountOut = amountOut.formatToBigDecimal(assetOut.decimals) ?: return null
            )
        }
    }

    private companion object {
        const val DEFAULT_INNER_TXN_COUNT = 0
    }
}
