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

import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.validator.SendingLessAmountThanRequiredValidator.Payload
import java.math.BigInteger

internal class SendingLessAmountThanRequiredValidator(
    private val getAccountMinBalance: GetAccountMinBalance
) : TransactionValidator<Payload, BigInteger> {

    override suspend fun invoke(payload: Payload): ValidationResult<BigInteger> {
        if (payload.assetId != ALGO_ASSET_ID) return ValidationResult(isValid = true)
        val requiredMinBalance = getAccountMinBalance(payload.receiverAccountInformation)
        val algoBalance = payload.receiverAccountInformation.amount
        val isAmountValid = algoBalance + payload.sendingAmount >= requiredMinBalance
        val requiredMinSendingAmount = requiredMinBalance - algoBalance
        return ValidationResult(isValid = isAmountValid, payload = requiredMinSendingAmount)
    }

    internal class Payload(
        val receiverAccountInformation: AccountInformation,
        val assetId: Long,
        val sendingAmount: BigInteger
    )
}