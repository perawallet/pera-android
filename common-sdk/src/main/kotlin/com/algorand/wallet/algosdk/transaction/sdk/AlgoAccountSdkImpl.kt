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

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toEntropy
import cash.z.ecc.android.bip39.toSeed
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.sdk.Sdk
import com.algorand.wallet.algosdk.model.Algo25Account
import com.algorand.wallet.algosdk.model.Bip32DerivationType
import com.algorand.wallet.algosdk.model.HdKeyAccount
import com.algorand.wallet.encryption.domain.services.AESPlatformManager
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.fromSeed
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.getBIP44PathFromContext
import javax.inject.Inject

internal class AlgoAccountSdkImpl @Inject constructor(
    private val aesPlatformManager: AESPlatformManager
) : AlgoAccountSdk {

    override fun createHdAccount(): HdKeyAccount {
        val entropy = Mnemonics.WordCount.COUNT_24.toEntropy()
        val m = Mnemonics.MnemonicCode(entropy)
        return getHdKeyAccount(m)
    }

    override fun recoverHdAccount(mnemonic: String): HdKeyAccount {
        val m = Mnemonics.MnemonicCode(mnemonic)
        return getHdKeyAccount(m)
    }

    override fun createAlgo25Account(): Algo25Account {
        var secretKey = Sdk.generateSK()
        val output = Algo25Account(
            address = Sdk.generateAddressFromSK(secretKey),
            encryptedSecretKey = aesPlatformManager.encryptByteArray(secretKey)
        )
        secretKey = ByteArray(0) // delete secret key from memory
        return output
    }

    override fun recoverAlgo25Account(mnemonic: String): Algo25Account {
        var secretKey = Sdk.mnemonicToPrivateKey(mnemonic)

        val output = Algo25Account(
            address = Sdk.generateAddressFromSK(secretKey),
            encryptedSecretKey = aesPlatformManager.encryptByteArray(secretKey)
        )
        secretKey = ByteArray(0) // delete secret key from memory
        return output
    }

    private fun getHdKeyAccount(mnemonic: Mnemonics.MnemonicCode): HdKeyAccount {
        var entropy = mnemonic.toEntropy()
        val seed = mnemonic.toSeed()
        val xHDWalletAPI = XHDWalletAPIAndroid(seed)
        val keyContext = KeyContext.Address
        val account = 0.toUInt()
        val change = 0.toUInt()
        val keyIndex = 0.toUInt()

        val publicKey = xHDWalletAPI.keyGen(
            keyContext,
            account,
            change,
            keyIndex
        )

        // Produce the PK and turn it into an Algorand formatted address
        val algoAddress = Address(publicKey)
        var privateKey: ByteArray = xHDWalletAPI.deriveKey(
            fromSeed(seed),
            getBIP44PathFromContext(keyContext, account, change, keyIndex),
            true
        )

        val output = HdKeyAccount(
            address = algoAddress.toString(),
            publicKey = publicKey,
            encryptedPrivateKey = aesPlatformManager.encryptByteArray(privateKey),
            encryptedEntropy = aesPlatformManager.encryptByteArray(entropy),
            account = account.toInt(),
            change = change.toInt(),
            keyIndex = keyIndex.toInt(),
            derivationType = Bip32DerivationType.Peikert.value
        )
        privateKey = ByteArray(0) // delete secret key from memory
        entropy = ByteArray(0) // delete secret key from memory
        return output
    }
}
