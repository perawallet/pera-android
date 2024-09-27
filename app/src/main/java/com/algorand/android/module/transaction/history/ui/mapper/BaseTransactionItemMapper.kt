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

import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem

internal interface BaseTransactionItemMapper {
    fun createPayTransactionSendItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.PayItem.PaySendItem

    fun createReceivePayTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.PayItem.PayReceiveItem

    fun createSelfPayTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.PayItem.PaySelfItem

    fun createSendAssetTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendItem

    fun createSendOptOutAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseAssetSendItem.AssetSendOptOutItem

    fun createReceiveAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveItem

    fun createReceiveOptOutItemAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseReceiveItem.AssetReceiveOptOutItem

    fun createOptOutAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.AssetOptOutItem

    fun createSelfAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfItem

    fun createSelfOptInAssetTransferItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        description: String?,
        formattedAmount: String?,
        amountColorRes: Int?
    ): BaseTransactionItem.TransactionItem.AssetTransferItem.BaseSelfItem.AssetSelfOptInItem

    fun createAssetConfigurationItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.AssetConfigurationItem

    fun createApplicationCallItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.ApplicationCallItem

    fun createUndefinedItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.UndefinedItem

    fun createTransactionDateTitle(
        transaction: BaseTransactionHistoryItem.TransactionDateTitle
    ): BaseTransactionItem.StringTitleItem

    fun createPendingTransactionTitle(
        transaction: BaseTransactionHistoryItem.PendingTransactionTitle
    ): BaseTransactionItem.ResourceTitleItem

    fun createKeyRegTransactionItem(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory
    ): BaseTransactionItem.TransactionItem.KeyRegItem
}
