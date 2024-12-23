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
    @SerialName("asset_id") val assetId: Long?,
    @SerialName("name") val fullName: String?,
    @SerialName("logo") val logoUri: String?,
    @SerialName("unit_name") val shortName: String?,
    @SerialName("fraction_decimals") val fractionDecimals: Int?,
    @SerialName("usd_value") val usdValue: String?,
    @SerialName("creator") val assetCreator: AssetCreatorResponse?,
    @SerialName("collectible") val collectible: CollectibleResponse?,
    @SerialName("total") val maxSupply: String?,
    @SerialName("explorer_url") val explorerUrl: String?,
    @SerialName("verification_tier") val verificationTier: VerificationTierResponse?,
    @SerialName("project_url") val projectUrl: String?,
    @SerialName("project_name") val projectName: String?,
    @SerialName("logo_svg") val logoSvgUri: String?,
    @SerialName("discord_url") val discordUrl: String?,
    @SerialName("telegram_url") val telegramUrl: String?,
    @SerialName("twitter_username") val twitterUsername: String?,
    @SerialName("description") val description: String?,
    @SerialName("url") val url: String?,
    @SerialName("total_supply") val totalSupply: String?,
    @SerialName("last_24_hours_algo_price_change_percentage") val last24HoursAlgoPriceChangePercentage: String?,
    @SerialName("available_on_discover_mobile") val isAvailableOnDiscoverMobile: Boolean?
)
