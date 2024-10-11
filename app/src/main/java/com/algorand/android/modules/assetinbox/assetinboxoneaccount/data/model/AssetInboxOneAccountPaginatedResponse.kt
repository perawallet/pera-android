/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model

import com.algorand.android.models.VerificationTierResponse
import com.google.gson.annotations.SerializedName
import java.math.BigInteger

data class AssetInboxOneAccountPaginatedResponse(
    @SerializedName("next")
    val next: String?,
    @SerializedName("previous")
    val previous: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("inbox_address")
    val inboxAddress: String?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("results")
    val results: List<AssetInboxOneAccountResultResponse>?
) {

    data class AssetInboxOneAccountResultResponse(
        @SerializedName("total_amount")
        val totalAmount: Int?,
        @SerializedName("asset")
        val asset: AssetResponse?,
        @SerializedName("algo_gain_on_claim")
        val algoGainOnClaim: BigInteger?,
        @SerializedName("algo_gain_on_reject")
        val algoGainOnReject: BigInteger?,
        @SerializedName("senders")
        val senders: SendersResponse?,
        @SerializedName("insufficient_algo_for_claiming")
        val insufficientAlgoForClaiming: Boolean?,
        @SerializedName("should_use_funds_before_claiming")
        val shouldUseFundsBeforeClaiming: Boolean?,
        @SerializedName("insufficient_algo_for_rejecting")
        val insufficientAlgoForRejecting: Boolean?,
        @SerializedName("should_use_funds_before_rejecting")
        val shouldUseFundsBeforeRejecting: Boolean?
    )

    data class AssetResponse(
        @SerializedName("asset_id")
        val assetId: Long?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("logo")
        val logo: String?,
        @SerializedName("unit_name")
        val unitName: String?,
        @SerializedName("fraction_decimals")
        val fractionDecimals: Int?,
        @SerializedName("total")
        val total: String?,
        @SerializedName("usd_value")
        val usdValue: String?,
        @SerializedName("is_verified")
        val isVerified: Boolean?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        @SerializedName("verification_tier")
        val verificationTier: VerificationTierResponse?,
        @SerializedName("explorer_url")
        val explorerUrl: String?,
        @SerializedName("collectible")
        val collectible: CollectibleResponse?,
        @SerializedName("creator")
        val creator: CreatorResponse?,
        @SerializedName("type")
        val type: String?
    )

    data class SendersResponse(
        @SerializedName("count")
        val count: Int?,
        @SerializedName("results")
        val results: List<SenderResultResponse>?
    )

    data class SenderResultResponse(
        @SerializedName("sender")
        val sender: SenderResponse?,
        @SerializedName("amount")
        val amount: BigInteger?
    )

    data class SenderResponse(
        @SerializedName("address")
        val address: String?,
        @SerializedName("name")
        val name: String?
    )

    data class CollectibleResponse(
        @SerializedName("title")
        val title: String?,
        @SerializedName("standard")
        val standard: String?,
        @SerializedName("primary_image")
        val primaryImage: String?,
        @SerializedName("media_type")
        val mediaType: String?,
        @SerializedName("explorer_url")
        val explorerUrl: String?,
        @SerializedName("collection")
        val collection: CollectionResponse?
    )

    data class CollectionResponse(
        @SerializedName("id")
        val collectionId: Long?,
        @SerializedName("name")
        val collectionName: String?,
        @SerializedName("description")
        val collectionDescription: String?
    )

    data class CreatorResponse(
        @SerializedName("id")
        val id: BigInteger?,
        @SerializedName("address")
        val address: String?,
        @SerializedName("is_verified_asset_creator")
        val isVerifiedAssetCreator: Boolean?
    )
}
