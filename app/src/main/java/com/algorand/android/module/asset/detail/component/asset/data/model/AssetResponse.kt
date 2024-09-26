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

package com.algorand.android.module.asset.detail.component.asset.data.model

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleResponse
import com.google.gson.annotations.SerializedName
import java.math.*

internal data class AssetResponse(
    @SerializedName("asset_id") val assetId: Long?,
    @SerializedName("name") val fullName: String?,
    @SerializedName("logo") val logoUri: String?,
    @SerializedName("unit_name") val shortName: String?,
    @SerializedName("fraction_decimals") val fractionDecimals: Int?,
    @SerializedName("usd_value") val usdValue: BigDecimal?,
    @SerializedName("creator") val assetCreator: AssetCreatorResponse?,
    @SerializedName("collectible") val collectible: CollectibleResponse?,
    @SerializedName("total") val maxSupply: BigInteger?,
    @SerializedName("explorer_url") val explorerUrl: String?,
    @SerializedName("verification_tier") val verificationTier: VerificationTierResponse?,
    @SerializedName("project_url") val projectUrl: String?,
    @SerializedName("project_name") val projectName: String?,
    @SerializedName("logo_svg") val logoSvgUri: String?,
    @SerializedName("discord_url") val discordUrl: String?,
    @SerializedName("telegram_url") val telegramUrl: String?,
    @SerializedName("twitter_username") val twitterUsername: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("total_supply") val totalSupply: BigDecimal?,
    @SerializedName("last_24_hours_algo_price_change_percentage") val last24HoursAlgoPriceChangePercentage: BigDecimal?,
    @SerializedName("available_on_discover_mobile") val isAvailableOnDiscoverMobile: Boolean?
)
