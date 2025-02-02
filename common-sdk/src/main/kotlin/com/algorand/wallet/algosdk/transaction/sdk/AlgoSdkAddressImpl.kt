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

package com.algorand.wallet.algosdk.transaction.sdk

import android.util.Base64
import com.algorand.algosdk.sdk.Sdk
import com.algorand.wallet.algosdk.transaction.model.AlgorandAddress
import com.algorand.wallet.encryption.Base64Manager
import javax.inject.Inject

internal class AlgoSdkAddressImpl @Inject constructor(
    private val base64Manager: Base64Manager
) : AlgoSdkAddress {

    override fun isValid(address: String): Boolean {
        return try {
            Sdk.isValidAddress(address)
        } catch (e: Exception) {
            false
        }
    }

    override fun generateAddressFromPublicKey(publicKey: ByteArray): AlgorandAddress? {
        return try {
            val address = Sdk.generateAddressFromPublicKey(publicKey)
            AlgorandAddress(address, publicKey)
        } catch (exception: Exception) {
            null
        }
    }

    override fun generateAddressFromPublicKey(addressBase64: String): AlgorandAddress? {
        val publicKey = try {
            base64Manager.decode(addressBase64, Base64.DEFAULT)
        } catch (exception: Exception) {
            return null
        }
        return generateAddressFromPublicKey(publicKey)
    }
}
