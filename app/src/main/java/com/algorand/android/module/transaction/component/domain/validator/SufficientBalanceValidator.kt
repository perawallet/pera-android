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
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.validator.SufficientBalanceValidator.Payload
import java.math.BigInteger

internal class SufficientBalanceValidator : TransactionValidator<Payload, Unit> {

    override suspend fun invoke(payload: Payload): ValidationResult<Unit> {
        val ownedAssetDataAmount = payload.assetDetail.amount
        val isBalanceSufficient = ownedAssetDataAmount >= payload.amount
        return ValidationResult(isValid = isBalanceSufficient)
    }

    internal data class Payload(
        val senderInfo: AccountInformation,
        val assetDetail: BaseAccountAssetData.BaseOwnedAssetData,
        val amount: BigInteger
    )
}