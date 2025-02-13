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

package com.algorand.android.modules.algosdk.domain.usecase

import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.modules.algosdk.domain.model.OnlineKeyRegTransactionPayload
import com.algorand.android.utils.extensions.standardizeBase64
import com.algorand.android.utils.toBigIntegerOrZero
import com.algorand.android.utils.toSuggestedParams
import com.algorand.android.utils.toUint64
import javax.inject.Inject

internal class BuildKeyRegOnlineTransactionImpl @Inject constructor() : BuildKeyRegOnlineTransaction {

    override fun invoke(
        params: OnlineKeyRegTransactionPayload
    ): ByteArray? {
        return try {
            createTransaction(params)
        } catch (e: Exception) {
            null
        }
    }

    private fun createTransaction(params: OnlineKeyRegTransactionPayload): ByteArray {
        return with(params) {
            val suggestedParams = params.txnParams.toSuggestedParams()

            if (flatFee != null) {
                suggestedParams.fee = flatFee.toLong()
                suggestedParams.flatFee = true
            }

            Sdk.makeKeyRegTxnWithStateProofKey(
                senderAddress,
                note?.toByteArray(),
                suggestedParams,
                voteKey.standardizeBase64(),
                selectionPublicKey.standardizeBase64(),
                stateProofKey.standardizeBase64(),
                voteFirstRound.toBigIntegerOrZero().toUint64(),
                voteLastRound.toBigIntegerOrZero().toUint64(),
                voteKeyDilution.toBigIntegerOrZero().toUint64(),
                false
            )
        }
    }
}
