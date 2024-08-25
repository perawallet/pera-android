/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.algosdk.component.transaction.mapper

import com.algorand.android.algosdk.component.AlgoSdkAddress
import com.algorand.android.algosdk.component.transaction.model.ApplicationCallStateSchema
import com.algorand.android.algosdk.component.transaction.model.AssetConfigParameters
import com.algorand.android.algosdk.component.transaction.model.RawTransaction
import com.algorand.android.algosdk.component.transaction.model.RawTransactionType
import com.algorand.android.algosdk.component.transaction.model.payload.RawTransactionApplicationCallStateSchemaPayload
import com.algorand.android.algosdk.component.transaction.model.payload.RawTransactionAssetConfigParametersPayload
import com.algorand.android.algosdk.component.transaction.model.payload.RawTransactionPayload
import com.algorand.android.algosdk.component.transaction.model.payload.RawTransactionTypePayload
import javax.inject.Inject

internal class RawTransactionMapperImpl @Inject constructor(
    private val algoSdkAddress: AlgoSdkAddress
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
            transactionType = getRawTxnType(payload.transactionType),
            closeToAddress = payload.closeToAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            rekeyAddress = payload.rekeyAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            assetCloseToAddress = payload.assetCloseToAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            assetReceiverAddress = payload.assetReceiverAddress?.let { algoSdkAddress.generateAddressFromPublicKey(it) },
            assetAmount = payload.assetAmount,
            assetId = payload.assetId,
            appArgs = payload.appArgs,
            appOnComplete = payload.appOnComplete,
            appId = payload.appId,
            appGlobalSchema = getAppCallStateSchema(payload.appGlobalSchema),
            appLocalSchema = getAppCallStateSchema(payload.appLocalSchema),
            appExtraPages = payload.appExtraPages,
            approvalHash = payload.approvalHash,
            stateHash = payload.stateHash,
            assetIdBeingConfigured = payload.assetIdBeingConfigured,
            assetConfigParameters = getAssetConfigParameters(payload.decodedAssetConfigParameters),
            groupId = payload.groupId
        )
    }

    private fun getRawTxnType(rawTransactionTypePayload: RawTransactionTypePayload): RawTransactionType {
        return when (rawTransactionTypePayload) {
            RawTransactionTypePayload.PAY_TRANSACTION -> RawTransactionType.PAY_TRANSACTION
            RawTransactionTypePayload.ASSET_TRANSACTION -> RawTransactionType.ASSET_TRANSACTION
            RawTransactionTypePayload.APP_TRANSACTION -> RawTransactionType.APP_TRANSACTION
            RawTransactionTypePayload.ASSET_CONFIGURATION -> RawTransactionType.ASSET_CONFIGURATION
            RawTransactionTypePayload.UNDEFINED -> RawTransactionType.UNDEFINED
        }
    }

    private fun getAppCallStateSchema(
        payload: RawTransactionApplicationCallStateSchemaPayload?
    ): ApplicationCallStateSchema {
        return ApplicationCallStateSchema(
            numberOfBytes = payload?.numberOfBytes,
            numberOfInts = payload?.numberOfInts
        )
    }

    private fun getAssetConfigParameters(
        payload: RawTransactionAssetConfigParametersPayload?
    ): AssetConfigParameters {
        return AssetConfigParameters(
            totalSupply = payload?.totalSupply,
            decimal = payload?.decimal,
            isFrozen = payload?.isFrozen,
            unitName = payload?.unitName,
            name = payload?.name,
            url = payload?.url,
            metadataHash = payload?.metadataHash,
            managerAddress = payload?.managerAddress,
            reserveAddress = payload?.reserveAddress,
            frozenAddress = payload?.frozenAddress,
            clawbackAddress = payload?.clawbackAddress
        )
    }
}
