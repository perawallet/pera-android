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
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.validator.ReceiverOptedInToAssetValidator.Payload

internal class ReceiverOptedInToAssetValidator : TransactionValidator<Payload, Unit> {

    override suspend fun invoke(payload: Payload): ValidationResult<Unit> {
        if (payload.assetId == ALGO_ASSET_ID) return ValidationResult(isValid = true)
        val isOptedIn = payload.accountInformation.assetHoldings.any { it.assetId == payload.assetId }
        return ValidationResult(isValid = isOptedIn)
    }

    internal data class Payload(
        val accountInformation: AccountInformation,
        val assetId: Long
    )
}
