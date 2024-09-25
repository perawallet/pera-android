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

package com.algorand.android.modules.rekey.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.designsystem.RecyclerListItem
import kotlinx.parcelize.Parcelize

sealed class AccountSelectionListItem : RecyclerListItem, Parcelable {

    enum class ItemType {
        INSTRUCTION_ITEM,
        ACCOUNT_ITEM
    }

    enum class SearchType {
        REKEY,
        REGISTER
    }

    abstract val itemType: ItemType

    @Parcelize
    data class InstructionItem(
        val accountCount: Int,
        val searchType: SearchType
    ) : AccountSelectionListItem() {

        override val itemType: ItemType
            get() = ItemType.INSTRUCTION_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is InstructionItem && accountCount == other.accountCount
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is InstructionItem && this == other
        }
    }

    @Parcelize
    data class AccountItem(
        val address: String,
        val accountDisplayName: AccountDisplayName,
        val accountIconDrawablePreview: AccountIconDrawablePreview,
        var isSelected: Boolean = false,
        @DrawableRes val selectorDrawableRes: Int,
        val selectedLedgerAccount: SelectedLedgerAccount
    ) : AccountSelectionListItem() {

        override val itemType: ItemType
            get() = ItemType.ACCOUNT_ITEM

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountItem && address == other.address && isSelected == other.isSelected
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountItem && this == other
        }
    }
}
