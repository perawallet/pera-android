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

package com.algorand.common.asset.data.mapper.entity

import com.algorand.common.asset.data.database.model.AssetDetailEntity
import com.algorand.common.asset.data.model.AssetResponse

internal class AssetDetailEntityMapperImpl(
    private val verificationTierEntityMapper: VerificationTierEntityMapper
) : AssetDetailEntityMapper {

    override fun invoke(response: AssetResponse): AssetDetailEntity? {
        return with(response) {
            AssetDetailEntity(
                assetId = assetId ?: return null,
                name = fullName,
                unitName = shortName,
                decimals = fractionDecimals ?: 0,
                usdValue = usdValue,
                maxSupply = maxSupply ?: "0",
                explorerUrl = explorerUrl,
                projectUrl = projectUrl,
                projectName = projectName,
                logoSvgUrl = logoSvgUri,
                logoUrl = logoUri,
                description = description,
                totalSupply = totalSupply ?: "0",
                url = url,
                telegramUrl = telegramUrl,
                twitterUsername = twitterUsername,
                discordUrl = discordUrl,
                availableOnDiscoverMobile = isAvailableOnDiscoverMobile ?: false,
                last24HoursAlgoPriceChangePercentage = last24HoursAlgoPriceChangePercentage,
                verificationTier = verificationTierEntityMapper(verificationTier),
                assetCreatorAddress = assetCreator?.publicKey,
                assetCreatorId = assetCreator?.id,
                isVerifiedAssetCreator = assetCreator?.isVerifiedAssetCreator
            )
        }
    }
}
