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

package com.algorand.wallet.asset.data.model

import com.algorand.wallet.asset.data.model.collectible.CollectibleResponse
import com.google.gson.annotations.SerializedName

internal data class AssetResponse(
    @SerializedName("asset_id") val assetId: Long? = null,
    @SerializedName("name") val fullName: String? = null,
    @SerializedName("logo") val logoUri: String? = null,
    @SerializedName("unit_name") val shortName: String? = null,
    @SerializedName("fraction_decimals") val fractionDecimals: Int? = null,
    @SerializedName("usd_value") val usdValue: String? = null,
    @SerializedName("creator") val assetCreator: AssetCreatorResponse? = null,
    @SerializedName("collectible") val collectible: CollectibleResponse? = null,
    @SerializedName("total") val maxSupply: String? = null,
    @SerializedName("explorer_url") val explorerUrl: String? = null,
    @SerializedName("verification_tier") val verificationTier: VerificationTierResponse? = null,
    @SerializedName("project_url") val projectUrl: String? = null,
    @SerializedName("project_name") val projectName: String? = null,
    @SerializedName("logo_svg") val logoSvgUri: String? = null,
    @SerializedName("discord_url") val discordUrl: String? = null,
    @SerializedName("telegram_url") val telegramUrl: String? = null,
    @SerializedName("twitter_username") val twitterUsername: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("total_supply") val totalSupply: String? = null,
    @SerializedName("last_24_hours_algo_price_change_percentage") val last24HoursAlgoPriceChangePercentage: String? = null,
    @SerializedName("available_on_discover_mobile") val isAvailableOnDiscoverMobile: Boolean? = null
)
