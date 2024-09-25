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
import com.algorand.android.algosdk.component.transaction.model.Transaction
import com.algorand.android.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.module.transaction.component.domain.TransactionConstants.MIN_REQUIRED_BALANCE_PER_ASSET
import com.algorand.android.module.transaction.component.domain.model.TransactionParams
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.usecase.CalculateTransactionFee
import com.algorand.android.module.transaction.component.domain.validator.AddAssetMinRequiredBalanceValidator.Payload
import java.math.BigInteger

internal class AddAssetMinRequiredBalanceValidator(
    private val getAccountMinBalance: GetAccountMinBalance,
    private val calculateTransactionFee: CalculateTransactionFee
) : TransactionValidator<Payload, BigInteger> {

    override suspend fun invoke(payload: Payload): ValidationResult<BigInteger> {
        val minRequiredBalance = getAccountMinBalance(payload.senderInfo) + MIN_REQUIRED_BALANCE_PER_ASSET
        val accountAlgoBalance = payload.senderInfo.amount
        val fee = calculateTransactionFee(payload.params, null)
        val balanceAfterTransaction = accountAlgoBalance - fee
        val isViolated = balanceAfterTransaction < minRequiredBalance
        return ValidationResult(isValid = !isViolated, payload = minRequiredBalance)
    }

    internal data class Payload(
        val params: TransactionParams,
        val transaction: Transaction,
        val senderInfo: AccountInformation
    )
}
