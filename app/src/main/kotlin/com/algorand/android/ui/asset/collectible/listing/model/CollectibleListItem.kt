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

package com.algorand.android.ui.asset.collectible.listing.model

import com.algorand.android.models.RecyclerListItem
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import com.algorand.android.utils.nftindicatordrawable.BaseNFTIndicatorDrawable

data class CollectibleListItem(
    val collectibleId: Long,
    val collectibleName: AssetName?,
    val collectionName: String?,
    val optedInAccountAddress: String,
    val baseAssetDrawableProvider: BaseAssetDrawableProvider,
    val type: CollectibleType,
    val itemType: ItemType
) : RecyclerListItem {

    @Suppress("MagicNumber")
    enum class ItemType(val value: Int) {
        LINEAR_VERTICAL_SIMPLE_NFT_ITEM(0),
        LINEAR_VERTICAL_SIMPLE_PENDING_ITEM(1),
        GRID_SIMPLE_NFT_ITEM(2),
        GRID_SIMPLE_PENDING_ITEM(3),
        PLACEHOLDER_ITEM(4),
    }

    val isAmountVisible: Boolean
        get() = type.isAmountVisible

    val formattedCollectibleAmount: String?
        get() = type.formattedCollectibleAmount

    val nftIndicatorDrawable: BaseNFTIndicatorDrawable?
        get() = type.nftIndicatorDrawable

    val shouldDecreaseOpacity: Boolean
        get() = type.shouldDecreaseOpacity

    override fun areItemsTheSame(other: RecyclerListItem): Boolean {
        return other is CollectibleListItem && this.collectibleId == other.collectibleId
    }

    override fun areContentsTheSame(other: RecyclerListItem): Boolean {
        return other is CollectibleListItem && this == other
    }

    sealed interface CollectibleType {

        val isAmountVisible: Boolean
        val formattedCollectibleAmount: String?
        val nftIndicatorDrawable: BaseNFTIndicatorDrawable?
        val shouldDecreaseOpacity: Boolean

        data class Owned(
            override val isAmountVisible: Boolean,
            override val formattedCollectibleAmount: String?,
            override val nftIndicatorDrawable: BaseNFTIndicatorDrawable?,
            override val shouldDecreaseOpacity: Boolean
        ) : CollectibleType

        data object Pending : CollectibleType {

            override val isAmountVisible: Boolean = false
            override val formattedCollectibleAmount: String? = null
            override val nftIndicatorDrawable: BaseNFTIndicatorDrawable? = null
            override val shouldDecreaseOpacity: Boolean = false
        }
    }

    companion object {
        val singleColumnItemList: List<Int> = listOf(
            ItemType.GRID_SIMPLE_NFT_ITEM.value,
            ItemType.GRID_SIMPLE_PENDING_ITEM.value
        )
        val excludedItemFromDivider: List<Int> = BaseCollectibleListHeaderItem.excludedItemFromDivider + listOf(
            ItemType.GRID_SIMPLE_NFT_ITEM.value,
            ItemType.GRID_SIMPLE_PENDING_ITEM.value,
            ItemType.PLACEHOLDER_ITEM.value
        )
    }
}
