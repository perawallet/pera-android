/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.usecase

import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAsset
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.mapper.AssetTransferAmountAssetPreviewMapper
import com.algorand.android.mapper.AssetTransferAmountPreviewMapper
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.AssetTransferAmountPreview
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencyName
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.primary.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.secondary.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.AssetNotFoundError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.InsufficientBalanceError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.InsufficientBalanceToPayFeeError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.MinBalanceViolationError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.NetworkError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.Valid
import com.algorand.android.module.transaction.component.domain.model.ValidateTransactionAmountPayload
import com.algorand.android.module.transaction.component.domain.usecase.ValidateTransactionAmount
import com.algorand.android.module.transaction.ui.sendasset.domain.GetAssetTransferTargetUser
import com.algorand.android.module.transaction.ui.sendasset.model.SendTransactionPayload
import com.algorand.android.utils.Event
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.formatAmountAsBigInteger
import com.algorand.android.utils.formatAsCurrency
import com.algorand.android.utils.getDecimalSeparator
import com.algorand.android.utils.multiplyOrNull
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

@Suppress("LongParameterList")
class AssetTransferAmountPreviewUseCase @Inject constructor(
    private val assetTransferAmountPreviewMapper: AssetTransferAmountPreviewMapper,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getPrimaryCurrencyName: GetPrimaryCurrencyName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val assetTransferAmountAssetPreviewMapper: AssetTransferAmountAssetPreviewMapper,
    private val transactionTipsUseCase: TransactionTipsUseCase,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val validateTransactionAmount: ValidateTransactionAmount,
    private val getAsset: GetAsset,
    private val getAccountMinBalance: GetAccountMinBalance,
    private val getAssetTransferTargetUser: GetAssetTransferTargetUser
) {

    fun shouldShowTransactionTips(): Boolean {
        return transactionTipsUseCase.shouldShowTransactionTips()
    }

    suspend fun handleNextNavigation(
        preview: AssetTransferAmountPreview,
        assetTransaction: AssetTransaction,
        amount: BigInteger,
        note: String?,
        xnote: String?
    ): AssetTransferAmountPreview {
        return if (assetTransaction.receiverUser != null) {
            val payload = getSendTransactionPayload(assetTransaction, amount, assetTransaction.receiverUser.address)
            preview.copy(navigateToAssetTransferPreview = Event(payload))
        } else {
            val newAssetTransaction = assetTransaction.copy(amount = amount, note = note, xnote = xnote)
            preview.copy(navigateToReceiverAccountSelection = Event(newAssetTransaction))
        }
    }

    suspend fun updatePreviewWithMaximumAmount(
        preview: AssetTransferAmountPreview,
        assetTransaction: AssetTransaction
    ): AssetTransferAmountPreview {
        val baseAssetData = getAccountBaseOwnedAssetData(assetTransaction.senderAddress, assetTransaction.assetId)
        val formattedAmount = baseAssetData?.formattedAmount ?: return preview
        return preview.copy(
            onFormattedMaxAmount = Event(formattedAmount)
        )
    }

    suspend fun getAssetTransferAmountPreview(
        senderAddress: String,
        assetId: Long,
        amount: BigDecimal? = null
    ): AssetTransferAmountPreview {
        val accountAssetData = getAccountBaseOwnedAssetData(senderAddress, assetId)
            ?: return assetTransferAmountPreviewMapper.mapToAssetNotFoundStatePreview()
        val enteredAmountSelectedCurrencyValue = formatEnteredAmount(
            amount = amount ?: BigDecimal.ZERO,
            usdValue = accountAssetData.usdValue,
            usdToDisplayedCurrencyConversionRate = getUsdToDisplayedCurrencyConversionRate(assetId),
            displayCurrencySymbol = getDisplayCurrencySymbol(assetId)
        )
        val decimalSeparator = getDecimalSeparator()
        return assetTransferAmountPreviewMapper.mapToSuccessPreview(
            assetTransferAmountAssetPreview = assetTransferAmountAssetPreviewMapper.mapTo(accountAssetData),
            enteredAmountSelectedCurrencyValue = enteredAmountSelectedCurrencyValue,
            decimalSeparator = decimalSeparator,
            selectedAmount = amount,
            senderAddress = senderAddress,
            accountName = getAccountDisplayName(senderAddress).primaryDisplayName,
            accountIconDrawablePreview = getAccountIconDrawablePreview(senderAddress)
        )
    }

    suspend fun getAmountValidatedPreview(
        preview: AssetTransferAmountPreview,
        amount: BigDecimal
    ): AssetTransferAmountPreview {

        val senderAddress = preview.senderAddress
        val assetId = preview.assetPreview?.assetId
        if (senderAddress == null || assetId == null) return preview

        return when (val result = getTransactionAmountValidationResult(amount, senderAddress, assetId)) {
            AssetNotFoundError -> preview.copy(assetNotFoundErrorEvent = Event(Unit))
            InsufficientBalanceError -> preview.copy(amountIsMoreThanBalanceEvent = Event(Unit))
            InsufficientBalanceToPayFeeError -> preview.copy(insufficientBalanceToPayFeeEvent = Event(Unit))
            MinBalanceViolationError -> preview.copy(minimumBalanceIsViolatedResultEvent = Event(preview.senderAddress))
            NetworkError -> preview.copy(anErrorOccurred = Event(Unit))
            is Valid -> preview.copy(amountIsValidEvent = Event(result.amount))
        }
    }

    suspend fun getCalculatedSendableAmount(address: String, assetId: Long, amount: BigDecimal): BigInteger? {
        val amountInBigInteger = getAmountAsBigInteger(assetId, amount)
        val maximumSendableAmount = getMaximumSendableAmount(address, assetId) ?: return null
        return minOf(amountInBigInteger, maximumSendableAmount)
    }

    private suspend fun getSendTransactionPayload(
        assetTransaction: AssetTransaction,
        amount: BigInteger,
        receiverAddress: String
    ): SendTransactionPayload {
        return SendTransactionPayload(
            assetId = assetTransaction.assetId,
            senderAddress = assetTransaction.senderAddress,
            note = SendTransactionPayload.Note(assetTransaction.note, assetTransaction.xnote),
            amount = amount,
            targetUser = getAssetTransferTargetUser(receiverAddress)
        )
    }

    private fun getUsdToDisplayedCurrencyConversionRate(assetId: Long): BigDecimal {
        return if (shouldUseSecondaryCurrency(assetId)) {
            getUsdToSecondaryCurrencyConversionRate()
        } else {
            getUsdToPrimaryCurrencyConversionRate()
        }
    }

    private fun getDisplayCurrencySymbol(assetId: Long): String {
        return if (shouldUseSecondaryCurrency(assetId)) {
            getSecondaryCurrencySymbol()
        } else {
            getPrimaryCurrencySymbol() ?: getPrimaryCurrencyName()
        }
    }

    private fun shouldUseSecondaryCurrency(assetId: Long): Boolean {
        return assetId == ALGO_ASSET_ID && isPrimaryCurrencyAlgo()
    }

    private fun formatEnteredAmount(
        amount: BigDecimal,
        usdValue: BigDecimal?,
        usdToDisplayedCurrencyConversionRate: BigDecimal?,
        displayCurrencySymbol: String
    ): String? {
        return amount.multiplyOrNull(usdValue)
            ?.multiplyOrNull(usdToDisplayedCurrencyConversionRate)
            ?.formatAsCurrency(displayCurrencySymbol)
    }

    private suspend fun getTransactionAmountValidationResult(
        amount: BigDecimal,
        senderAddress: String,
        assetId: Long
    ): TransactionAmountValidationResult {
        val amountAsBigInt = getAmountAsBigInteger(assetId, amount)
        val payload = ValidateTransactionAmountPayload(amountAsBigInt, senderAddress, assetId)
        return validateTransactionAmount(payload)
    }

    private suspend fun getAmountAsBigInteger(assetId: Long, amount: BigDecimal): BigInteger {
        val assetDecimal = getAsset(assetId)?.getDecimalsOrZero() ?: 0
        return amount.formatAmountAsBigInteger(assetDecimal)
    }

    private suspend fun getMaximumSendableAmount(address: String, assetId: Long): BigInteger? {
        val ownedAssetData = getAccountBaseOwnedAssetData(address, assetId) ?: return null
        return if (assetId == ALGO_ASSET_ID) {
            ownedAssetData.amount - getAccountMinBalance(address) - MIN_FEE.toBigInteger()
        } else {
            ownedAssetData.amount
        }
    }
}
