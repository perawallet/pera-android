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

package com.algorand.wallet.algosdk.transaction

import com.algorand.algosdk.sdk.Sdk
import javax.inject.Inject

internal class AlgoTransactionSignerImpl @Inject constructor() : AlgoTransactionSigner {

    override fun signWithSecretKey(secretKey: ByteArray, transaction: ByteArray): ByteArray? {
        return try {
            Sdk.signTransaction(secretKey, transaction)
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    override fun attachSignature(signature: ByteArray, transaction: ByteArray?): ByteArray? {
        return try {
            Sdk.attachSignature(signature, transaction)
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    override fun attachSignatureWithSigner(
        signature: ByteArray,
        transaction: ByteArray?,
        address: String?
    ): ByteArray? {
        return try {
            Sdk.attachSignatureWithSigner(signature, transaction, address)
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }
}
