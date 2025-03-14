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

import android.util.Log
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import com.algorand.algosdk.crypto.Address
import com.algorand.wallet.account.info.domain.usecase.GetAccountFastLookup
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddressUseCase
import com.algorand.wallet.algosdk.domain.model.HdKeyAccount
import com.algorand.wallet.algosdk.model.RegisteredAlgorandAccount
import foundation.algorand.xhdwalletapi.Bip32DerivationType
import foundation.algorand.xhdwalletapi.KeyContext
import foundation.algorand.xhdwalletapi.XHDWalletAPIAndroid
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.fromSeed
import foundation.algorand.xhdwalletapi.XHDWalletAPIBase.Companion.getBIP44PathFromContext
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class PeraBip39SdkImpl @Inject constructor(
    private val getAccountFastLookup: GetAccountFastLookup,
    private val isThereAnyAccountWithAddressUseCase: IsThereAnyAccountWithAddressUseCase
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
        val output = getHdKeyAccount(entropy,0,0, 0)
        entropy = ByteArray(0) // delete secret from memory
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
            val xHDWalletAPI = XHDWalletAPIAndroid(seed)
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
                fromSeed(seed),
                getBIP44PathFromContext(keyContext, account, change, keyIndex),
                true
            )

            val output = HdKeyAccount(
                address = algoAddress.toString(),
                publicKey = publicKey,
                privateKey = privateKey,
                entropy = entropy,
                account = account.toInt(),
                change = change.toInt(),
                keyIndex = keyIndex.toInt(),
                derivationType = Bip32DerivationType.Peikert.value
            )

            privateKey = ByteArray(0) // delete secret from memory
            seed = ByteArray(0) // delete secret from memory
            return output
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchRegisteredAccounts(entropy: ByteArray): List<RegisteredAlgorandAccount> {
        val output = mutableListOf<RegisteredAlgorandAccount>()
        try {
            val mnemonicCode = Mnemonics.MnemonicCode(entropy)
            var seed = mnemonicCode.toSeed()
            val xHDWalletAPI = XHDWalletAPIAndroid(seed)

            for (accountIndex in 0 until 5) {
                for (changeIndex in 0 until 5) {
                    for (keyIndex in 0 until 5) {
                        val algoAddress = Address(
                            xHDWalletAPI.keyGen(
                                context = KeyContext.Address,
                                account = accountIndex.toUInt(),
                                change = changeIndex.toUInt(),
                                keyIndex = keyIndex.toUInt(),
                                derivationType = Bip32DerivationType.Peikert
                            )
                        ).toString()

                        val fastLookupAccountResponse = getAccountFastLookup(algoAddress)
                        if (fastLookupAccountResponse.isSuccess) {
                            val fastLookupAccount = fastLookupAccountResponse.getDataOrNull()

                            val tempAccount = RegisteredAlgorandAccount(
                                address = algoAddress,
                                algoValue = fastLookupAccount?.algoValue ?: BigDecimal.ZERO,
                                usdValue = fastLookupAccount?.algoValue ?: BigDecimal.ZERO,
                                calculationType = fastLookupAccount?.calculationType ?: "exact",
                                accountExists = fastLookupAccount?.accountExists ?: false,
                                account = accountIndex,
                                change = changeIndex,
                                keyIndex = keyIndex,
                                isImportedToDB = isThereAnyAccountWithAddressUseCase(algoAddress),
                                derivationType = Bip32DerivationType.Peikert.value
                            )

                            Log.i(
                                TAG,
                                "$algoAddress | Accounts Exists: ${fastLookupAccount?.accountExists}"
                            )

                            if (fastLookupAccount?.accountExists == true) {
                                output.add(tempAccount)
                            }
                        }
                    }
                }
            }

            seed = ByteArray(0) // delete secret from memory
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        return output
    }

    companion object {
        private const val TAG = "Bip39SDK"
    }
}
