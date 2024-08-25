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

package com.algorand.android.algosdk.component.transaction.mapper.implementation

import android.util.Base64
import com.algorand.algosdk.sdk.SuggestedParams
import com.algorand.android.algosdk.component.transaction.mapper.SuggestedParamsMapper
import com.algorand.android.algosdk.component.transaction.model.SuggestedTransactionParams
import javax.inject.Inject

internal class SuggestedParamsMapperImpl @Inject constructor() : SuggestedParamsMapper {

    override fun invoke(params: SuggestedTransactionParams, addGenesis: Boolean): SuggestedParams {
        return SuggestedParams().apply {
            fee = params.fee
            genesisID = if (addGenesis) params.genesisId else ""
            firstRoundValid = params.lastRound
            lastRoundValid = params.lastRound + ROUND_THRESHOLD
            genesisHash = Base64.decode(params.genesisHash, Base64.DEFAULT)
        }
    }

    companion object {
        private const val ROUND_THRESHOLD = 1000
    }
}
