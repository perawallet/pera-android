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
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.sdk.Sdk
import com.algorand.wallet.account.core.domain.usecase.AddHdSeedUseCase
import com.algorand.wallet.algosdk.model.Algo25Account
import com.algorand.wallet.algosdk.model.Bip32DerivationType
import com.algorand.wallet.algosdk.model.HdAccount
import com.algorand.wallet.encryption.SecretKeyEncryptionManager
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.fromSeed
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.getBIP44PathFromContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class AlgoAccountSdkImpl @Inject constructor(
    private val secretKeyEncryptionManager: SecretKeyEncryptionManager,
    private val addHdSeedUseCase: AddHdSeedUseCase
) : AlgoAccountSdk {

    override fun createHdAccount(): HdAccount? {
        val entropy = Mnemonics.WordCount.COUNT_24.toEntropy()
        val m = Mnemonics.MnemonicCode(entropy)

        var seedId = 0

        runBlocking(Dispatchers.IO) {
            addHdSeedUseCase.invoke(m).collect { collectedSeedId ->
                seedId = collectedSeedId
            }
        }

        if (seedId > 0)
            return getHdAccount(m, seedId)
        else
            return null
    }

    override fun recoverHdAccount(mnemonic: String): HdAccount {
        val m = Mnemonics.MnemonicCode(mnemonic)
        return getHdAccount(m, 0)
    }

    override fun createAlgo25Account(): Algo25Account? {
        val secretKey = Sdk.generateSK()
        try {
            return Algo25Account(
                Sdk.generateAddressFromSK(secretKey),
                Sdk.mnemonicFromPrivateKey(secretKey),
                secretKey
            )
        } finally {
            secretKey.fill(0) // Overwrite secret key with zeros
        }
    }

    override fun recoverAlgo25Account(mnemonic: String): Algo25Account? {
        try {
            val account = Account(mnemonic)
            return Algo25Account(
                account.address.toString(),
                account.toMnemonic(),
                account.toSeed()
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun getHdAccount(mnemonic: Mnemonics.MnemonicCode, seedId: Int): HdAccount {
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
        val privateKey: ByteArray = xHDWalletAPI.deriveKey(
            fromSeed(seed),
            getBIP44PathFromContext(keyContext, account, change, keyIndex),
            true
        )
        return HdAccount(
            address = algoAddress.toString(),
            encryptedEntropy = secretKeyEncryptionManager.encrypt(mnemonic.toEntropy()),
            publicKey = publicKey,
            encryptedPrivateKey = secretKeyEncryptionManager.encrypt(privateKey),
            seedId = seedId,
            account = account.toInt(),
            change = change.toInt(),
            keyIndex = keyIndex.toInt(),
            derivationType = Bip32DerivationType.Peikert.value
        )
    }
}
