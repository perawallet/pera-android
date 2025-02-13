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

import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import java.math.BigInteger

interface AlgoSdk {

    fun createAssetTransferTxn(
        senderAddress: String,
        receiverAddress: String,
        amount: BigInteger,
        assetId: Long,
        noteInByteArray: ByteArray?,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray

    fun createAlgoTransferTxn(
        senderAddress: String,
        receiverAddress: String,
        amount: BigInteger,
        isMax: Boolean,
        noteInByteArray: ByteArray?,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray

    fun createRekeyTxn(
        rekeyAddress: String,
        rekeyAdminAddress: String,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray

    fun createAddAssetTxn(
        address: String,
        assetId: Long,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray

    fun createRemoveAssetTxn(
        senderAddress: String,
        creatorPublicKey: String,
        assetId: Long,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray

    fun createSendAndRemoveAssetTxn(
        senderAddress: String,
        receiverAddress: String,
        assetId: Long,
        amount: BigInteger,
        noteInByteArray: ByteArray?,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray

    fun transactionMsgpackToJson(txnByteArray: ByteArray): String
}
