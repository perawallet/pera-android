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
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkNumberExtensions.toUint64
import com.algorand.wallet.algosdk.transaction.sdk.mapper.SuggestedParamsMapper
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import java.math.BigInteger
import javax.inject.Inject

internal class AlgoSdkImpl @Inject constructor(private val suggestedParamsMapper: SuggestedParamsMapper) : AlgoSdk {

    override fun createAssetTransferTxn(
        senderAddress: String,
        receiverAddress: String,
        amount: BigInteger,
        assetId: Long,
        noteInByteArray: ByteArray?,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray {
        return Sdk.makeAssetTransferTxn(
            senderAddress,
            receiverAddress,
            "",
            amount.toUint64(),
            noteInByteArray,
            suggestedParamsMapper(suggestedTransactionParams, addGenesis = false),
            assetId
        )
    }

    override fun createAlgoTransferTxn(
        senderAddress: String,
        receiverAddress: String,
        amount: BigInteger,
        isMax: Boolean,
        noteInByteArray: ByteArray?,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray {
        return Sdk.makePaymentTxn(
            senderAddress,
            receiverAddress,
            amount.toUint64(),
            noteInByteArray,
            if (isMax) receiverAddress else "",
            suggestedParamsMapper(suggestedTransactionParams, addGenesis = true)
        )
    }

    override fun createRekeyTxn(
        rekeyAddress: String,
        rekeyAdminAddress: String,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray {
        return Sdk.makeRekeyTxn(
            rekeyAddress,
            rekeyAdminAddress,
            suggestedParamsMapper(suggestedTransactionParams, addGenesis = true)
        )
    }

    override fun createAddAssetTxn(
        address: String,
        assetId: Long,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray {
        return Sdk.makeAssetAcceptanceTxn(
            address,
            null,
            suggestedParamsMapper(suggestedTransactionParams, addGenesis = true),
            assetId
        )
    }

    override fun createRemoveAssetTxn(
        senderAddress: String,
        creatorPublicKey: String,
        assetId: Long,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray {
        return Sdk.makeAssetTransferTxn(
            senderAddress,
            creatorPublicKey,
            creatorPublicKey,
            0L.toUint64(),
            null,
            suggestedParamsMapper(suggestedTransactionParams, addGenesis = false),
            assetId
        )
    }

    override fun createSendAndRemoveAssetTxn(
        senderAddress: String,
        receiverAddress: String,
        assetId: Long,
        amount: BigInteger,
        noteInByteArray: ByteArray?,
        suggestedTransactionParams: SuggestedTransactionParams
    ): ByteArray {
        return Sdk.makeAssetTransferTxn(
            senderAddress,
            receiverAddress,
            receiverAddress,
            amount.toUint64(),
            noteInByteArray,
            suggestedParamsMapper(suggestedTransactionParams, addGenesis = false),
            assetId
        )
    }

    override fun transactionMsgpackToJson(txnByteArray: ByteArray): String {
        return Sdk.transactionMsgpackToJson(txnByteArray)
    }
}
