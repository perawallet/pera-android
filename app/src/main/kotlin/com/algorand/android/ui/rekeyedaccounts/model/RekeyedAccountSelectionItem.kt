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

package com.algorand.android.ui.rekeyedaccounts.model

import com.algorand.android.models.PluralAnnotatedString
import com.algorand.android.models.RecyclerListItem
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview

sealed interface RekeyedAccountSelectionItem : RecyclerListItem {

    enum class ItemType {
        ICON_ITEM,
        TITLE_ITEM,
        DESCRIPTION_ITEM,
        AUTH_ACCOUNT_HEADER_ITEM,
        ACCOUNT_ITEM
    }

    val itemType: ItemType

    data class IconItem(val iconResId: Int) : RekeyedAccountSelectionItem {

        override val itemType: ItemType = ItemType.ICON_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is IconItem && iconResId == other.iconResId
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is IconItem && this == other
        }
    }

    data class TitleItem(val title: PluralAnnotatedString) : RekeyedAccountSelectionItem {

        override val itemType: ItemType = ItemType.TITLE_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleItem && title == other.title
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleItem && this == other
        }
    }

    data class DescriptionItem(val descriptionResId: Int) : RekeyedAccountSelectionItem {

        override val itemType: ItemType = ItemType.DESCRIPTION_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is DescriptionItem && descriptionResId == other.descriptionResId
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is DescriptionItem && this == other
        }
    }

    data class AuthAddressHeaderItem(
        val authAddress: String,
        val iconDrawablePreview: AccountIconDrawablePreview
    ) : RekeyedAccountSelectionItem {

        override val itemType: ItemType = ItemType.AUTH_ACCOUNT_HEADER_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is AuthAddressHeaderItem && authAddress == other.authAddress
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is AuthAddressHeaderItem && this == other
        }
    }

    data class AccountItem(
        val authAddress: String,
        val accountIconDrawablePreview: AccountIconDrawablePreview,
        val accountDisplayName: AccountDisplayName,
        val selectorDrawableRes: Int,
        val isSelected: Boolean
    ) : RekeyedAccountSelectionItem {

        override val itemType: ItemType = ItemType.ACCOUNT_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountItem && accountDisplayName.accountAddress == other.accountDisplayName.accountAddress
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountItem && this == other
        }
    }
}
