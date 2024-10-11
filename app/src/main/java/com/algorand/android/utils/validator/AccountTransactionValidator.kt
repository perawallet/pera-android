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

package com.algorand.android.utils.validator

import com.algorand.android.R
import com.algorand.android.models.AccountCacheData
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.AssetInformation
import com.algorand.android.models.AssetInformation.Companion.ALGO_ID
import com.algorand.android.models.Result
import com.algorand.android.modules.accountasset.domain.model.AccountAssetDetail
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.usecase.GetAccountMinimumBalanceUseCase
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.exceptions.WarningException
import com.algorand.android.utils.extensions.hasAsset
import com.algorand.android.utils.isEqualTo
import com.algorand.android.utils.isLesserThan
import com.algorand.android.utils.isValidAddress
import com.algorand.android.utils.minBalancePerAssetAsBigInteger
import java.math.BigInteger
import javax.inject.Inject

class AccountTransactionValidator @Inject constructor(
    private val accountDetailUseCase: AccountDetailUseCase,
    private val getAccountMinimumBalanceUseCase: GetAccountMinimumBalanceUseCase
) {

    fun isAccountAddressValid(toAccountPublicKey: String): Result<String> {
        if (toAccountPublicKey.isValidAddress()) {
            return Result.Success(toAccountPublicKey)
        }
        return Result.Error(WarningException(R.string.warning, AnnotatedString(R.string.key_not_valid)))
    }

    fun isSelectedAssetValid(fromAccountPublicKey: String, assetId: Long): Boolean {
        val accountDetail = accountDetailUseCase.getCachedAccountDetail(fromAccountPublicKey)?.data
        val isAlgo = assetId == ALGO_ID
        return isAlgo || accountDetail?.hasAsset(assetId) == true
    }

    fun isSendingAmountLesserThanMinimumBalance(
        toAccountSelectedAssetBalance: BigInteger,
        amount: BigInteger,
        minBalance: BigInteger
    ): Boolean {
        return (toAccountSelectedAssetBalance + amount) isLesserThan minBalance
    }

    fun isCloseTransactionToSameAccount(
        fromAccount: AccountCacheData?,
        toAccount: String,
        selectedAsset: AssetInformation?,
        amount: BigInteger
    ): Boolean {
        val isMax = amount == selectedAsset?.amount
        val hasOnlyAlgo = fromAccount?.accountInformation?.run {
            !isThereAnOptedInApp() || !isThereAnyDifferentAsset()
        } ?: false
        return fromAccount?.account?.address == toAccount && selectedAsset?.isAlgo() == true && isMax && hasOnlyAlgo
    }

    fun isSendingMaxAmountToTheSameAccount(
        fromAccount: String,
        toAccount: String,
        maxAmount: BigInteger,
        amount: BigInteger,
        isAlgo: Boolean
    ): Boolean {
        val maxSelectableAmount = if (isAlgo) {
            maxAmount - getAccountMinimumBalanceUseCase.getAccountMinimumBalance(toAccount) - MIN_FEE.toBigInteger()
        } else {
            maxAmount
        }
        val isMax = amount >= maxSelectableAmount
        val isTheSameAccount = fromAccount == toAccount
        return isMax && isTheSameAccount
    }

    fun isAccountNewlyOpenedAndBalanceInvalid(
        accountAssetDetail: AccountAssetDetail,
        amount: BigInteger,
        assetId: Long
    ): Boolean {
        return assetId == ALGO_ID &&
            accountAssetDetail.algoAmount isEqualTo BigInteger.ZERO &&
            amount < minBalancePerAssetAsBigInteger
    }
}
