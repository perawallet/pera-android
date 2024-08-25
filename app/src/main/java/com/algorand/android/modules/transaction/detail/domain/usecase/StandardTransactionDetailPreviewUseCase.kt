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
import com.algorand.android.account.localaccount.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.modules.transaction.detail.domain.model.BaseTransactionDetail
import com.algorand.android.modules.transaction.detail.domain.model.BaseTransactionDetail.BaseKeyRegTransaction.OfflineKeyRegTransaction
import com.algorand.android.modules.transaction.detail.domain.model.BaseTransactionDetail.BaseKeyRegTransaction.OnlineKeyRegTransaction
import com.algorand.android.modules.transaction.detail.domain.model.TransactionDetailPreview
import com.algorand.android.modules.transaction.detail.ui.mapper.TransactionDetailItemMapper
import com.algorand.android.modules.transaction.detail.ui.mapper.TransactionDetailPreviewMapper
import com.algorand.android.modules.transaction.detail.ui.model.TransactionDetailItem
import com.algorand.android.node.domain.usecase.GetActiveNodeNetworkSlug
import com.algorand.android.tooltip.domain.usecase.TransactionDetailTooltipDisplayPreferenceUseCase
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.DEFAULT_ASSET_DECIMAL
import com.algorand.android.utils.formatNumberWithDecimalSeparators
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@SuppressWarnings("LongParameterList")
class StandardTransactionDetailPreviewUseCase @Inject constructor(
    private val getTransactionDetailUseCase: GetTransactionDetailUseCase,
    private val getTransactionDetailAccountUseCase: GetTransactionDetailAccountUseCase,
    private val transactionDetailItemMapper: TransactionDetailItemMapper,
    private val transactionDetailTooltipDisplayPreferenceUseCase: TransactionDetailTooltipDisplayPreferenceUseCase,
    private val transactionDetailPreviewMapper: TransactionDetailPreviewMapper,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    getAsset: GetAsset,
    getActiveNodeNetworkSlug: GetActiveNodeNetworkSlug,
    clearInnerTransactionStackCacheUseCase: ClearInnerTransactionStackCacheUseCase
) : BaseTransactionDetailPreviewUseCase(
    transactionDetailItemMapper = transactionDetailItemMapper,
    transactionDetailTooltipDisplayPreferenceUseCase = transactionDetailTooltipDisplayPreferenceUseCase,
    clearInnerTransactionStackCacheUseCase = clearInnerTransactionStackCacheUseCase,
    getActiveNodeNetworkSlug = getActiveNodeNetworkSlug,
    getAsset = getAsset
) {

    suspend fun getTransactionDetailPreview(
        transactionId: String,
        publicKey: String,
        isInnerTransaction: Boolean
    ) = flow {
        emit(transactionDetailPreviewMapper.mapTo(isLoading = true, transactionDetailItemList = emptyList()))
        getTransactionDetailUseCase.getTransactionDetail(transactionId).collect { transactionDetailResource ->
            transactionDetailResource.useSuspended(
                onSuccess = { baseTransactionDetail ->
                    val transactionDetailPreview = createTransactionDetailListItems(
                        baseTransactionDetail = baseTransactionDetail,
                        publicKey = publicKey,
                        transactionId = transactionId,
                        isInnerTransaction = isInnerTransaction
                    )
                    emit(transactionDetailPreview)
                }
            )
        }
    }

    @SuppressWarnings("LongMethod")
    suspend fun createTransactionDetailListItems(
        baseTransactionDetail: BaseTransactionDetail,
        publicKey: String,
        transactionId: String,
        isInnerTransaction: Boolean
    ): TransactionDetailPreview {
        val assetId = getTransactionAssetId(baseTransactionDetail)
        val assetDetail = getAsset(assetId)
        val assetDecimal = assetDetail?.getDecimalsOrZero() ?: DEFAULT_ASSET_DECIMAL
        val assetName = AssetName.createShortName(assetDetail?.shortName)
        val isAlgo = assetId == ALGO_ASSET_ID

        val transactionAmount = getTransactionDetailAmount(baseTransactionDetail, false)

        val receiverAccountPublicKey = baseTransactionDetail.receiverAccountAddress.orEmpty()
        val senderAccountPublicKey = baseTransactionDetail.senderAccountAddress.orEmpty()

        val areAccountsInCache = isThereAnyAccountWithAddress(senderAccountPublicKey) ||
            isThereAnyAccountWithAddress(receiverAccountPublicKey)

        val transactionSign = getTransactionSign(
            receiverAccountPublicKey = receiverAccountPublicKey,
            senderAccountPublicKey = senderAccountPublicKey,
            publicKey = publicKey,
            closeToAccountAddress = baseTransactionDetail.closeToAccountAddress,
            areAccountsInCache = areAccountsInCache,
            isKeyReg = baseTransactionDetail is BaseTransactionDetail.BaseKeyRegTransaction
        )

        val isCloseTo = isTransactionCloseTo(baseTransactionDetail)

        val shouldShowCopyAddressTip = transactionDetailTooltipDisplayPreferenceUseCase.shouldShowCopyAddressTip()

        val transactionDetailItemList = mutableListOf<TransactionDetailItem>().apply {
            add(
                createTransactionAmount(
                    transactionSign = transactionSign,
                    transactionAmount = transactionAmount,
                    assetName = assetName,
                    assetDecimal = assetDecimal,
                    isAlgo = isAlgo
                )
            )

            if (isCloseTo) {
                val transactionFullAmount = getTransactionDetailAmount(baseTransactionDetail, true)
                add(
                    createTransactionCloseToAmountItem(
                        transactionSign = transactionSign,
                        transactionFullAmount = transactionFullAmount,
                        assetName = assetName,
                        assetDecimal = assetDecimal,
                        isAlgo = isAlgo
                    )
                )
            }

            if (baseTransactionDetail is BaseTransactionDetail.AssetConfigurationTransaction) {
                add(createTransactionAssetInformationItem(assetId, baseTransactionDetail))
            }

            add(createTransactionStatusItem())

            add(TransactionDetailItem.DividerItem)
            add(
                getTransactionDetailAccountUseCase.getTransactionFromAccount(
                    senderAccountPublicKey,
                    shouldShowCopyAddressTip
                )
            )
            if (receiverAccountPublicKey.isNotBlank()) {
                add(getTransactionDetailAccountUseCase.getTransactionToAccount(receiverAccountPublicKey))
            }

            val closeToAddress = baseTransactionDetail.closeToAccountAddress
            if (isCloseTo && !closeToAddress.isNullOrBlank()) {
                add(getTransactionDetailAccountUseCase.getTransactionCloseToAccount(closeToAddress))
            }
            add(createTransactionFeeItem(baseTransactionDetail.fee))
            add(
                transactionDetailItemMapper.mapToDateItem(
                    labelTextRes = R.string.date,
                    date = getTransactionFormattedDate(baseTransactionDetail.roundTimeAsTimestamp)
                )
            )
            add(
                transactionDetailItemMapper.mapToRoundItem(
                    labelTextRes = R.string.round,
                    round = baseTransactionDetail.confirmedRound?.toString().orEmpty()
                )
            )
            add(
                transactionDetailItemMapper.mapToTransactionIdItem(
                    labelTextRes = getRequiredTransactionIdLabelTextResId(isInnerTransaction = isInnerTransaction),
                    transactionId = transactionId
                )
            )
            add(TransactionDetailItem.DividerItem)

            if (baseTransactionDetail is BaseTransactionDetail.BaseKeyRegTransaction) {
                if (baseTransactionDetail is OnlineKeyRegTransaction) {
                    add(createOnlineKeyRegItem(baseTransactionDetail))
                }

                if (baseTransactionDetail is OfflineKeyRegTransaction) {
                    add(createOfflineKeyRegItem(baseTransactionDetail))
                }
                add(TransactionDetailItem.DividerItem)
            }

            addNoteIfExist(this, baseTransactionDetail.noteInBase64)
            add(createTransactionChipGroupItem(transactionId))
        }

        return transactionDetailPreviewMapper.mapTo(
            isLoading = false,
            transactionDetailItemList = transactionDetailItemList,
            baseTransactionDetail.toolbarTitleResId
        )
    }

    private fun createTransactionAssetInformationItem(
        assetId: Long,
        transactionDetail: BaseTransactionDetail.AssetConfigurationTransaction
    ): TransactionDetailItem.StandardTransactionItem.AssetInformationItem {
        return transactionDetailItemMapper.mapToAssetInformationItem(
            assetFullName = AssetName.create(transactionDetail.name),
            assetShortName = AssetName.createShortName(transactionDetail.unitName),
            assetId = assetId
        )
    }

    private fun createOnlineKeyRegItem(
        onlineKeyReg: OnlineKeyRegTransaction
    ): TransactionDetailItem.BaseKeyRegItem.OnlineKeyRegItem {
        return with(onlineKeyReg) {
            TransactionDetailItem.BaseKeyRegItem.OnlineKeyRegItem(
                voteKey = voteKey,
                selectionKey = selectionKey,
                stateProofKey = stateProofKey,
                validFirstRound = formatNumberWithDecimalSeparators(validFirstRound) ?: validFirstRound.toString(),
                validLastRound = formatNumberWithDecimalSeparators(validLastRound) ?: validLastRound.toString(),
                voteKeyDilution = formatNumberWithDecimalSeparators(voteKeyDilution) ?: voteKeyDilution.toString()
            )
        }
    }

    private fun createOfflineKeyRegItem(
        offlineKeyReg: OfflineKeyRegTransaction
    ): TransactionDetailItem.BaseKeyRegItem.OfflineKeyRegItem {
        return TransactionDetailItem.BaseKeyRegItem.OfflineKeyRegItem(
            participationStatusResId = if (offlineKeyReg.isParticipating) {
                R.string.participating
            } else {
                R.string.not_participating
            }
        )
    }
}
