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

package com.algorand.android.transaction.domain.usecase.implementation

import com.algorand.android.transaction.domain.TransactionConstants.MIN_FEE
import com.algorand.android.transaction.domain.usecase.CalculateRekeyFee
import com.algorand.android.transaction.domain.usecase.GetTransactionParams
import javax.inject.Inject
import kotlin.math.max

internal class CalculateRekeyFeeUseCase @Inject constructor(
    private val getTransactionParams: GetTransactionParams
) : CalculateRekeyFee {

    override suspend fun invoke(): Long {
        val minFee = MIN_FEE.toLong()
        return getTransactionParams().use(
            onSuccess = { params ->
                max(REKEY_BYTE_ARRAY_SIZE * params.fee, params.minFee ?: minFee)
            },
            onFailed = { _, _ ->
                minFee
            }
        )
    }

    private companion object {
        const val REKEY_BYTE_ARRAY_SIZE = 30
    }
}
