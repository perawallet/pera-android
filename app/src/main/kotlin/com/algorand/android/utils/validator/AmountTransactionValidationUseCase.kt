/*
 * Copyright 2025 Pera Wallet, LDA
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

package com.algorand.android.utils.validator

import com.algorand.android.models.AssetTransferAmountValidationResult
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.formatAmountAsBigInteger
import com.algorand.android.utils.isGreaterThan
import com.algorand.android.utils.isLesserThan
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldingAmount
import com.algorand.wallet.account.info.domain.usecase.IsAccountOptedInToAnyApp
import com.algorand.wallet.account.info.domain.usecase.IsAccountOptedInToAnyAsset
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.asset.domain.util.AssetConstants
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

class AmountTransactionValidationUseCase @Inject constructor(
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getAsset: GetAsset,
    private val getAccountLite: GetAccountLite,
    private val isAccountOptedInToAnyApp: IsAccountOptedInToAnyApp,
    private val isAccountOptedInToAnyAsset: IsAccountOptedInToAnyAsset,
    private val getAccountAssetHoldingAmount: GetAccountAssetHoldingAmount
) {

    suspend fun validateAssetAmount(
        amountInBigDecimal: BigDecimal,
        senderAddress: String,
        assetId: Long
    ): AssetTransferAmountValidationResult {
        val isAmountBiggerThanBalance = isAmountBiggerThanBalance(
            address = senderAddress,
            assetId = assetId,
            amount = amountInBigDecimal
        )
        val isBalanceInsufficientForPayingFee = isBalanceInsufficientForPayingFee(senderAddress)
        val isMinimumBalanceViolated = isMinimumBalanceViolated(
            address = senderAddress,
            assetId = assetId,
            amount = amountInBigDecimal
        )
        val selectedAmount = getAmountAsBigInteger(amountInBigDecimal, assetId)
        return AssetTransferAmountValidationResult(
            isAmountMoreThanBalance = isAmountBiggerThanBalance,
            isBalanceInsufficientForPayingFee = isBalanceInsufficientForPayingFee,
            isMinimumBalanceViolated = isMinimumBalanceViolated,
            selectedAmount = selectedAmount
        )
    }

    suspend fun getMaximumSendableAmount(address: String, assetId: Long): BigInteger? {
        return if (assetId == AssetConstants.ALGO_ID) {
            val accountLiteCachedInfo = getAccountLite(address)?.cachedInfo ?: return null
            val algoAmount = accountLiteCachedInfo.algoAmountValue.amount
            val minRequiredBalance = accountLiteCachedInfo.minRequiredBalance
            algoAmount - minRequiredBalance - MIN_FEE.toBigInteger()
        } else {
            getAccountAssetHoldingAmount(address, assetId)
        }
    }

    private suspend fun isAmountBiggerThanBalance(address: String, assetId: Long, amount: BigDecimal): Boolean? {
        val ownedAssetData = getAccountBaseOwnedAssetData(address, assetId) ?: return null
        val amountAsBigInteger = amount.formatAmountAsBigInteger(ownedAssetData.decimals)
        return amountAsBigInteger.isGreaterThan(ownedAssetData.amount)
    }

    private suspend fun isBalanceInsufficientForPayingFee(address: String): Boolean? {
        val accountLiteCachedInfo = getAccountLite(address)?.cachedInfo ?: return null
        val requiredMinBalance = accountLiteCachedInfo.minRequiredBalance
        val algoAmount = accountLiteCachedInfo.algoAmountValue.amount
        return algoAmount.isLesserThan(requiredMinBalance + MIN_FEE.toBigInteger())
    }

    private suspend fun isMinimumBalanceViolated(address: String, assetId: Long, amount: BigDecimal): Boolean? {
        val accountLiteCachedInfo = getAccountLite(address)?.cachedInfo ?: return null
        val requiredMinBalance = accountLiteCachedInfo.minRequiredBalance
        val ownedAssetData = getAccountBaseOwnedAssetData(address, assetId) ?: return null
        val isThereAnotherAsset = isAccountOptedInToAnyAsset(address)
        val isThereAppOptedIn = isAccountOptedInToAnyApp(address)
        val amountAsBigInteger = amount.formatAmountAsBigInteger(ownedAssetData.decimals)
        return ownedAssetData.isAlgo &&
            (ownedAssetData.amount - amountAsBigInteger - MIN_FEE.toBigInteger()) isLesserThan requiredMinBalance &&
            (isThereAnotherAsset || isThereAppOptedIn)
    }

    suspend fun getAmountAsBigInteger(amount: BigDecimal, assetId: Long): BigInteger? {
        val assetDetail = getAsset(assetId) ?: return null
        val assetDecimal = assetDetail.assetInfo?.decimals ?: return null
        return amount.formatAmountAsBigInteger(assetDecimal)
    }
}
