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

package com.algorand.wallet.algosdk.transaction.mapper

import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddress
import javax.inject.Inject

internal class RawTransactionMapperImpl @Inject constructor(
    private val algoSdkAddress: AlgoSdkAddress,
    private val rawTransactionTypeMapper: RawTransactionTypeMapper,
    private val assetConfigParametersMapper: AssetConfigParametersMapper,
    private val applicationCallStateSchemaMapper: ApplicationCallStateSchemaMapper
) : RawTransactionMapper {

    override fun invoke(payload: RawTransactionPayload): RawTransaction {
        return RawTransaction(
            amount = payload.amount,
            fee = payload.fee,
            firstValidRound = payload.firstValidRound,
            genesisId = payload.genesisId,
            genesisHash = payload.genesisHash,
            lastValidRound = payload.lastValidRound,
            note = payload.note,
            receiverAddress = payload.receiverAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            senderAddress = payload.senderAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            transactionType = rawTransactionTypeMapper(payload.transactionType),
            closeToAddress = payload.closeToAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            rekeyAddress = payload.rekeyAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            assetCloseToAddress = payload.assetCloseToAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            assetReceiverAddress = payload.assetReceiverAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            assetAmount = payload.assetAmount,
            assetId = payload.assetId,
            appArgs = payload.appArgs,
            appOnComplete = payload.appOnComplete,
            appId = payload.appId,
            appGlobalSchema = applicationCallStateSchemaMapper(payload.appGlobalSchema),
            appLocalSchema = applicationCallStateSchemaMapper(payload.appLocalSchema),
            appExtraPages = payload.appExtraPages,
            approvalHash = payload.approvalHash,
            stateHash = payload.stateHash,
            assetIdBeingConfigured = payload.assetIdBeingConfigured,
            assetConfigParameters = assetConfigParametersMapper(payload.decodedAssetConfigParameters),
            groupId = payload.groupId
        )
    }
}
