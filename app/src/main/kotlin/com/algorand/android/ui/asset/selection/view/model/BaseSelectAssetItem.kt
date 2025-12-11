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

package com.algorand.android.ui.asset.selection.view.model

import com.algorand.android.customviews.accountandassetitem.model.BaseItemConfiguration
import com.algorand.android.models.RecyclerListItem
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import java.math.BigDecimal
import java.math.BigInteger

sealed interface BaseSelectAssetItem : RecyclerListItem {

    enum class ItemType {
        SELECT_ASSET_TEM,
        SELECT_COLLECTIBLE_IMAGE_ITEM,
        SELECT_COLLECTIBLE_VIDEO_ITEM,
        SELECT_COLLECTIBLE_AUDIO_ITEM,
        SELECT_COLLECTIBLE_NOT_SUPPORTED_ITEM,
        SELECT_COLLECTIBLE_MIXED_ITEM,
        PLACEHOLDER_ITEM
    }

    val itemType: ItemType
    val isFavorite: Boolean

    data class SelectAssetItem(
        val assetItemConfiguration: BaseItemConfiguration.BaseAssetItemConfiguration.AssetItemConfiguration,
        override val isFavorite: Boolean
    ) : BaseSelectAssetItem {

        override val itemType: ItemType = ItemType.SELECT_ASSET_TEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is SelectAssetItem && assetItemConfiguration.assetId == other.assetItemConfiguration.assetId
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is SelectAssetItem && this == other
        }
    }

    data class SelectCollectibleItem(
        val id: Long,
        val name: String?,
        val shortName: String?,
        val avatarDisplayText: AssetName,
        val isAlgo: Boolean,
        val amount: BigInteger,
        val formattedAmount: String,
        val formattedCompactAmount: String,
        val formattedSelectedCurrencyValue: String,
        val formattedSelectedCurrencyCompactValue: String,
        val isAmountInSelectedCurrencyVisible: Boolean,
        val baseAssetDrawableProvider: BaseAssetDrawableProvider,
        val optedInAtRound: Long?,
        val amountInSelectedCurrency: BigDecimal?,
        val type: CollectibleType,
        override val isFavorite: Boolean
    ) : BaseSelectAssetItem {

        override val itemType: ItemType
            get() = type.itemType

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is SelectCollectibleItem && id == other.id
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is SelectCollectibleItem && this == other
        }

        sealed interface CollectibleType {

            val itemType: ItemType

            data object Image : CollectibleType {

                override val itemType: ItemType
                    get() = ItemType.SELECT_COLLECTIBLE_IMAGE_ITEM
            }

            data object Video : CollectibleType {
                override val itemType: ItemType
                    get() = ItemType.SELECT_COLLECTIBLE_VIDEO_ITEM
            }

            data object Audio : CollectibleType {
                override val itemType: ItemType
                    get() = ItemType.SELECT_COLLECTIBLE_AUDIO_ITEM
            }

            data object Mixed : CollectibleType {
                override val itemType: ItemType
                    get() = ItemType.SELECT_COLLECTIBLE_MIXED_ITEM
            }

            data object NotSupported : CollectibleType {
                override val itemType: ItemType
                    get() = ItemType.SELECT_COLLECTIBLE_NOT_SUPPORTED_ITEM
            }
        }
    }
}
