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

package com.algorand.android.modules.transaction.detail.domain.usecase

import com.algorand.android.R
import com.algorand.android.module.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAsset
import com.algorand.android.module.node.domain.usecase.GetActiveNodeNetworkSlug
import com.algorand.android.modules.transaction.detail.domain.model.BaseTransactionDetail
import com.algorand.android.modules.transaction.detail.domain.model.TransactionSign
import com.algorand.android.modules.transaction.detail.ui.mapper.TransactionDetailItemMapper
import com.algorand.android.modules.transaction.detail.ui.mapper.TransactionDetailPreviewMapper
import com.algorand.android.modules.transaction.detail.ui.model.TransactionDetailItem
import com.algorand.android.tooltip.domain.usecase.TransactionDetailTooltipDisplayPreferenceUseCase
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.DEFAULT_ASSET_DECIMAL
import com.algorand.android.utils.appendAssetName
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@SuppressWarnings("LongParameterList")
class InnerTransactionDetailPreviewUseCase @Inject constructor(
    private val transactionDetailPreviewMapper: TransactionDetailPreviewMapper,
    private val transactionDetailItemMapper: TransactionDetailItemMapper,
    private val peekInnerTransactionFromCacheUseCase: PeekInnerTransactionFromCacheUseCase,
    private val popInnerTransactionFromStackCacheUseCase: PopInnerTransactionFromStackCacheUseCase,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    getAsset: GetAsset,
    getActiveNodeNetworkSlug: GetActiveNodeNetworkSlug,
    transactionDetailTooltipDisplayPreferenceUseCase: TransactionDetailTooltipDisplayPreferenceUseCase,
    clearInnerTransactionStackCacheUseCase: ClearInnerTransactionStackCacheUseCase
) : BaseTransactionDetailPreviewUseCase(
    transactionDetailItemMapper = transactionDetailItemMapper,
    transactionDetailTooltipDisplayPreferenceUseCase = transactionDetailTooltipDisplayPreferenceUseCase,
    clearInnerTransactionStackCacheUseCase = clearInnerTransactionStackCacheUseCase,
    getActiveNodeNetworkSlug = getActiveNodeNetworkSlug,
    getAsset = getAsset
) {

    suspend fun peekInnerTransactionFromCache(): List<BaseTransactionDetail> {
        return peekInnerTransactionFromCacheUseCase.peekInnerTransactionFromCacheUseCase() ?: emptyList()
    }

    suspend fun popInnerTransactionFromStackCache() {
        popInnerTransactionFromStackCacheUseCase.popInnerTransactionFromStackCache()
    }

    suspend fun getTransactionDetailPreview(publicKey: String, transactions: List<BaseTransactionDetail>) = flow {
        val transactionDetailItemList = mutableListOf<TransactionDetailItem>().apply {
            transactions.forEach { baseTransactionDetail ->
                when (baseTransactionDetail) {
                    is BaseTransactionDetail.ApplicationCallTransaction -> {
                        createApplicationCallTransactionItem(baseTransactionDetail)
                    }
                    is BaseTransactionDetail.AssetConfigurationTransaction,
                    is BaseTransactionDetail.AssetTransferTransaction,
                    is BaseTransactionDetail.PaymentTransaction,
                    is BaseTransactionDetail.UndefinedTransaction,
                    is BaseTransactionDetail.BaseKeyRegTransaction -> {
                        createStandardTransactionItem(transaction = baseTransactionDetail, publicKey = publicKey)
                    }
                }.apply { add(this) }
            }
            val innerTransactionTitleItem = transactionDetailItemMapper.mapToInnerTransactionTitleItem(
                innerTransactionCount = transactions.count()
            )
            add(TITLE_ITEM_INDEX, innerTransactionTitleItem)
        }
        emit(
            transactionDetailPreviewMapper.mapTo(
                isLoading = false,
                transactionDetailItemList = transactionDetailItemList,
                toolbarTitleResId = R.string.inner_transactions
            )
        )
    }

    private fun createApplicationCallTransactionItem(
        transaction: BaseTransactionDetail.ApplicationCallTransaction
    ): TransactionDetailItem.InnerTransactionItem.ApplicationInnerTransactionItem {
        return transactionDetailItemMapper.mapToApplicationInnerTransactionItem(
            accountAddress = transaction.senderAccountAddress.toShortenedAddress(),
            transactionSign = TransactionSign.POSITIVE,
            innerTransactionCount = transaction.innerTransactionCount,
            transaction = transaction
        )
    }

    private suspend fun createStandardTransactionItem(
        transaction: BaseTransactionDetail,
        publicKey: String
    ): TransactionDetailItem.InnerTransactionItem.StandardInnerTransactionItem {
        val transactionAmount = getTransactionDetailAmount(transaction, true)
        val assetId = getTransactionAssetId(transaction)
        val assetDetail = getAsset(assetId)
        val assetDecimal = assetDetail?.getDecimalsOrZero() ?: DEFAULT_ASSET_DECIMAL
        val assetName = AssetName.createShortName(assetDetail?.shortName)
        val isAlgo = assetId == ALGO_ASSET_ID
        val formattedTransactionAmount = with(transactionAmount.formatAmount(assetDecimal)) {
            if (isAlgo) formatAsAlgoAmount() else appendAssetName(assetName)
        }

        val receiverAccountPublicKey = transaction.receiverAccountAddress.orEmpty()
        val senderAccountPublicKey = transaction.senderAccountAddress.orEmpty()

        val areAccountsInCache = isThereAnyAccountWithAddress(senderAccountPublicKey) ||
            isThereAnyAccountWithAddress(receiverAccountPublicKey)

        return transactionDetailItemMapper.mapToStandardInnerTransactionItem(
            accountAddress = transaction.senderAccountAddress.toShortenedAddress(),
            transactionSign = getTransactionSign(
                receiverAccountPublicKey = transaction.receiverAccountAddress.orEmpty(),
                senderAccountPublicKey = transaction.senderAccountAddress.orEmpty(),
                publicKey = publicKey,
                closeToAccountAddress = transaction.closeToAccountAddress,
                areAccountsInCache = areAccountsInCache,
                isKeyReg = transaction is BaseTransactionDetail.BaseKeyRegTransaction
            ),
            transactionAmount = transactionAmount,
            formattedTransactionAmount = formattedTransactionAmount,
            transaction = transaction
        )
    }

    companion object {
        private const val TITLE_ITEM_INDEX = 0
    }
}
