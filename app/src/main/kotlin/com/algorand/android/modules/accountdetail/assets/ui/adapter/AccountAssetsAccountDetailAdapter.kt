/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.modules.accountdetail.assets.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accountdetail.assets.ui.AccountAssetsLineChartViewModel
import com.algorand.android.modules.accountdetail.assets.ui.adapter.AccountDetailAssetsTitleViewHolder.AccountDetailAssetsTitleViewHolderListener
import com.algorand.android.modules.accountdetail.assets.ui.adapter.AccountDetailQuickActionsViewHolder.AccountDetailQuickActionsListener
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.ItemType.ACCOUNT_PORTFOLIO
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.ItemType.ASSETS_LIST_TITLE
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.ItemType.BACKUP_WARNING
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.ItemType.QUICK_ACTIONS
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.ItemType.SEARCH
import com.algorand.android.utils.hideKeyboard

class AccountAssetsAccountDetailAdapter(
    private val address: String,
    private val lineChartViewModel: AccountAssetsLineChartViewModel,
    private val listener: Listener
) : ListAdapter<AccountDetailAccountsItem, BaseViewHolder<AccountDetailAccountsItem>>(BaseDiffUtil()) {

    private val accountValueViewHolderListener = object : AccountValueViewHolder.Listener {
        override fun onAccountValueClick() {
            listener.onAccountValueClick()
        }

        override fun onInfoButtonClick() {
            listener.onRequiredMinimumBalanceClick()
        }

        override fun onChartTap() {
            listener.onChartTap()
        }
    }

    private val searchViewItemListener = object : SearchViewViewHolder.Listener {
        override fun onSearchQueryChanged(query: String) {
            listener.onSearchQueryUpdated(query)
        }
    }

    private val quickActionsViewHolderListener = object : AccountDetailQuickActionsListener {
        override fun onAssetInboxClick() {
            listener.onAssetInboxClick()
        }

        override fun onSendClick() {
            listener.onSendClick()
        }

        override fun onSwapClick() {
            listener.onSwapClick()
        }

        override fun onMoreClick() {
            listener.onMoreClick()
        }

        override fun onCopyAddressClick() {
            listener.onCopyAddressClick()
        }

        override fun onShowAddressClick() {
            listener.onShowAddressClick()
        }

        override fun onBuySellClick() {
            listener.onBuySellClick()
        }
    }

    private val assetsTitleViewHolderListener = object : AccountDetailAssetsTitleViewHolderListener {
        override fun onManageAssetsClick() {
            listener.onManageAssetsClick()
        }

        override fun onAddAssetClick() {
            listener.onAddNewAssetClick()
        }
    }

    private val backupWarningListener = object : BackupWarningViewHolder.Listener {
        override fun onBackupNowClick() {
            listener.onBackupNowClick()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<AccountDetailAccountsItem> {
        return when (viewType) {
            SEARCH.viewType -> createAssetSearchItemViewHolder(parent)
            ACCOUNT_PORTFOLIO.viewType -> createAccountValueViewHolder(parent)
            ASSETS_LIST_TITLE.viewType -> createAssetTitleViewHolder(parent)
            QUICK_ACTIONS.viewType -> createQuickActionsViewHolder(parent)
            BACKUP_WARNING.viewType -> createBackupWarningViewHolder(parent)
            else -> throw IllegalArgumentException("$logTag : Item View Type is Unknown.")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AccountDetailAccountsItem>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<AccountDetailAccountsItem>) {
        super.onViewDetachedFromWindow(holder)
        if (holder is SearchViewViewHolder) {
            holder.itemView.hideKeyboard()
        }
    }

    private fun createAssetSearchItemViewHolder(parent: ViewGroup): SearchViewViewHolder {
        return SearchViewViewHolder.create(parent, searchViewItemListener)
    }

    private fun createAccountValueViewHolder(parent: ViewGroup): AccountValueViewHolder {
        return AccountValueViewHolder.create(parent, address, lineChartViewModel, accountValueViewHolderListener)
    }

    private fun createAssetTitleViewHolder(parent: ViewGroup): AccountDetailAssetsTitleViewHolder {
        return AccountDetailAssetsTitleViewHolder.create(parent, assetsTitleViewHolderListener)
    }

    private fun createQuickActionsViewHolder(parent: ViewGroup): AccountDetailQuickActionsViewHolder {
        return AccountDetailQuickActionsViewHolder.create(parent, quickActionsViewHolderListener)
    }

    private fun createBackupWarningViewHolder(parent: ViewGroup): BackupWarningViewHolder {
        return BackupWarningViewHolder.create(parent, backupWarningListener)
    }

    interface Listener {
        fun onSearchQueryUpdated(query: String) {}
        fun onAddNewAssetClick() {}
        fun onManageAssetsClick()
        fun onAssetInboxClick()
        fun onSendClick()
        fun onSwapClick()
        fun onMoreClick()
        fun onRequiredMinimumBalanceClick()
        fun onCopyAddressClick()
        fun onShowAddressClick()
        fun onBackupNowClick()
        fun onBuySellClick()
        fun onAccountValueClick()
        fun onChartTap()
    }

    companion object {
        private val logTag = AccountAssetsAccountDetailAdapter::class.simpleName
    }
}
