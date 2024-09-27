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

package com.algorand.android.decider

import com.algorand.android.assetsearch.domain.model.BaseSearchedAsset
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAsset
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.assetdrawable.AlgoDrawableProvider
import com.algorand.android.utils.assetdrawable.AssetDrawableProvider
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import com.algorand.android.utils.assetdrawable.CollectibleDrawableProvider
import javax.inject.Inject

class AssetDrawableProviderDecider @Inject constructor(
    private val getAsset: GetAsset
) {

    suspend fun getAssetDrawableProvider(assetId: Long): BaseAssetDrawableProvider {
        val isAlgo = assetId == ALGO_ASSET_ID
        val asset = getAsset(assetId)
        return when {
            isAlgo -> createAlgoDrawableProvider()
            asset is CollectibleDetail -> createCollectibleDrawableProvider(asset)
            else -> createAssetDrawableProvider(asset)
        }
    }

    suspend fun getAssetDrawableProvider(
        assetId: Long,
        assetName: AssetName,
        logoUri: String?
    ): BaseAssetDrawableProvider {
        val isAlgo = assetId == ALGO_ASSET_ID
        val asset = getAsset(assetId)
        return when {
            isAlgo -> createAlgoDrawableProvider()
            asset is CollectibleDetail -> CollectibleDrawableProvider(assetName, logoUri)
            else -> AssetDrawableProvider(assetName, logoUri)
        }
    }

    private fun createAlgoDrawableProvider(): AlgoDrawableProvider {
        return AlgoDrawableProvider()
    }

    private fun createCollectibleDrawableProvider(collectibleDetail: CollectibleDetail?): CollectibleDrawableProvider {
        return CollectibleDrawableProvider(
            assetName = AssetName.create(collectibleDetail?.fullName),
            logoUri = collectibleDetail?.primaryImageUrl
        )
    }

    private fun createAssetDrawableProvider(asset: Asset?): AssetDrawableProvider {
        return AssetDrawableProvider(
            assetName = AssetName.create(asset?.fullName),
            logoUri = asset?.logoUri
        )
    }

    /**
     * Since the all assets are not cached in local, we should check by domain model if it's ASA or NFT in listed ASAs
     * and NFTs in searching screens
     */
    fun getAssetDrawableProvider(searchedAsset: BaseSearchedAsset): BaseAssetDrawableProvider {
        return when {
            searchedAsset.assetId == ALGO_ASSET_ID -> {
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
}
