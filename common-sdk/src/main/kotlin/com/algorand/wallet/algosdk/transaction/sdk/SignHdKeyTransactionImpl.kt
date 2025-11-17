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

package com.algorand.wallet.algosdk.transaction.sdk

import app.perawallet.gomobilesdk.sdk.Sdk
import app.perawallet.xhdwalletapi.Bip32DerivationType
import app.perawallet.xhdwalletapi.KeyContext
import app.perawallet.xhdwalletapi.XHDWalletAPIAndroid
import app.perawallet.xhdwalletapi.XHDWalletAPIBase.Companion.getBIP44PathFromContext
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.transaction.Transaction
import com.algorand.algosdk.util.Encoder
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import java.nio.charset.StandardCharsets
import javax.inject.Inject

internal class SignHdKeyTransactionImpl @Inject constructor(
    private val peraExceptionLogger: PeraExceptionLogger
) : SignHdKeyTransaction {
    override fun signTransaction(
        transactionByteArray: ByteArray,
        seed: ByteArray,
        account: Int,
        change: Int,
        key: Int
    ): ByteArray? {
        return try {
            val tx = Encoder.decodeFromMsgPack(transactionByteArray, Transaction::class.java)

            val xHDWalletAPI = XHDWalletAPIAndroid(seed)
            val (accountIndex, changeIndex, keyIndex) = listOf(
                account.toUInt(),
                change.toUInt(),
                key.toUInt()
            )

            val signedTxn = xHDWalletAPI.signAlgoTransaction(
                KeyContext.Address,
                accountIndex,
                changeIndex,
                keyIndex,
                rawTransactionBytesToSign(transactionByteArray)
            )

            val pkAddress = Address(
                xHDWalletAPI.keyGen(
                    KeyContext.Address,
                    accountIndex,
                    changeIndex,
                    keyIndex
                )
            )

            return if (tx.sender != pkAddress) {
                Sdk.attachSignatureWithSigner(signedTxn, transactionByteArray, pkAddress.toString())
            } else {
                Sdk.attachSignature(signedTxn, transactionByteArray)
            }
        } catch (e: Exception) {
            peraExceptionLogger.logException(e)
            null
        }
    }

    private fun rawTransactionBytesToSign(tx: ByteArray): ByteArray {
        val txIdPrefix = "TX".toByteArray(StandardCharsets.UTF_8)
        return txIdPrefix + tx
    }

    /*
    * currently cards arbitrary signing uses a prefix MX that is not supported
    * in xHD library. SignData is basically rawSign with a validation step before
    * it, but since we can't validate the prefix MX...we're calling rawSign directly
    */
    override fun signLegacyArbitaryData(
        transactionByteArray: ByteArray,
        seed: ByteArray,
        account: Int,
        change: Int,
        key: Int
    ): ByteArray? {
        return try {
            val prefixedData = prefixData(transactionByteArray)

            val xHDWalletAPI = XHDWalletAPIAndroid(seed)
            val (accountIndex, changeIndex, keyIndex) = listOf(
                account.toUInt(),
                change.toUInt(),
                key.toUInt()
            )

            val stx = xHDWalletAPI.rawSign(
                getBIP44PathFromContext(
                    context = KeyContext.Address,
                    account = accountIndex,
                    change = changeIndex,
                    keyIndex = keyIndex
                ),
                prefixedData,
                Bip32DerivationType.Peikert
            )

            return stx
        } catch (e: Exception) {
            peraExceptionLogger.logException(e)
            null
        }
    }

    private fun prefixData(data: ByteArray): ByteArray {
        val prefix = "MX".toByteArray(Charsets.UTF_8)
        return prefix + data
    }
}
