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
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.validator.SenderOptedInToAssetValidator.Payload

internal class SenderOptedInToAssetValidator : TransactionValidator<Payload, Unit> {

    override suspend fun invoke(payload: Payload): ValidationResult<Unit> {
        val isSenderOptedInToAsset = isSenderOptedInToAsset(payload)
        return ValidationResult(isValid = isSenderOptedInToAsset)
    }

    private fun isSenderOptedInToAsset(payload: Payload): Boolean {
        if (payload.assetId == ALGO_ASSET_ID) return true
        return payload.senderAccountInformation.assetHoldings.any { it.assetId == payload.assetId }
    }

    internal data class Payload(
        val senderAccountInformation: AccountInformation,
        val assetId: Long
    )
}
