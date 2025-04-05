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
import cash.z.ecc.android.bip39.toSeed
import com.algorand.algosdk.crypto.Address
import com.algorand.wallet.algosdk.domain.model.HdKeyAccount
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import foundation.algorand.xhdwalletapi.Bip32DerivationType
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.fromSeed
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.getBIP44PathFromContext
import javax.inject.Inject

internal class PeraBip39SdkImpl @Inject constructor(
    private val peraExceptionLogger: PeraExceptionLogger
) : PeraBip39Sdk {
    override fun getSeedFromEntropy(entropy: ByteArray): ByteArray? {
        return try {
            Mnemonics.MnemonicCode(entropy).toSeed()
        } catch (e: Exception) {
            null
        }
    }

    override fun getEntropyFromMnemonic(mnemonic: String): ByteArray? {
        return try {
            Mnemonics.MnemonicCode(mnemonic).toEntropy()
        } catch (e: Exception) {
            null
        }
    }

    override fun getMnemonicFromEntropy(entropy: ByteArray): String? {
        return try {
            val mnemonic = Mnemonics.MnemonicCode(entropy).words.joinToString(" ") { charArray ->
                String(charArray)
            }
            mnemonic
        } catch (e: Exception) {
            null
        }
    }

    override fun createHdKeyAccount(): HdKeyAccount? {
        var mnemonic = Mnemonics.MnemonicCode(Mnemonics.WordCount.COUNT_24)
            .words.joinToString(" ") { charArray ->
                String(charArray)
            }
        val mnemonicCode = Mnemonics.MnemonicCode(mnemonic)
        var entropy = mnemonicCode.toEntropy()
        val output = getHdKeyAccount(entropy.copyOf(), 0, 0, 0)
        entropy.clearFromMemory()
        return output
    }

    override fun getHdKeyAccount(
        entropy: ByteArray,
        accountIndex: Int,
        changeIndex: Int,
        keyIndex: Int
    ): HdKeyAccount? {
        return try {
            val mnemonicCode = Mnemonics.MnemonicCode(entropy)
            var seed = mnemonicCode.toSeed()
            val xHDWalletAPI = XHDWalletAPIAndroid(seed.copyOf())
            val keyContext = KeyContext.Address
            val account = accountIndex.toUInt()
            val change = changeIndex.toUInt()
            val keyIndex = keyIndex.toUInt()

            val publicKey = xHDWalletAPI.keyGen(
                keyContext,
                account,
                change,
                keyIndex
            )

            // Produce the PK and turn it into an Algorand formatted address
            val algoAddress = Address(publicKey)
            var privateKey: ByteArray = xHDWalletAPI.deriveKey(
                fromSeed(seed.copyOf()),
                getBIP44PathFromContext(keyContext, account, change, keyIndex),
                true
            )

            val output = HdKeyAccount(
                address = algoAddress.toString(),
                publicKey = publicKey,
                privateKey = privateKey.copyOf(),
                entropy = entropy,
                account = account.toInt(),
                change = change.toInt(),
                keyIndex = keyIndex.toInt(),
                derivationType = Bip32DerivationType.Peikert.value
            )

            privateKey.clearFromMemory()
            seed.clearFromMemory()
            return output
        } catch (e: Exception) {
            peraExceptionLogger.logException(e)
            null
        }
    }

    override fun generateHdKeyAddress(entropy: ByteArray, accountIndex: Int, changeIndex: Int, keyIndex: Int): String {
        val mnemonicCode = Mnemonics.MnemonicCode(entropy)
        val seed = mnemonicCode.toSeed()
        val xHDWalletAPI = XHDWalletAPIAndroid(seed)
        val key = xHDWalletAPI.keyGen(
            context = KeyContext.Address,
            account = accountIndex.toUInt(),
            change = changeIndex.toUInt(),
            keyIndex = keyIndex.toUInt(),
            derivationType = Bip32DerivationType.Peikert
        )
        seed.clearFromMemory()
        return Address(key).toString()
    }
}
