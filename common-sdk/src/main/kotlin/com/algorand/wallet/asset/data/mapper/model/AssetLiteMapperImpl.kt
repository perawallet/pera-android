/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.account.info.data.mapper.model.AssetStatusMapper
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMediaTypeMapper
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.model.CollectibleMediaType
import javax.inject.Inject

internal class AssetLiteMapperImpl @Inject constructor(
    private val verificationTierMapper: VerificationTierMapper,
    private val assetStatusMapper: AssetStatusMapper,
    private val mediaTypeMapper: CollectibleMediaTypeMapper
) : AssetLiteMapper {

    override fun invoke(dto: PaginatedAssetCollectibleItemDto): AssetLite {
        return with(dto) {
            AssetLite(
                address = address,
                amount = amount,
                usdValue = usdValue,
                decimal = decimals,
                assetId = assetId,
                name = assetName,
                shortName = assetShortName,
                verificationTier = verificationTierMapper(verificationTierEntity),
                totalUsdValue = totalUsdValue,
                assetStatus = assetStatusMapper(assetStatusEntity),
                optedInAtRound = optedInAtRound,
                type = if (collectibleName != null) {
                    AssetLite.Type.Collectible(
                        name = collectibleName,
                        logoUrl = collectibleImageUrl,
                        collectionName = collectionName,
                        mediaType = mediaTypeEntity?.let { mediaTypeMapper(it) } ?: CollectibleMediaType.UNKNOWN,
                    )
                } else {
                    AssetLite.Type.Asset(logoUrl)
                }
            )
        }
    }
}
