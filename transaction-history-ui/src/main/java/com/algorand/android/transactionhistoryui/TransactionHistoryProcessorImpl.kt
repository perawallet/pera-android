/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.transactionhistoryui

import com.algorand.android.assetdetail.component.AssetConstants.ALGO_DECIMALS
import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.core.utils.toShortenedAddress
import com.algorand.android.designsystem.R
import com.algorand.android.formatting.formatAmount
import com.algorand.android.formatting.formatAsAlgoAmount
import com.algorand.android.formatting.formatAsAssetAmount
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.ApplicationCall
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.AssetConfiguration
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.AssetTransfer
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.KeyReg
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.Pay.Receive
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.Pay.Self
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.Pay.Send
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.Undefined
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.PendingTransactionTitle
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.TransactionDateTitle
import com.algorand.android.transactionhistoryui.mapper.BaseTransactionItemMapper
import com.algorand.android.transactionhistoryui.model.BaseTransactionItem
import com.algorand.android.transactionhistoryui.usecase.GetTransactionTargetUserDisplayName
import javax.inject.Inject

internal class TransactionHistoryProcessorImpl @Inject constructor(
    private val baseTransactionItemMapper: BaseTransactionItemMapper,
    private val getTransactionTargetUserDisplayName: GetTransactionTargetUserDisplayName,
    private val getAsset: GetAsset,
) : TransactionHistoryProcessor {

    override suspend fun invoke(address: String, txn: BaseTransactionHistoryItem): BaseTransactionItem {
        return when (txn) {
            is BaseTransactionHistory -> {
                when (txn.type) {
                    is Send -> createPayTransactionSendItem(txn, address)
                    is Receive -> createPayTransactionReceiveItem(txn, address)
                    is Self -> createPayTransactionSelfItem(txn, address)
                    is AssetConfiguration -> baseTransactionItemMapper.createAssetConfigurationItem(txn)
                    is AssetTransfer.BaseReceive.Receive -> createAssetTransferReceiveItem(txn, address)
                    is AssetTransfer.BaseReceive.ReceiveOptOut -> createAssetTransferReceiveOptOutItem(txn, address)
                    is AssetTransfer.BaseSelf.Self -> createAssetTransferSelfItem(txn, address)
                    is AssetTransfer.BaseSelf.SelfOptIn -> createAssetTransferSelfOptInItem(txn, address)
                    is AssetTransfer.BaseSend.Send -> createAssetTransferSendItem(txn, address)
                    is AssetTransfer.BaseSend.SendOptOut -> createAssetTransferSendOptOutItem(txn, address)
                    is AssetTransfer.OptOut -> createAssetTransferOptOutItem(txn)
                    is ApplicationCall -> baseTransactionItemMapper.createApplicationCallItem(txn)
                    is KeyReg -> baseTransactionItemMapper.createKeyRegTransactionItem(txn)
                    Undefined -> baseTransactionItemMapper.createUndefinedItem(txn)
                }
            }
            is PendingTransactionTitle -> baseTransactionItemMapper.createPendingTransactionTitle(txn)
            is TransactionDateTitle -> baseTransactionItemMapper.createTransactionDateTitle(txn)
        }
    }

    private suspend fun createPayTransactionSendItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.PayItem.PaySendItem {
        val send = txn.type as Send
        return baseTransactionItemMapper.createPayTransactionSendItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = send.amount
                .formatAmount(decimals = ALGO_DECIMALS, isCompact = true)
                .formatAsAlgoAmount(getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createPayTransactionReceiveItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.PayItem.PayReceiveItem {
        val receive = txn.type as Receive
        return baseTransactionItemMapper.createReceivePayTransactionItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = receive.amount
                .formatAmount(decimals = ALGO_DECIMALS, isCompact = true)
                .formatAsAlgoAmount(getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createPayTransactionSelfItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.PayItem.PaySelfItem {
        val self = txn.type as Self
        return baseTransactionItemMapper.createSelfPayTransactionItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = self.amount
                .formatAmount(decimals = ALGO_DECIMALS, isCompact = true)
                .formatAsAlgoAmount(getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createAssetTransferSendItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendItem {
        val send = txn.type as AssetTransfer.BaseSend.Send
        val assetDetail = getAsset(send.assetId)
        return baseTransactionItemMapper.createSendAssetTransactionItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = send.amount
                .formatAmount(decimals = assetDetail.getFractionDecimalsOrZero(), isCompact = true)
                .formatAsAssetAmount(assetDetail.getShortNameOrEmpty(), getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createAssetTransferSendOptOutItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendOptOutItem {
        val sendOptOut = txn.type as AssetTransfer.BaseSend.SendOptOut
        val assetDetail = getAsset(sendOptOut.assetId)
        return baseTransactionItemMapper.createSendOptOutAssetTransferItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = sendOptOut.amount
                .formatAmount(decimals = assetDetail.getFractionDecimalsOrZero(), isCompact = true)
                .formatAsAssetAmount(assetDetail.getShortNameOrEmpty(), getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createAssetTransferReceiveItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveItem {
        val receive = txn.type as AssetTransfer.BaseReceive.Receive
        val assetDetail = getAsset(receive.assetId)
        return baseTransactionItemMapper.createReceiveAssetTransferItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = receive.amount
                .formatAmount(decimals = assetDetail.getFractionDecimalsOrZero(), isCompact = true)
                .formatAsAssetAmount(assetDetail.getShortNameOrEmpty(), getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createAssetTransferReceiveOptOutItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveOptOutItem {
        val receiveOptOut = txn.type as AssetTransfer.BaseReceive.ReceiveOptOut
        val assetDetail = getAsset(receiveOptOut.assetId)
        return baseTransactionItemMapper.createReceiveOptOutItemAssetTransferItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = receiveOptOut.amount
                .formatAmount(decimals = assetDetail.getFractionDecimalsOrZero(), isCompact = true)
                .formatAsAssetAmount(assetDetail.getShortNameOrEmpty(), getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createAssetTransferOptOutItem(
        txn: BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.AssetOptOutItem {
        val optOut = txn.type as AssetTransfer.OptOut
        val assetDetail = getAsset(optOut.assetId)
        return baseTransactionItemMapper.createOptOutAssetTransferItem(
            transaction = txn,
            description = optOut.closeToAddress.toShortenedAddress(),
            formattedAmount = optOut.amount
                .formatAmount(decimals = assetDetail.getFractionDecimalsOrZero(), isCompact = true)
                .formatAsAssetAmount(assetDetail.getShortNameOrEmpty(), getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createAssetTransferSelfItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfItem {
        val self = txn.type as AssetTransfer.BaseSelf.Self
        val assetDetail = getAsset(self.assetId)
        return baseTransactionItemMapper.createSelfAssetTransferItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = self.amount
                .formatAmount(decimals = assetDetail.getFractionDecimalsOrZero(), isCompact = true)
                .formatAsAssetAmount(assetDetail.getShortNameOrEmpty(), getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private suspend fun createAssetTransferSelfOptInItem(
        txn: BaseTransactionHistory,
        publicKey: String
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfOptInItem {
        val selfOptIn = txn.type as AssetTransfer.BaseSelf.SelfOptIn
        val assetDetail = getAsset(selfOptIn.assetId)
        return baseTransactionItemMapper.createSelfOptInAssetTransferItem(
            transaction = txn,
            description = getTransactionTargetUserDisplayName(txn, publicKey),
            formattedAmount = selfOptIn.amount
                .formatAmount(decimals = assetDetail.getFractionDecimalsOrZero(), isCompact = true)
                .formatAsAssetAmount(assetDetail.getShortNameOrEmpty(), getTransactionSign(txn)),
            amountColorRes = getTransactionColorRes(txn)
        )
    }

    private fun getTransactionSign(transaction: BaseTransactionHistory): String? {
        return if (transaction.type is Send || transaction.type is AssetTransfer.BaseSend) {
            if (isSenderAndReceiverSame(transaction)) null else NEGATIVE_SIGN
        } else if (transaction.type is Receive || transaction.type is AssetTransfer.BaseReceive) {
            if (isSenderAndReceiverSame(transaction)) null else POSITIVE_SIGN
        } else {
            null
        }
    }

    private fun getTransactionColorRes(transaction: BaseTransactionHistory): Int? {
        return if (transaction.type is Send || transaction.type is AssetTransfer.BaseSend) {
            if (isSenderAndReceiverSame(transaction)) null else R.color.transaction_amount_negative_color
        } else if (transaction.type is Receive || transaction.type is AssetTransfer.BaseReceive) {
            if (isSenderAndReceiverSame(transaction)) null else R.color.transaction_amount_positive_color
        } else {
            null
        }
    }

    private fun isSenderAndReceiverSame(transaction: BaseTransactionHistoryItem): Boolean {
        return if (transaction is BaseTransactionHistory) {
            transaction.senderAddress == transaction.receiverAddress
        } else {
            false
        }
    }

    private fun Asset?.getFractionDecimalsOrZero(): Int = this?.assetInfo?.decimals ?: 0

    private fun Asset?.getShortNameOrEmpty(): String = this?.assetInfo?.name?.shortName.orEmpty()

    private companion object {
        const val POSITIVE_SIGN = "+"
        const val NEGATIVE_SIGN = "-"
    }
}
