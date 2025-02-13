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
import com.algorand.android.modules.algosdk.domain.model.OfflineKeyRegTransactionPayload
import com.algorand.android.utils.toSuggestedParams
import com.algorand.android.utils.toUint64
import java.math.BigInteger
import javax.inject.Inject

internal class BuildKeyRegOfflineTransactionImpl @Inject constructor() : BuildKeyRegOfflineTransaction {

    override fun invoke(payload: OfflineKeyRegTransactionPayload): ByteArray {
        return try {
            createTransaction(payload)
        } catch (e: Exception) {
            ByteArray(0)
        }
    }

    private fun createTransaction(payload: OfflineKeyRegTransactionPayload): ByteArray {
        return with(payload) {
            val suggestedParams = txnParams.toSuggestedParams()
            if (flatFee != null) {
                suggestedParams.fee = flatFee.toLong()
                suggestedParams.flatFee = true
            }

            val defaultVoteValue = BigInteger.ZERO.toUint64()

            Sdk.makeKeyRegTxnWithStateProofKey(
                senderAddress,
                note?.toByteArray(),
                suggestedParams,
                null,
                null,
                null,
                defaultVoteValue,
                defaultVoteValue,
                defaultVoteValue,
                false
            )
        }
    }
}
