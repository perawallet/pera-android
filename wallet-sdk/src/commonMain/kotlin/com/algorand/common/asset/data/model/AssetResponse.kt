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

package com.algorand.common.asset.data.model

import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AssetResponse(
    @SerialName("asset_id") val assetId: Long? = null,
    @SerialName("name") val fullName: String? = null,
    @SerialName("logo") val logoUri: String? = null,
    @SerialName("unit_name") val shortName: String? = null,
    @SerialName("fraction_decimals") val fractionDecimals: Int? = null,
    @SerialName("usd_value") val usdValue: String? = null,
    @SerialName("creator") val assetCreator: AssetCreatorResponse? = null,
    @SerialName("collectible") val collectible: CollectibleResponse? = null,
    @SerialName("total") val maxSupply: String? = null,
    @SerialName("explorer_url") val explorerUrl: String? = null,
    @SerialName("verification_tier") val verificationTier: VerificationTierResponse? = null,
    @SerialName("project_url") val projectUrl: String? = null,
    @SerialName("project_name") val projectName: String? = null,
    @SerialName("logo_svg") val logoSvgUri: String? = null,
    @SerialName("discord_url") val discordUrl: String? = null,
    @SerialName("telegram_url") val telegramUrl: String? = null,
    @SerialName("twitter_username") val twitterUsername: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("total_supply") val totalSupply: String? = null,
    @SerialName("last_24_hours_algo_price_change_percentage") val last24HoursAlgoPriceChangePercentage: String? = null,
    @SerialName("available_on_discover_mobile") val isAvailableOnDiscoverMobile: Boolean? = null
)
