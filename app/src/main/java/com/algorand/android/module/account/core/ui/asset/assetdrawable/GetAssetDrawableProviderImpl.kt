/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.account.core.ui.asset.assetdrawable

import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.drawableui.asset.AlgoDrawableProvider
import com.algorand.android.drawableui.asset.AssetDrawableProvider
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider
import com.algorand.android.drawableui.asset.CollectibleDrawableProvider
import javax.inject.Inject

internal class GetAssetDrawableProviderImpl @Inject constructor(
    private val getAssetName: GetAssetName,
    private val getAsset: GetAsset
) : GetAssetDrawableProvider {

    override suspend operator fun invoke(assetId: Long): BaseAssetDrawableProvider {
        val isAlgo = assetId == ALGO_ASSET_ID
        val asset = getAsset(assetId)
        return when {
            isAlgo -> createAlgoDrawableProvider()
            asset is CollectibleDetail -> createCollectibleDrawableProvider(asset)
            else -> createAssetDrawableProvider(asset)
        }
    }

    override operator fun invoke(asset: Asset): BaseAssetDrawableProvider {
        return when {
            asset.id == ALGO_ASSET_ID -> AlgoDrawableProvider()
            asset is AssetDetail -> createAssetDrawableProvider(asset)
            asset is CollectibleDetail -> createCollectibleDrawableProvider(asset)
            else -> createAssetDrawableProvider(asset)
        }
    }

    override suspend operator fun invoke(
        assetId: Long,
        assetName: AssetName,
        logoUri: String?
    ): BaseAssetDrawableProvider {
        val isAlgo = assetId == ALGO_ASSET_ID
        val asset = getAsset(assetId)
        return when {
            isAlgo -> createAlgoDrawableProvider()
            asset is CollectibleDetail -> CollectibleDrawableProvider(assetName.avatarName, logoUri)
            else -> AssetDrawableProvider(assetName.avatarName, logoUri)
        }
    }

    private fun createAlgoDrawableProvider(): AlgoDrawableProvider {
        return AlgoDrawableProvider()
    }

    private fun createCollectibleDrawableProvider(
        collectibleDetail: CollectibleDetail?
    ): CollectibleDrawableProvider {
        return CollectibleDrawableProvider(
            assetAvatarName = getAssetName(collectibleDetail?.assetInfo?.name?.fullName).avatarName,
            logoUri = collectibleDetail?.primaryImageUrl
        )
    }

    private fun createAssetDrawableProvider(assetDetail: Asset?): AssetDrawableProvider {
        return AssetDrawableProvider(
            assetAvatarName = getAssetName(assetDetail?.assetInfo?.name?.fullName).avatarName,
            logoUri = assetDetail?.assetInfo?.logo?.uri
        )
    }
}

/*
    /**
     * Since the all assets are not cached in local, we should check by domain model if it's ASA or NFT in listed ASAs
     * and NFTs in searching screens
     */
    fun getAssetDrawableProvider(searchedAsset: BaseSearchedAsset): BaseAssetDrawableProvider {
        return when {
            searchedAsset.assetId == com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID -> {
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
 */