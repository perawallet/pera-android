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

package com.algorand.android.transaction.data.mapper

import com.algorand.android.transaction.data.model.TransactionParamsResponse
import com.algorand.android.transaction.domain.model.TransactionParams
import javax.inject.Inject

internal class TransactionParamsMapperImpl @Inject constructor() : TransactionParamsMapper {

    override fun invoke(response: TransactionParamsResponse): TransactionParams? {
        if (!response.isValid()) return null
        return TransactionParams(
            minFee = response.minFee!!,
            fee = response.fee!!,
            genesisHash = response.genesisHash!!,
            genesisId = response.genesisId!!,
            lastRound = response.lastRound!!
        )
    }

    private fun TransactionParamsResponse.isValid(): Boolean {
        return fee != null && genesisHash != null && genesisId != null && lastRound != null
    }
}
