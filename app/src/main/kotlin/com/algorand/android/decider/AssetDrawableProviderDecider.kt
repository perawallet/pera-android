/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.decider

import com.algorand.android.assetsearch.domain.model.BaseSearchedAsset
import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.assetdrawable.AlgoDrawableProvider
import com.algorand.android.utils.assetdrawable.AssetDrawableProvider
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import com.algorand.android.utils.assetdrawable.CollectibleDrawableProvider
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.asset.domain.usecase.GetCollectibleDetail
import com.algorand.wallet.asset.domain.usecase.IsCollectibleExist
import com.algorand.wallet.asset.domain.util.AssetConstants
import javax.inject.Inject

class AssetDrawableProviderDecider @Inject constructor(
    private val isCollectibleExist: IsCollectibleExist,
    private val getAsset: GetAsset,
    private val getCollectibleDetail: GetCollectibleDetail
) {

    suspend fun getAssetDrawableProvider(assetId: Long): BaseAssetDrawableProvider {
        if (assetId == AssetConstants.ALGO_ID) return AlgoDrawableProvider()

        val collectibleDetail = getCollectibleDetail(assetId)
        if (collectibleDetail != null) {
            return CollectibleDrawableProvider(
                assetName = AssetName.create(collectibleDetail.fullName),
                logoUri = collectibleDetail.primaryImageUrl
            )
        }

        val asset = getAsset(assetId)
        return AssetDrawableProvider(
            assetName = AssetName.create(asset?.fullName),
            logoUri = asset?.logoUri
        )
    }

    suspend fun getAssetDrawableProvider(
        assetId: Long,
        assetName: AssetName,
        logoUri: String?
    ): BaseAssetDrawableProvider {
        val isAlgo = assetId == AssetConstants.ALGO_ID
        val isCollectible = isCollectibleExist(assetId)
        return when {
            isAlgo -> AlgoDrawableProvider()
            isCollectible -> CollectibleDrawableProvider(assetName, logoUri)
            else -> AssetDrawableProvider(assetName, logoUri)
        }
    }

    /**
     * Since the all assets are not cached in local, we should check by domain model if it's ASA or NFT in listed ASAs
     * and NFTs in searching screens
     */
    fun getAssetDrawableProvider(searchedAsset: BaseSearchedAsset): BaseAssetDrawableProvider {
        return when {
            searchedAsset.assetId == AssetConstants.ALGO_ID -> {
                // This is unnecessary check but to keep consistency, I added this check, too
                AlgoDrawableProvider()
            }
            searchedAsset is BaseSearchedAsset.SearchedAsset -> {
                AssetDrawableProvider(
                    assetName = AssetName.create(searchedAsset.fullName),
                    logoUri = searchedAsset.logo
                )
            }
            searchedAsset is BaseSearchedAsset.SearchedCollectible -> {
                CollectibleDrawableProvider(
                    assetName = AssetName.create(searchedAsset.fullName),
                    logoUri = searchedAsset.collectible?.primaryImageUrl
                )
            }
            else -> AssetDrawableProvider(
                assetName = AssetName.create(searchedAsset.fullName),
                logoUri = searchedAsset.logo
            )
        }
    }

    fun getAssetDrawableProvider(asset: Asset): BaseAssetDrawableProvider {
        val assetName = asset.assetInfo?.name?.fullName
        return when {
            asset.id == AssetConstants.ALGO_ID -> AlgoDrawableProvider()
            asset is com.algorand.wallet.asset.domain.model.AssetDetail -> {
                AssetDrawableProvider(
                    assetName = AssetName.create(assetName),
                    logoUri = asset.logoUri
                )
            }
            asset is CollectibleDetail -> {
                CollectibleDrawableProvider(
                    assetName = AssetName.create(assetName),
                    logoUri = asset.collectibleInfo.primaryImageUrl
                )
            }
            else -> AssetDrawableProvider(
                assetName = AssetName.create(assetName),
                logoUri = asset.logoUri
            )
        }
    }

    fun getAssetDrawableProvider(assetData: BaseAccountAssetData.BaseOwnedAssetData): BaseAssetDrawableProvider {
        if (assetData.id == AssetConstants.ALGO_ID) return AlgoDrawableProvider()
        return AssetDrawableProvider(
            assetName = AssetName.create(assetData.name),
            logoUri = assetData.prismUrl
        )
    }

    fun getAssetDrawableProvider(
        collectibleData: BaseAccountAssetData.PendingAssetData.BasePendingCollectibleData
    ): BaseAssetDrawableProvider {
        return CollectibleDrawableProvider(
            assetName = AssetName.create(collectibleData.collectibleName),
            logoUri = collectibleData.primaryImageUrl
        )
    }

    fun getAssetDrawableProvider(
        collectibleData: BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
    ): BaseAssetDrawableProvider {
        return CollectibleDrawableProvider(
            assetName = AssetName.create(collectibleData.collectibleName),
            logoUri = collectibleData.prismUrl
        )
    }
}
