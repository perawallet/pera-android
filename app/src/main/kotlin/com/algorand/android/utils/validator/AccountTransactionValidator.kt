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

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.models.Result
import com.algorand.android.modules.accountasset.domain.model.AccountAssetDetail
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.exceptions.WarningException
import com.algorand.android.utils.isEqualTo
import com.algorand.android.utils.isLesserThan
import com.algorand.android.utils.isValidAddress
import com.algorand.android.utils.minBalancePerAssetAsBigInteger
import com.algorand.wallet.account.core.domain.usecase.GetAccountMinBalance
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigInteger
import javax.inject.Inject

class AccountTransactionValidator @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getAccountMinBalance: GetAccountMinBalance
) {

    fun isAccountAddressValid(toAccountPublicKey: String): Result<String> {
        if (toAccountPublicKey.isValidAddress()) {
            return Result.Success(toAccountPublicKey)
        }
        return Result.Error(WarningException(R.string.warning, AnnotatedString(R.string.key_not_valid)))
    }

    suspend fun isSelectedAssetValid(fromAccountPublicKey: String, assetId: Long): Boolean {
        if (assetId == ALGO_ID) {
            return true
        }
        return getAccountInformation(fromAccountPublicKey)?.hasAsset(assetId) == true
    }

    fun isSendingAmountLesserThanMinimumBalance(
        toAccountSelectedAssetBalance: BigInteger,
        amount: BigInteger,
        minBalance: BigInteger
    ): Boolean {
        return (toAccountSelectedAssetBalance + amount) isLesserThan minBalance
    }

    fun isCloseTransactionToSameAccount(
        fromAccount: AccountInformation?,
        toAccount: String,
        ownedAssetData: BaseAccountAssetData.BaseOwnedAssetData?,
        amount: BigInteger
    ): Boolean {
        val isMax = amount == ownedAssetData?.amount
        val hasOnlyAlgo = fromAccount?.run {
            !isThereAnOptedInApp() || !isThereAnOptedInAsset()
        } ?: false
        return fromAccount?.address == toAccount && ownedAssetData?.isAlgo == true && isMax && hasOnlyAlgo
    }

    suspend fun isSendingMaxAmountToTheSameAccount(
        fromAccount: String,
        toAccount: String,
        maxAmount: BigInteger,
        amount: BigInteger,
        isAlgo: Boolean
    ): Boolean {
        val maxSelectableAmount = if (isAlgo) {
            maxAmount - getAccountMinBalance(toAccount) - MIN_FEE.toBigInteger()
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
