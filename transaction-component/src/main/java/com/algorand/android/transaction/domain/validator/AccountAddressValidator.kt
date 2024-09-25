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

import com.algorand.android.transaction.domain.model.ValidationResult
import com.algorand.android.transaction.domain.usecase.IsValidAlgorandAddress

internal class AccountAddressValidator(
    private val isValidAlgorandAddress: IsValidAlgorandAddress
) : TransactionValidator<String, Unit> {

    override suspend fun invoke(payload: String): ValidationResult<Unit> {
        return ValidationResult(
            isValid = isValidAlgorandAddress(payload),
        )
    }
}