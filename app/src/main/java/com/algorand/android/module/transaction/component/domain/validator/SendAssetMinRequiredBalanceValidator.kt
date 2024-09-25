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

package com.algorand.android.module.transaction.component.domain.validator

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.transaction.component.domain.TransactionConstants.MIN_FEE
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.validator.SendAssetMinRequiredBalanceValidator.Payload
import java.math.BigInteger

internal class SendAssetMinRequiredBalanceValidator : TransactionValidator<Payload, Unit> {

    override suspend fun invoke(payload: Payload): ValidationResult<Unit> {
        with(payload) {
            val isThereAnotherOptedInAsset = senderInfo.assetHoldings.isNotEmpty()
            val isThereAppOptedIn = senderInfo.totalAppsOptedIn > 0
            val isAlgo = ownedAssetData.isAlgo
            val remainingBalanceAfterTransaction = ownedAssetData.amount - amount - MIN_FEE
            val isMinRequiredBalanceViolated = isAlgo &&
                remainingBalanceAfterTransaction < minRequiredBalance &&
                (isThereAnotherOptedInAsset || isThereAppOptedIn)
            return ValidationResult(isValid = !isMinRequiredBalanceViolated)
        }
    }

    internal data class Payload(
        val senderInfo: AccountInformation,
        val ownedAssetData: BaseAccountAssetData.BaseOwnedAssetData,
        val minRequiredBalance: BigInteger,
        val amount: BigInteger
    )
}
