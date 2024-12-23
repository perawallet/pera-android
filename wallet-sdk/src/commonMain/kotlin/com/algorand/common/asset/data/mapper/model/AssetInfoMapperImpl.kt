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

package com.algorand.common.asset.data.mapper.model

import com.algorand.common.asset.data.model.NodeAssetDetailResponse
import com.algorand.common.asset.data.database.model.AssetDetailEntity
import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.asset.domain.model.Asset
import com.algorand.common.asset.domain.model.AssetCreator

internal class AssetInfoMapperImpl : AssetInfoMapper {

    override fun invoke(assetResponse: AssetResponse): Asset.AssetInfo {
        return Asset.AssetInfo(
            name = assetResponse.mapToName(),
            decimals = assetResponse.fractionDecimals ?: 0,
            fiat = assetResponse.mapToFiat(),
            creator = assetResponse.mapToAssetCreator(),
            logo = assetResponse.mapToLogo(),
            explorerUrl = assetResponse.explorerUrl,
            project = assetResponse.mapToProject(),
            social = assetResponse.mapToSocial(),
            description = assetResponse.description,
            supply = assetResponse.mapToSupply(),
            url = assetResponse.url,
            isAvailableOnDiscoverMobile = assetResponse.isAvailableOnDiscoverMobile
        )
    }

    override fun invoke(entity: AssetDetailEntity): Asset.AssetInfo {
        return Asset.AssetInfo(
            name = entity.mapToName(),
            decimals = entity.decimals,
            fiat = entity.mapToFiat(),
            creator = entity.mapToAssetCreator(),
            logo = entity.mapToLogo(),
            explorerUrl = entity.explorerUrl,
            project = entity.mapToProject(),
            social = entity.mapToSocial(),
            description = entity.description,
            supply = entity.mapToSupply(),
            url = entity.url,
            isAvailableOnDiscoverMobile = entity.availableOnDiscoverMobile
        )
    }

    override fun invoke(response: NodeAssetDetailResponse): Asset.AssetInfo {
        return Asset.AssetInfo(
            name = Asset.Name(
                fullName = response.nodeAssetDetailParamsResponse?.fullName.orEmpty(),
                shortName = response.nodeAssetDetailParamsResponse?.shortName.orEmpty()
            ),
            decimals = response.nodeAssetDetailParamsResponse?.fractionalDecimal ?: 0,
            supply = Asset.Supply(
                total = response.nodeAssetDetailParamsResponse?.totalSupply,
                max = null
            ),
            fiat = null,
            creator = null,
            logo = null,
            explorerUrl = null,
            project = null,
            social = null,
            description = null,
            url = null,
            isAvailableOnDiscoverMobile = null
        )
    }

    private fun AssetResponse.mapToAssetCreator(): AssetCreator? {
        return assetCreator?.let { assetCreator ->
            AssetCreator(
                id = assetCreator.id,
                publicKey = assetCreator.publicKey,
                isVerifiedAssetCreator = assetCreator.isVerifiedAssetCreator
            )
        }
    }

    private fun AssetDetailEntity.mapToAssetCreator(): AssetCreator {
        return AssetCreator(
            id = assetCreatorId,
            publicKey = assetCreatorAddress,
            isVerifiedAssetCreator = isVerifiedAssetCreator
        )
    }

    private fun AssetResponse.mapToFiat(): Asset.Fiat {
        return Asset.Fiat(
            usdValue = usdValue,
            last24HoursAlgoPriceChangePercentage = last24HoursAlgoPriceChangePercentage
        )
    }

    private fun AssetDetailEntity.mapToFiat(): Asset.Fiat {
        return Asset.Fiat(
            usdValue = usdValue,
            last24HoursAlgoPriceChangePercentage = last24HoursAlgoPriceChangePercentage
        )
    }

    private fun AssetResponse.mapToName(): Asset.Name {
        return Asset.Name(
            fullName = fullName.orEmpty(),
            shortName = shortName.orEmpty()
        )
    }

    private fun AssetDetailEntity.mapToName(): Asset.Name {
        return Asset.Name(
            fullName = name.orEmpty(),
            shortName = unitName.orEmpty()
        )
    }

    private fun AssetResponse.mapToLogo(): Asset.Logo? {
        if (logoUri.isNullOrBlank() && logoSvgUri.isNullOrBlank()) {
            return null
        }
        return Asset.Logo(
            uri = logoUri,
            svgUri = logoSvgUri
        )
    }

    private fun AssetDetailEntity.mapToLogo(): Asset.Logo? {
        if (logoUrl.isNullOrBlank() && logoSvgUrl.isNullOrBlank()) {
            return null
        }
        return Asset.Logo(
            uri = logoUrl,
            svgUri = logoSvgUrl
        )
    }

    private fun AssetResponse.mapToProject(): Asset.Project? {
        if (projectName.isNullOrBlank() && projectUrl.isNullOrBlank()) {
            return null
        }
        return Asset.Project(
            name = projectName,
            url = projectUrl
        )
    }

    private fun AssetDetailEntity.mapToProject(): Asset.Project? {
        if (projectName.isNullOrBlank() && projectUrl.isNullOrBlank()) {
            return null
        }
        return Asset.Project(
            name = projectName,
            url = projectUrl
        )
    }

    private fun AssetResponse.mapToSocial(): Asset.Social? {
        if (discordUrl.isNullOrBlank() && telegramUrl.isNullOrBlank() && twitterUsername.isNullOrBlank()) {
            return null
        }
        return Asset.Social(
            discordUrl = discordUrl,
            telegramUrl = telegramUrl,
            twitterUsername = twitterUsername
        )
    }

    private fun AssetDetailEntity.mapToSocial(): Asset.Social? {
        if (discordUrl.isNullOrBlank() && telegramUrl.isNullOrBlank() && twitterUsername.isNullOrBlank()) {
            return null
        }
        return Asset.Social(
            discordUrl = discordUrl,
            telegramUrl = telegramUrl,
            twitterUsername = twitterUsername
        )
    }

    private fun AssetResponse.mapToSupply(): Asset.Supply {
        return Asset.Supply(
            total = totalSupply,
            max = maxSupply
        )
    }

    private fun AssetDetailEntity.mapToSupply(): Asset.Supply {
        return Asset.Supply(
            total = totalSupply,
            max = maxSupply
        )
    }
}
