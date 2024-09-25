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

package com.algorand.android.module.transaction.history.ui.mapper

import com.algorand.android.foundation.common.addHashtagToStart
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem
import javax.inject.Inject

internal class BaseTransactionItemMapperImpl @Inject constructor() : BaseTransactionItemMapper {

    override fun createPayTransactionSendItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.PayItem.PaySendItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.PayItem.PaySendItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createReceivePayTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.PayItem.PayReceiveItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.PayItem.PayReceiveItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createSelfPayTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.PayItem.PaySelfItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.PayItem.PaySelfItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createSendAssetTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createSendOptOutAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendOptOutItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendOptOutItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createReceiveAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createReceiveOptOutItemAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveOptOutItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveOptOutItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createOptOutAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.AssetOptOutItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetTransferItem.AssetOptOutItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes,
                closeToAddress = (transaction.type as? BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.AssetTransfer.OptOut)?.closeToAddress.orEmpty()
            )
        }
    }

    override fun createSelfAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createSelfOptInAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfOptInItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfOptInItem(
                id = id,
                signature = signature,
                description = description,
                isPending = isPending,
                formattedAmount = formattedAmount,
                amountColorRes = amountColorRes
            )
        }
    }

    override fun createAssetConfigurationItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.AssetConfigurationItem {
        val assetConfig =
            transaction.type as BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.AssetConfiguration
        return with(transaction) {
            BaseTransactionItem.TransactionItem.AssetConfigurationItem(
                id = id,
                signature = signature,
                isPending = isPending,
                assetId = assetConfig.assetId,
                description = assetConfig.assetId?.toString()?.addHashtagToStart()
            )
        }
    }

    override fun createApplicationCallItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.ApplicationCallItem {
        return with(transaction) {
            val applicationCall =
                transaction.type as BaseTransactionHistoryItem.BaseTransactionHistory.BaseTransactionType.ApplicationCall
            BaseTransactionItem.TransactionItem.ApplicationCallItem(
                id = id,
                signature = signature,
                isPending = isPending,
                description = applicationCall.applicationId.toString(),
                innerTransactionCount = applicationCall.innerTransactionCount,
                applicationId = applicationCall.applicationId
            )
        }
    }

    override fun createUndefinedItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.UndefinedItem {
        return with(transaction) {
            BaseTransactionItem.TransactionItem.UndefinedItem(
                id = id,
                signature = signature,
                isPending = isPending
            )
        }
    }

    override fun createTransactionDateTitle(
        transaction: BaseTransactionHistoryItem.TransactionDateTitle
    ): BaseTransactionItem.StringTitleItem {
        return BaseTransactionItem.StringTitleItem(transaction.title)
    }

    override fun createPendingTransactionTitle(
        transaction: BaseTransactionHistoryItem.PendingTransactionTitle
    ): BaseTransactionItem.ResourceTitleItem {
        return BaseTransactionItem.ResourceTitleItem(transaction.stringRes)
    }

    override fun createKeyRegTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.KeyRegItem {
        return BaseTransactionItem.TransactionItem.KeyRegItem(
            id = transaction.id,
            signature = transaction.signature,
            isPending = transaction.isPending
        )
    }
}
