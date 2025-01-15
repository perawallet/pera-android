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

package com.algorand.common.algosdk

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.Mnemonics.MnemonicCode
import cash.z.ecc.android.bip39.toSeed
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.Address
import com.algorand.common.algosdk.model.Algo25Account
import com.algorand.common.algosdk.model.HdAccount
import com.algorand.common.encryption.EntropyEncryptionManager
import com.algorand.common.encryption.SecretKeyEncryptionManager
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.fromSeed
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.getBIP44PathFromContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security


actual interface AlgoAccountSdk {
    actual fun createHdAccount(): HdAccount
    actual fun recoverHdAccount(mnemonic: String): HdAccount?
    actual fun createAlgo25Account(): Algo25Account
    actual fun recoverAlgo25Account(mnemonic: String): Algo25Account?
}

internal class AlgoAccountSdkImpl(
    private val entropyEncryptionManager: EntropyEncryptionManager,
    private val secretKeyEncryptionManager: SecretKeyEncryptionManager
) : AlgoAccountSdk {
    init {
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 0)
    }

    override fun createHdAccount(): HdAccount {
        val generatedMnemonic = MnemonicCode(Mnemonics.WordCount.COUNT_24)
        return getHdAccount(generatedMnemonic)
    }

    override fun recoverHdAccount(mnemonic: String): HdAccount {
        val m = MnemonicCode(mnemonic)
        return getHdAccount(m)
    }

    override fun createAlgo25Account(): Algo25Account {
        val account = Account()
        return Algo25Account(account.address.toString(), account.toMnemonic(), account.toSeed())
    }

    override fun recoverAlgo25Account(mnemonic: String): Algo25Account? {
        try {
            val account = Account(mnemonic)
            return Algo25Account(account.address.toString(), account.toMnemonic(), account.toSeed())
        } catch (e: Exception) {
            return null
        }
    }

    private fun getHdAccount(mnemonic: MnemonicCode): HdAccount {
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
        val algoAddress =
            Address(
                publicKey
            )
        val privateKey: ByteArray =
            xHDWalletAPI.deriveKey(
                fromSeed(seed),
                getBIP44PathFromContext(keyContext, account, change, keyIndex),
                true
            )
        return HdAccount(
            address = algoAddress.toString(),
            encryptedMnemonicEntropy = entropyEncryptionManager.encrypt(mnemonic.toString()),
            publicKey = publicKey,
            encryptedPrivateKey = secretKeyEncryptionManager.encrypt(privateKey),
            account = account.toInt(),
            change = change.toInt(),
            keyIndex = keyIndex.toInt(),
            derivationType = Bip32DerivationType.Peikert
        )
    }
}
