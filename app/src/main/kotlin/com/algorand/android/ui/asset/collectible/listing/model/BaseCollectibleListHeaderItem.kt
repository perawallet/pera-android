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

import androidx.annotation.StringRes
import com.algorand.android.models.RecyclerListItem
import com.algorand.android.utils.Event

sealed interface BaseCollectibleListHeaderItem : RecyclerListItem {

    @Suppress("MagicNumber")
    enum class ItemType(val value: Int) {
        TITLE_TEXT_VIEW_ITEM(101),
        SEARCH_VIEW_ITEM(102),
        INFO_VIEW_ITEM(103)
    }

    val itemType: ItemType

    data object TitleTextViewItem : BaseCollectibleListHeaderItem {

        override val itemType: ItemType
            get() = ItemType.TITLE_TEXT_VIEW_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleTextViewItem
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleTextViewItem
        }
    }

    data class InfoViewItem(
        val displayedCollectibleCount: Int,
        val isAddButtonVisible: Boolean
    ) : BaseCollectibleListHeaderItem {

        override val itemType: ItemType
            get() = ItemType.INFO_VIEW_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is InfoViewItem && other.displayedCollectibleCount == displayedCollectibleCount
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is InfoViewItem && other == this
        }
    }

    data class SearchViewItem(
        @param:StringRes val searchViewHintResId: Int,
        val query: String,
        val onGridListViewSelectedEvent: Event<Unit>? = null,
        val onLinearListViewSelectedEvent: Event<Unit>? = null
    ) : BaseCollectibleListHeaderItem {

        override val itemType: ItemType = ItemType.SEARCH_VIEW_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is SearchViewItem && other.searchViewHintResId == searchViewHintResId
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is SearchViewItem && other.searchViewHintResId == searchViewHintResId
        }
    }

    companion object {
        val excludedItemFromDivider = listOf(
            ItemType.TITLE_TEXT_VIEW_ITEM.value,
            ItemType.SEARCH_VIEW_ITEM.value,
            ItemType.INFO_VIEW_ITEM.value
        )
    }
}
