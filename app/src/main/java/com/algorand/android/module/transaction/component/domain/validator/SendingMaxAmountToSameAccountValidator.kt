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

import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.transaction.component.domain.TransactionConstants.MIN_FEE
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.validator.SendingMaxAmountToSameAccountValidator.Payload
import java.math.BigInteger

internal class SendingMaxAmountToSameAccountValidator(
    private val getAccountMinBalance: GetAccountMinBalance
) : TransactionValidator<Payload, Unit> {

    override suspend fun invoke(payload: Payload): ValidationResult<Unit> {
        val isTheSameAccount = areAccountsTheSame(payload)
        val maxSelectableAmount = getMaxSelectableAmount(payload)
        val isMax = payload.amount >= maxSelectableAmount
        val isValid = !(isMax && isTheSameAccount)
        return ValidationResult(isValid = isValid)
    }

    private fun areAccountsTheSame(payload: Payload): Boolean {
        return with(payload) {
            senderAccountInformation.address == receiverAccountInformation.address
        }
    }

    private suspend fun getMaxSelectableAmount(payload: Payload): BigInteger {
        return with(payload) {
            if (assetId == ALGO_ASSET_ID) {
                senderAccountInformation.amount - getAccountMinBalance(receiverAccountInformation.address) - MIN_FEE
            } else {
                senderAccountInformation.amount
            }
        }
    }

    internal data class Payload(
        val senderAccountInformation: AccountInformation,
        val receiverAccountInformation: AccountInformation,
        val assetId: Long,
        val amount: BigInteger
    )
}
