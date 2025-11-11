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

import androidx.annotation.StringRes
import com.algorand.android.models.RecyclerListItem

sealed interface RemoveAssetHeaderItem : RecyclerListItem {

    @Suppress("MagicNumber")
    enum class ItemType(val value: Int) {
        SEARCH_VIEW_ITEM(100),
        TITLE_VIEW_ITEM(101),
        DESCRIPTION_VIEW_ITEM(102)
    }

    val itemType: ItemType

    data class TitleViewItem(@param:StringRes val titleTextRes: Int) : RemoveAssetHeaderItem {

        override val itemType = ItemType.TITLE_VIEW_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleViewItem && other.titleTextRes == titleTextRes
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleViewItem && this == other
        }
    }

    data class DescriptionViewItem(@param:StringRes val descriptionTextRes: Int) : RemoveAssetHeaderItem {

        override val itemType = ItemType.DESCRIPTION_VIEW_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is DescriptionViewItem && other.descriptionTextRes == descriptionTextRes
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is DescriptionViewItem && this == other
        }
    }

    data class SearchViewItem(@param:StringRes val searchViewHintResId: Int) : RemoveAssetHeaderItem {

        override val itemType: ItemType = ItemType.SEARCH_VIEW_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is SearchViewItem && other.searchViewHintResId == searchViewHintResId
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is SearchViewItem && other == this
        }
    }

    companion object {
        val excludedItemFromDivider = listOf(
            ItemType.SEARCH_VIEW_ITEM.value,
            ItemType.TITLE_VIEW_ITEM.value,
            ItemType.DESCRIPTION_VIEW_ITEM.value
        )
    }
}
