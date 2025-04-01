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

import com.algorand.algosdk.sdk.Sdk
import com.algorand.wallet.algosdk.domain.model.Algo25Account
import com.algorand.wallet.algosdk.domain.model.HdKeyAccount
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import javax.inject.Inject

internal class AlgoAccountSdkImpl @Inject constructor(
    private val bip39Sdk: PeraBip39Sdk
) : AlgoAccountSdk {

    override fun createHdAccount(): HdKeyAccount? {
        return try {
            bip39Sdk.createHdKeyAccount()
        } catch (e: Exception) {
            null
        }
    }

    override fun recoverHdAccount(mnemonic: String): HdKeyAccount? {
        return try {
            bip39Sdk.getHdKeyAccountFromMnemonic(mnemonic)
        } catch (e: Exception) {
            null
        }
    }

    override fun createAlgo25Account(): Algo25Account? {
        return try {
            var secretKey = Sdk.generateSK()
            val output = Algo25Account(
                address = Sdk.generateAddressFromSK(secretKey),
                secretKey = secretKey.copyOf()
            )
            secretKey.clearFromMemory()
            output
        } catch (e: Exception) {
            null
        }
    }

    override fun recoverAlgo25Account(mnemonic: String): Algo25Account? {
        return try {
            var secretKey = Sdk.mnemonicToPrivateKey(mnemonic)

            val output = Algo25Account(
                address = Sdk.generateAddressFromSK(secretKey),
                secretKey = secretKey.copyOf()
            )
            secretKey.clearFromMemory()
            output
        } catch (e: Exception) {
            null
        }
    }

    override fun getMnemonicFromSecretKey(secretKey: ByteArray): String? {
        return try {
            Sdk.mnemonicFromPrivateKey(secretKey)
        } catch (exception: Exception) {
            null
        }
    }
}
