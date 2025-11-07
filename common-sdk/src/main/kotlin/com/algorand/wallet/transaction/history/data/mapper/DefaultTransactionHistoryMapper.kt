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
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryItemResponse
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.APP_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.ASSET_CONFIGURATION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.ASSET_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.HEARTBEAT_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.KEYREG_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.PAY_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.SWAP_TRANSACTION
import com.algorand.wallet.transaction.history.data.model.TransactionTypeResponse.UNDEFINED
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType.OptIn
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType.OptOut
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType.ReceiveOptOut
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType.SendOptOut
import com.algorand.wallet.utils.date.TimeProvider
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import javax.inject.Inject

internal class DefaultTransactionHistoryMapper @Inject constructor(
    private val timeProvider: TimeProvider
) : TransactionHistoryMapper {

    override fun invoke(address: String, response: TransactionHistoryItemResponse): TransactionHistory? {
        return TransactionHistory(
            id = response.transactionId ?: return null,
            senderAddress = response.sender ?: return null,
            block = response.confirmedRound,
            time = timeProvider.getZonedDateTimeFromSeconds(response.roundTime ?: return null),
            fee = response.fee?.toBigDecimalOrNull()?.movePointLeft(ALGO_DECIMALS) ?: ZERO,
            type = getTransactionType(address, response) ?: return null
        )
    }

    private fun getTransactionType(address: String, response: TransactionHistoryItemResponse): Type? {
        return when (response.txType) {
            PAY_TRANSACTION -> mapPaymentTransaction(address, response)
            ASSET_TRANSACTION -> mapAssetTransaction(address, response)
            APP_TRANSACTION -> Type.ApplicationCall(response.applicationId ?: return null)
            ASSET_CONFIGURATION -> Type.AssetConfiguration(response.assetId ?: return null)
            KEYREG_TRANSACTION -> Type.KeyRegistration
            HEARTBEAT_TRANSACTION -> Type.Heartbeat
            SWAP_TRANSACTION -> mapSwapTransaction(response)
            UNDEFINED, null -> null
        }
    }

    private fun mapSwapTransaction(response: TransactionHistoryItemResponse): Type.Swap? {
        return response.swapMetadata?.run {
            Type.Swap(
                assetInId = assetInId ?: return null,
                assetInUnitName = assetInUnitName.orEmpty(),
                assetOutId = assetOutId ?: return null,
                assetOutUnitName = assetOutUnitName.orEmpty(),
                amountIn = amountInWithSlippage.formatToBigDecimal(assetInDecimals) ?: return null,
                amountOut = amountOutWithSlippage.formatToBigDecimal(assetOutDecimals) ?: return null
            )
        }
    }

    private fun mapPaymentTransaction(address: String, response: TransactionHistoryItemResponse): Type.Payment? {
        val paymentType = when {
            address == response.receiver && address == response.sender -> Type.Payment.PaymentType.Self
            address == response.receiver -> Type.Payment.PaymentType.Receive
            address == response.sender -> Type.Payment.PaymentType.Send(response.receiver ?: return null)
            else -> null
        } ?: return null
        return Type.Payment(amount = response.amount?.formatToBigDecimal(ALGO_DECIMALS) ?: return null, paymentType)
    }

    private fun mapAssetTransaction(address: String, response: TransactionHistoryItemResponse): Type.AssetTransfer? {
        val assetTransferType = with(response) {
            val assetAmount = amount.formatToBigDecimal(assetDecimals)
            when {
                !closeToAddress.isNullOrBlank() && closeToAddress == address -> ReceiveOptOut(assetAmount ?: ZERO)
                !closeToAddress.isNullOrBlank() && assetAmount != null && assetAmount > ZERO -> SendOptOut(assetAmount)
                !closeToAddress.isNullOrBlank() -> OptOut
                isSelfTransaction(address, response) && assetAmount == ZERO -> OptIn
                isSelfTransaction(address, response) -> AssetTransferType.Self(assetAmount ?: return null)
                isReceiveTransaction(address, response) -> AssetTransferType.Receive(assetAmount ?: return null)
                else -> AssetTransferType.Send(response.receiver ?: return null, assetAmount ?: return null)
            }
        }
        return Type.AssetTransfer(
            assetId = response.assetId ?: return null,
            assetUnitName = response.assetUnitName.orEmpty(),
            type = assetTransferType
        )
    }

    private fun isSelfTransaction(address: String, response: TransactionHistoryItemResponse): Boolean {
        return address == response.sender && address == response.receiver
    }

    private fun isReceiveTransaction(address: String, response: TransactionHistoryItemResponse): Boolean {
        return address == response.receiver || address == response.closeToAddress
    }

    private fun String?.formatToBigDecimal(decimals: Int?): BigDecimal? {
        return this?.toBigDecimalOrNull()?.movePointLeft(decimals ?: 0)?.stripTrailingZeros()
    }
}
