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
import com.algorand.common.algosdk.model.Bip39Account
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import kotlin.random.Random


actual interface AlgoAccountSdk {
    actual fun createBip39Account(): Bip39Account
    actual fun recoverBip39Account(mnemonic: String): Bip39Account?
    actual fun createAlgo25Account(): Algo25Account
    actual fun recoverAlgo25Account(mnemonic: String): Algo25Account?
}

internal class AlgoAccountSdkImpl : AlgoAccountSdk {
    init {
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 0)
    }

    override fun createBip39Account(): Bip39Account {
        val generatedMnemonic = MnemonicCode(Mnemonics.WordCount.COUNT_24)
        val wordsAsString = generatedMnemonic.joinToString(" ")
        val accountAddress = generateBip39Address(generatedMnemonic)
        return Bip39Account(accountAddress, wordsAsString, generatedMnemonic.toSeed())
    }

    override fun recoverBip39Account(mnemonic: String): Bip39Account {
        val m = MnemonicCode(mnemonic)
        val accountAddress = generateBip39Address(m)
        return Bip39Account(accountAddress, mnemonic, m.toSeed())
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

    private fun generateBip39Address(mnemonic: MnemonicCode): String {
        val xHDWalletAPI = XHDWalletAPIAndroid(mnemonic.toSeed())
        // Produce the PK and turn it into an Algorand formatted address
        val algoAddress =
            Address(
                xHDWalletAPI.keyGen(
                    KeyContext.Address,
                    Random.nextInt().toUInt(),
                    Random.nextInt().toUInt(),
                    Random.nextInt().toUInt()
                )
            )
        return algoAddress.toString()
    }
}
