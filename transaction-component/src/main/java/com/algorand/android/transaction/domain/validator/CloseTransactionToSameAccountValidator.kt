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

package com.algorand.android.transaction.domain.validator

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.transaction.domain.model.ValidationResult
import com.algorand.android.transaction.domain.validator.CloseTransactionToSameAccountValidator.Payload
import java.math.BigInteger

internal class CloseTransactionToSameAccountValidator : TransactionValidator<Payload, Unit> {

    override suspend fun invoke(payload: Payload): ValidationResult<Unit> {
        val isSendingMaxAmount = isSendingMaxAmount(payload)
        val hasOnlyAlgo = hasSenderOnlyAlgo(payload.senderAccountInformation)
        val isSendingAlgo = isSendingAlgo(payload)
        val areAccountsTheSame = areAccountsTheSame(payload)
        val isValid = !(isSendingAlgo && isSendingMaxAmount && hasOnlyAlgo && areAccountsTheSame)
        return ValidationResult(isValid = isValid)
    }

    private fun isSendingAlgo(payload: Payload): Boolean {
        return payload.assetId == ALGO_ASSET_ID
    }

    private fun isSendingMaxAmount(payload: Payload): Boolean {
        return payload.amount == payload.senderAccountInformation.amount
    }

    private fun areAccountsTheSame(payload: Payload): Boolean {
        return payload.senderAccountInformation.address == payload.receiverAddress
    }

    private fun hasSenderOnlyAlgo(senderAccountInformation: AccountInformation): Boolean {
        return senderAccountInformation.totalAppsOptedIn == 0 || senderAccountInformation.totalAssetsOptedIn == 0
    }

    internal data class Payload(
        val senderAccountInformation: AccountInformation,
        val receiverAddress: String,
        val assetId: Long,
        val amount: BigInteger
    )
}
