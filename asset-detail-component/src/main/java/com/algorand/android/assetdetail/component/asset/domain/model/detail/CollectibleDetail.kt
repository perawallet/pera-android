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

package com.algorand.android.assetdetail.component.asset.domain.model.detail

import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleTrait
import com.algorand.android.assetdetail.component.collectible.domain.model.BaseCollectibleMedia
import java.math.BigDecimal

sealed interface CollectibleDetail : Asset {

    val collectibleInfo: CollectibleInfo

    val collectibleMedias: List<BaseCollectibleMedia>

    val prismUrl: String?
        get() = collectibleInfo.prismUrl

    val title: String?
        get() = collectibleInfo.title

    val collectionName: String?
        get() = collectibleInfo.collectionName

    val primaryImageUrl: String?
        get() = collectibleInfo.primaryImageUrl

    data class CollectibleInfo(
        val title: String?,
        val collectionName: String?,
        val collectibleDescription: String?,
        val traits: List<CollectibleTrait>?,
        val nftExplorerUrl: String?,
        val prismUrl: String?,
        val primaryImageUrl: String?
    )

    fun isPure(): Boolean {
        return assetInfo?.supply?.total?.compareTo(BigDecimal.ONE) == 0 && assetInfo?.decimals == 0
    }
}
