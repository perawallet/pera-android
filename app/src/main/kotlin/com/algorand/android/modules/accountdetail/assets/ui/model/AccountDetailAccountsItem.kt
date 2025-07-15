package com.algorand.android.modules.accountdetail.assets.ui.model

import androidx.annotation.StringRes
import com.algorand.android.models.RecyclerListItem

sealed interface AccountDetailAccountsItem : RecyclerListItem {

    @Suppress("MagicNumber")
    enum class ItemType(val viewType: Int) {
        ACCOUNT_PORTFOLIO(0),
        ASSETS_LIST_TITLE(1),
        SEARCH(2),
        QUICK_ACTIONS(3),
        BACKUP_WARNING(5)
    }

    val itemType: ItemType

    data class AccountPortfolioItem(
        val accountPrimaryFormattedParityValue: String?,
        val accountSecondaryFormattedParityValue: String?,
        val requiredMinBalance: String,
        val displayChart: Boolean
    ) : AccountDetailAccountsItem {

        override val itemType: ItemType
            get() = ItemType.ACCOUNT_PORTFOLIO

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountPortfolioItem &&
                accountPrimaryFormattedParityValue == other.accountPrimaryFormattedParityValue
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is AccountPortfolioItem && this == other
        }
    }

    data class BackupWarningItem(val isBackedUp: Boolean) : AccountDetailAccountsItem {

        override val itemType: ItemType
            get() = ItemType.BACKUP_WARNING

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is BackupWarningItem
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is BackupWarningItem && this == other
        }
    }

    data class TitleItem(
        @StringRes val titleRes: Int,
        val isAddAssetButtonVisible: Boolean
    ) : AccountDetailAccountsItem {

        override val itemType: ItemType
            get() = ItemType.ASSETS_LIST_TITLE

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleItem && titleRes == other.titleRes
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is TitleItem && this == other
        }
    }

    data class SearchViewItem(val query: String) : AccountDetailAccountsItem {

        override val itemType: ItemType
            get() = ItemType.SEARCH

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is SearchViewItem
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is SearchViewItem
        }
    }

    data class QuickActionItemContainer(
        val quickActionItems: List<AccountDetailQuickActionItem>
    ) : AccountDetailAccountsItem {

        override val itemType: ItemType
            get() = ItemType.QUICK_ACTIONS

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is QuickActionItemContainer && quickActionItems == other.quickActionItems
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is QuickActionItemContainer && this == other
        }
    }
}
