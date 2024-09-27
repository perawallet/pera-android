@file:Suppress("MaxLineLength", "ReturnCount")
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

package com.algorand.android.module.transaction.component.domain.usecase.implementation

import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.AssetNotFoundError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.InsufficientBalanceError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.InsufficientBalanceToPayFeeError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.MinBalanceViolationError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.NetworkError
import com.algorand.android.module.transaction.component.domain.model.TransactionAmountValidationResult.Valid
import com.algorand.android.module.transaction.component.domain.model.ValidateTransactionAmountPayload
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.usecase.ValidateTransactionAmount
import com.algorand.android.module.transaction.component.domain.validator.SendAssetMinRequiredBalanceValidator
import com.algorand.android.module.transaction.component.domain.validator.SufficientBalanceToPayFeeValidator
import com.algorand.android.module.transaction.component.domain.validator.SufficientBalanceValidator
import com.algorand.android.module.transaction.component.domain.validator.TransactionValidator
import java.math.BigInteger

internal class ValidateTransactionAmountUseCase(
    private val getAccountInformation: GetAccountInformation,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val sufficientBalanceValidator: TransactionValidator<SufficientBalanceValidator.Payload, Unit>,
    private val sufficientBalanceToPayFeeValidator: TransactionValidator<SufficientBalanceToPayFeeValidator.Payload, Unit>,
    private val sendAssetMinRequiredBalanceValidator: TransactionValidator<SendAssetMinRequiredBalanceValidator.Payload, Unit>,
    private val getAccountMinBalance: GetAccountMinBalance
) : ValidateTransactionAmount {

    override suspend fun invoke(payload: ValidateTransactionAmountPayload): TransactionAmountValidationResult {
        with(payload) {
            val senderInfo = getAccountInformation(senderAddress) ?: return NetworkError
            val ownedAssetData = getAccountBaseOwnedAssetData(senderAddress, assetId) ?: return AssetNotFoundError
            val minRequiredBalance = getAccountMinBalance(senderInfo.address)

            isBalanceSufficient(senderInfo, ownedAssetData, amount).run {
                if (!this.isValid) return InsufficientBalanceError
            }

            isBalanceSufficientToPayFee(senderInfo, minRequiredBalance).run {
                if (!this.isValid) return InsufficientBalanceToPayFeeError
            }

            isMinimumBalanceViolated(senderInfo, ownedAssetData, amount, minRequiredBalance).run {
                if (!this.isValid) return MinBalanceViolationError
            }

            return Valid(amount)
        }
    }

    private suspend fun isBalanceSufficient(
        senderInfo: AccountInformation,
        ownedAssetData: BaseAccountAssetData.BaseOwnedAssetData,
        amount: BigInteger
    ): ValidationResult<Unit> {
        val payload = SufficientBalanceValidator.Payload(senderInfo, ownedAssetData, amount)
        return sufficientBalanceValidator(payload)
    }

    private suspend fun isBalanceSufficientToPayFee(
        senderInfo: AccountInformation,
        minRequiredBalance: BigInteger
    ): ValidationResult<Unit> {
        val payload = SufficientBalanceToPayFeeValidator.Payload(senderInfo, minRequiredBalance)
        return sufficientBalanceToPayFeeValidator(payload)
    }

    private suspend fun isMinimumBalanceViolated(
        senderInfo: AccountInformation,
        ownedAssetData: BaseAccountAssetData.BaseOwnedAssetData,
        amount: BigInteger,
        minRequiredBalance: BigInteger
    ): ValidationResult<Unit> {
        val payload =
            SendAssetMinRequiredBalanceValidator.Payload(senderInfo, ownedAssetData, minRequiredBalance, amount)
        return sendAssetMinRequiredBalanceValidator(payload)
    }
}
