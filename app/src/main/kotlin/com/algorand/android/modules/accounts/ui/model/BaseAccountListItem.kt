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

package com.algorand.android.modules.accounts.ui.model

import androidx.annotation.StringRes
import com.algorand.android.models.RecyclerListItem
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview

sealed interface BaseAccountListItem : RecyclerListItem {

    val itemType: ItemType

    enum class ItemType {
        ACCOUNT_SUCCESS,
        ACCOUNT_ERROR,
        HEADER,
        QUICK_ACTIONS,
        GOVERNANCE_BANNER,
        STAKING_BANNER,
        GENERIC_BANNER,
        BACKUP_BANNER,
        CARD_BANNER
    }

    data class QuickActionsItem(
        val isSwapButtonSelected: Boolean,
        val isImmersveEnabled: Boolean,
        val isStakingEnabled: Boolean
    ) : BaseAccountListItem {

        override val itemType: ItemType
            get() = ItemType.QUICK_ACTIONS

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is QuickActionsItem && isSwapButtonSelected == other.isSwapButtonSelected
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is QuickActionsItem && other == this
        }
    }

    data class BannerItem(
        val bannerId: Long,
        val buttonText: String?,
        val buttonUrl: String?,
        val isButtonVisible: Boolean,
        val title: String?,
        val isTitleVisible: Boolean,
        val description: String?,
        val isDescriptionVisible: Boolean,
        val type: BannerType
    ) : BaseAccountListItem {

        override val itemType: ItemType
            get() = type.itemType

        sealed interface BannerType {

            val itemType: ItemType

            data object Governance : BannerType {
                override val itemType: ItemType
                    get() = ItemType.GOVERNANCE_BANNER
            }

            data object Staking : BannerType {
                override val itemType: ItemType
                    get() = ItemType.STAKING_BANNER
            }

            data object Card : BannerType {
                override val itemType: ItemType
                    get() = ItemType.CARD_BANNER
            }

            data object Generic : BannerType {
                override val itemType: ItemType
                    get() = ItemType.GENERIC_BANNER
            }
        }

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is BannerItem && bannerId == other.bannerId
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is BannerItem && other == this
        }
    }

    data class BackupBannerItem(val addresses: List<String>) : BaseAccountListItem {

        override val itemType: ItemType = ItemType.BACKUP_BANNER

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is BackupBannerItem
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is BackupBannerItem && other == this
        }
    }

    data class HeaderItem(@StringRes val titleResId: Int) : BaseAccountListItem {

        override val itemType: ItemType
            get() = ItemType.HEADER

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is HeaderItem && titleResId == other.titleResId
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is HeaderItem && titleResId == other.titleResId
        }
    }

    data class AccountSuccessItem(
        val address: String,
        val primaryDisplayName: String,
        val secondaryDisplayName: String,
        val accountIconDrawablePreview: AccountIconDrawablePreview,
        val formattedPrimaryValue: String,
        val formattedSecondaryValue: String,
        val canCopyable: Boolean,
        val startSmallIconResource: Int?
    ) : BaseAccountListItem {
        override val itemType: ItemType
            get() = ItemType.ACCOUNT_SUCCESS

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountSuccessItem && address == other.address
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountSuccessItem && this == other
        }
    }

    data class AccountErrorItem(
        val address: String,
        val canCopyable: Boolean,
        val primaryDisplayName: String,
        val secondaryDisplayName: String,
        val accountIconDrawablePreview: AccountIconDrawablePreview
    ) : BaseAccountListItem {

        override val itemType: ItemType
            get() = ItemType.ACCOUNT_ERROR

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountErrorItem && address == other.address
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountErrorItem && this == other
        }
    }

    companion object {
        val bannerItemTypes = listOf(
            ItemType.GOVERNANCE_BANNER.ordinal,
            ItemType.STAKING_BANNER.ordinal,
            ItemType.CARD_BANNER.ordinal,
            ItemType.GENERIC_BANNER.ordinal,
            ItemType.BACKUP_BANNER.ordinal
        )
    }
}
