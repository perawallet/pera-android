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
import com.algorand.android.accountinfo.component.domain.model.AssetStatus
import com.algorand.android.transaction.domain.model.ValidationResult
import com.algorand.android.transaction.domain.validator.RemoveAssetAssetStatusValidator.Payload
import javax.inject.Inject

internal class RemoveAssetAssetStatusValidator @Inject constructor() : TransactionValidator<Payload, Unit> {

    override suspend fun invoke(payload: Payload): ValidationResult<Unit> {
        val assetHolding = payload.accountInformation.assetHoldings
            .firstOrNull { it.assetId == payload.assetId }
            ?: return ValidationResult(isValid = false)
        val isValid = assetHolding.status != AssetStatus.PENDING_FOR_REMOVAL
        return ValidationResult(isValid = isValid)
    }

    internal data class Payload(
        val accountInformation: AccountInformation,
        val assetId: Long
    )
}
