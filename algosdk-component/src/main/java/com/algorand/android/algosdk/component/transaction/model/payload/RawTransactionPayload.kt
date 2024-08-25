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

package com.algorand.android.algosdk.component.transaction.model.payload

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class RawTransactionPayload(
    @SerializedName("amt") val amount: String? = null,
    @SerializedName("fee") val fee: Long? = null,
    @SerializedName("fv") val firstValidRound: Long? = null,
    @SerializedName("gen") val genesisId: String? = null,
    @SerializedName("gh") val genesisHash: String? = null,
    @SerializedName("lv") val lastValidRound: Long? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("rcv") val receiverAddress: String? = null,
    @SerializedName("snd") val senderAddress: String? = null,
    @SerializedName("type") val transactionType: RawTransactionTypePayload,
    @SerializedName("close") val closeToAddress: String? = null,
    @SerializedName("rekey") val rekeyAddress: String? = null,
    @SerializedName("aclose") val assetCloseToAddress: String? = null,
    @SerializedName("arcv") val assetReceiverAddress: String? = null,
    @SerializedName("aamt") val assetAmount: BigInteger? = null,
    @SerializedName("xaid") val assetId: Long? = null,
    @SerializedName("apaa") val appArgs: List<String>? = null,
    @SerializedName("apan") val appOnComplete: Int? = null,
    @SerializedName("apid") val appId: Long? = null,
    @SerializedName("apgs") val appGlobalSchema: RawTransactionApplicationCallStateSchemaPayload? = null,
    @SerializedName("apls") val appLocalSchema: RawTransactionApplicationCallStateSchemaPayload? = null,
    @SerializedName("apep") val appExtraPages: Int? = null,
    @SerializedName("apap") val approvalHash: String? = null,
    @SerializedName("apsu") val stateHash: String? = null,
    @SerializedName("caid") val assetIdBeingConfigured: Long? = null,
    @SerializedName("apar") val decodedAssetConfigParameters: RawTransactionAssetConfigParametersPayload? = null,
    @SerializedName("grp") val groupId: String? = null
)
