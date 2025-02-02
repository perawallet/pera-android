/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.algosdk.transaction.sdk.mapper

import android.util.Base64
import com.algorand.algosdk.sdk.SuggestedParams
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams.TransactionFee.FeeType
import com.algorand.wallet.encryption.Base64Manager
import javax.inject.Inject

internal class SuggestedParamsMapperImpl @Inject constructor(
    private val base64Manager: Base64Manager
) : SuggestedParamsMapper {

    override fun invoke(params: SuggestedTransactionParams, addGenesis: Boolean): SuggestedParams {
        return SuggestedParams().apply {
            genesisID = if (addGenesis) params.genesisId else ""
            firstRoundValid = params.lastRound
            lastRoundValid = params.lastRound + ROUND_THRESHOLD
            genesisHash = base64Manager.decode(params.genesisHash, Base64.DEFAULT)
            fee = params.fee.fee
            flatFee = params.fee.type == FeeType.Flat
        }
    }

    companion object {
        private const val ROUND_THRESHOLD = 1000
    }
}
