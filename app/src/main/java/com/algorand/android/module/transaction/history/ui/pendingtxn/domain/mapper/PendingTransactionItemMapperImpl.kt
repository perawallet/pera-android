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

package com.algorand.android.module.transaction.history.ui.pendingtxn.domain.mapper

import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_DECIMALS
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.ApplicationCallItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.AssetConfigurationItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.AssetTransferItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.PayItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.PayItem.PayReceiveItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem.TransactionItem.PayItem.PaySendItem
import com.algorand.android.module.transaction.history.ui.usecase.GetTransactionTargetUserDisplayName
import com.algorand.android.module.transaction.component.pendingtxn.domain.model.PendingTransaction
import com.algorand.android.module.transaction.component.pendingtxn.domain.model.TransactionType.APP_TRANSACTION
import com.algorand.android.module.transaction.component.pendingtxn.domain.model.TransactionType.ASSET_CONFIGURATION
import com.algorand.android.module.transaction.component.pendingtxn.domain.model.TransactionType.ASSET_TRANSACTION
import com.algorand.android.module.transaction.component.pendingtxn.domain.model.TransactionType.PAY_TRANSACTION
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.formatAsAlgoAmount
import javax.inject.Inject

internal class PendingTransactionItemMapperImpl @Inject constructor(
    private val getTransactionTargetUserDisplayName: GetTransactionTargetUserDisplayName
) : PendingTransactionItemMapper {

    override suspend fun invoke(pendingTransaction: PendingTransaction, address: String): BaseTransactionItem {
        return when (pendingTransaction.detail?.transactionType) {
            PAY_TRANSACTION -> createBasePayTransaction(pendingTransaction, address)
            ASSET_TRANSACTION -> createBaseAssetTransferItem(pendingTransaction, address)
            ASSET_CONFIGURATION -> createAssetConfigurationItem(pendingTransaction, address)
            APP_TRANSACTION -> createApplicationCallItem(pendingTransaction, address)
            else -> createUndefinedItem(pendingTransaction, address)
        }
    }

    private suspend fun createBasePayTransaction(pendingTransaction: PendingTransaction, address: String): PayItem {
        return if (pendingTransaction.isSendTransaction(address)) {
            PaySendItem(
                id = null,
                signature = pendingTransaction.signatureKey,
                isPending = true,
                formattedAmount = getFormattedAmount(pendingTransaction, address),
                description = getTransactionTargetUserDisplayName(pendingTransaction, address)
            )
        } else {
            PayReceiveItem(
                id = null,
                signature = pendingTransaction.signatureKey,
                isPending = true,
                formattedAmount = getFormattedAmount(pendingTransaction, address),
                description = getTransactionTargetUserDisplayName(pendingTransaction, address)
            )
        }
    }

    private suspend fun createBaseAssetTransferItem(
        pendingTransaction: PendingTransaction,
        address: String
    ): AssetTransferItem {
        return if (pendingTransaction.isSendTransaction(address)) {
            AssetSendItem(
                id = null,
                signature = pendingTransaction.signatureKey,
                isPending = true,
                formattedAmount = getFormattedAmount(pendingTransaction, address),
                description = getTransactionTargetUserDisplayName(pendingTransaction, address)
            )
        } else {
            AssetReceiveItem(
                id = null,
                signature = pendingTransaction.signatureKey,
                isPending = true,
                formattedAmount = getFormattedAmount(pendingTransaction, address),
                description = getTransactionTargetUserDisplayName(pendingTransaction, address)
            )
        }
    }

    private suspend fun createAssetConfigurationItem(
        pendingTransaction: PendingTransaction,
        address: String
    ): AssetConfigurationItem {
        return AssetConfigurationItem(
            id = null,
            signature = pendingTransaction.signatureKey,
            isPending = true,
            formattedAmount = getFormattedAmount(pendingTransaction, address),
            description = getTransactionTargetUserDisplayName(pendingTransaction, address),
            assetId = pendingTransaction.detail?.assetId ?: ALGO_ASSET_ID
        )
    }

    private suspend fun createApplicationCallItem(
        pendingTransaction: PendingTransaction,
        address: String
    ): ApplicationCallItem {
        return ApplicationCallItem(
            id = null,
            signature = pendingTransaction.signatureKey,
            isPending = true,
            formattedAmount = getFormattedAmount(pendingTransaction, address),
            description = getTransactionTargetUserDisplayName(pendingTransaction, address),
            applicationId = null,
            innerTransactionCount = 0
        )
    }

    private suspend fun createUndefinedItem(
        pendingTransaction: PendingTransaction,
        address: String
    ): BaseTransactionItem.TransactionItem.UndefinedItem {
        return BaseTransactionItem.TransactionItem.UndefinedItem(
            id = null,
            signature = pendingTransaction.signatureKey,
            isPending = true,
            formattedAmount = getFormattedAmount(pendingTransaction, address),
            description = getTransactionTargetUserDisplayName(pendingTransaction, address)
        )
    }

    private fun getFormattedAmount(pendingTransaction: PendingTransaction, address: String): String {
        return pendingTransaction.detail?.amount
            .formatAmount(decimals = ALGO_DECIMALS, isCompact = true)
            .formatAsAlgoAmount(pendingTransaction.getTransactionSign(address))
    }
}
