/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.core.transaction

import app.perawallet.gomobilesdk.sdk.Sdk
import com.algorand.android.utils.signTx
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import javax.inject.Inject

class LocalAccountSigningHelper @Inject constructor(
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val signHdKeyTransaction: SignHdKeyTransaction
) {

    suspend fun signWithAlgo25Account(transactionData: ByteArray, senderAddress: String): ByteArray? {
        val secretKey = getAlgo25SecretKey(senderAddress) ?: return null
        return try {
            runCatching { transactionData.signTx(secretKey) }.getOrNull()
        } finally {
            secretKey.clearFromMemory()
        }
    }

    suspend fun signWithAlgo25AccountReturnSignature(
        transactionData: ByteArray,
        senderAddress: String
    ): ByteArray? {
        val secretKey = getAlgo25SecretKey(senderAddress) ?: return null
        return try {
            runCatching { Sdk.signTransactionReturnSignature(secretKey, transactionData) }.getOrNull()
        } finally {
            secretKey.clearFromMemory()
        }
    }

    suspend fun signWithHdKeyAccount(transactionData: ByteArray, hdKey: LocalAccount.HdKey): ByteArray? {
        val seed = getHdSeed(seedId = hdKey.seedId) ?: return null
        return try {
            signHdKeyTransaction.signTransaction(
                transactionData,
                seed,
                hdKey.account,
                hdKey.change,
                hdKey.keyIndex
            )
        } finally {
            seed.clearFromMemory()
        }
    }

    suspend fun signWithHdKeyAccountReturnSignature(transactionData: ByteArray, hdKey: LocalAccount.HdKey): ByteArray? {
        val seed = getHdSeed(seedId = hdKey.seedId) ?: return null
        return try {
            signHdKeyTransaction.signTransactionReturnSignature(
                transactionData,
                seed,
                hdKey.account,
                hdKey.change,
                hdKey.keyIndex
            )
        } finally {
            seed.clearFromMemory()
        }
    }
}
