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

package com.algorand.android.ui.asset.remove.model

import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.models.RecyclerListItem
import com.algorand.android.models.ScreenState
import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.modules.assets.core.ui.domain.model.AssetName
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import java.math.BigDecimal
import java.math.BigInteger

sealed interface BaseRemoveAssetItem : RecyclerListItem {

    @Suppress("MagicNumber")
    enum class ItemType(val value: Int) {
        REMOVE_ASSET_ITEM(0),
        REMOVE_COLLECTIBLE_ITEM(1),
        SCREEN_STATE_ITEM(2),
        PLACEHOLDER_ITEM(3)
    }

    val itemType: ItemType

    data class ScreenStateItem(val screenState: ScreenState) : BaseRemoveAssetItem {

        override val itemType: ItemType = ItemType.SCREEN_STATE_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is ScreenStateItem && other.screenState == screenState
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is ScreenStateItem && other == this
        }
    }

    data class RemoveAssetItem(
        val id: Long,
        val name: AssetName,
        val shortName: AssetName,
        val decimals: Int,
        val amount: BigInteger,
        val formattedAmount: String,
        val formattedCompactAmount: String,
        val formattedSelectedCurrencyValue: String,
        val formattedSelectedCurrencyCompactValue: String?,
        val baseAssetDrawableProvider: BaseAssetDrawableProvider,
        val actionItemButtonState: AccountAssetItemButtonState,
        val amountInPrimaryCurrency: BigDecimal?,
        val type: RemoveAssetItemType
    ) : BaseRemoveAssetItem {

        override val itemType: ItemType
            get() = type.itemType

        sealed interface RemoveAssetItemType {

            val itemType: ItemType

            data class Asset(
                val verificationTierConfiguration: VerificationTierConfiguration
            ) : RemoveAssetItemType {
                override val itemType: ItemType
                    get() = ItemType.REMOVE_ASSET_ITEM
            }

            data object Collectible : RemoveAssetItemType {
                override val itemType: ItemType
                    get() = ItemType.REMOVE_COLLECTIBLE_ITEM
            }
        }

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is RemoveAssetItem && other.id == id
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is RemoveAssetItem && other == this
        }
    }
}
