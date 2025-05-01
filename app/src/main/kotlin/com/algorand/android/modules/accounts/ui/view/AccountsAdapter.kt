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

package com.algorand.android.modules.accounts.ui.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.algorand.android.banner.domain.model.BannerType
import com.algorand.android.banner.ui.viewholder.BackupBannerViewHolder
import com.algorand.android.banner.ui.viewholder.BaseBannerViewHolder
import com.algorand.android.banner.ui.viewholder.CardsBannerViewHolder
import com.algorand.android.banner.ui.viewholder.GenericBannerViewHolder
import com.algorand.android.banner.ui.viewholder.GovernanceBannerViewHolder
import com.algorand.android.banner.ui.viewholder.StakingBannerViewHolder
import com.algorand.android.models.BaseDiffUtil
import com.algorand.android.models.BaseViewHolder
import com.algorand.android.modules.accounts.ui.view.viewholder.AccountErrorItemViewHolder
import com.algorand.android.modules.accounts.ui.view.viewholder.AccountItemViewHolder
import com.algorand.android.modules.accounts.ui.view.viewholder.AccountsQuickActionsViewHolder
import com.algorand.android.modules.accounts.ui.view.viewholder.AccountsQuickActionsViewHolder.AccountsQuickActionsListener
import com.algorand.android.modules.accounts.ui.view.viewholder.HeaderViewHolder
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.ACCOUNT_ERROR
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.ACCOUNT_SUCCESS
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.BACKUP_BANNER
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.CARD_BANNER
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.GENERIC_BANNER
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.GOVERNANCE_BANNER
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.HEADER
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.QUICK_ACTIONS
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem.ItemType.STAKING_BANNER

class AccountsAdapter(
    private val accountAdapterListener: AccountAdapterListener
) : ListAdapter<BaseAccountListItem, BaseViewHolder<BaseAccountListItem>>(BaseDiffUtil()) {

    private val accountClickListener = object : AccountItemViewHolder.AccountClickListener {
        override fun onAccountClick(publicKey: String) {
            accountAdapterListener.onSucceedAccountClick(publicKey)
        }

        override fun onAccountLongPress(publicKey: String) {
            accountAdapterListener.onAccountItemLongPressed(publicKey)
        }
    }

    private val accountErrorClickListener = object : AccountErrorItemViewHolder.AccountClickListener {
        override fun onAccountClick(publicKey: String) {
            accountAdapterListener.onFailedAccountClick(publicKey)
        }

        override fun onAccountLongPress(publicKey: String) {
            accountAdapterListener.onAccountItemLongPressed(publicKey)
        }
    }

    private val baseBannerListener = object : BaseBannerViewHolder.BannerListener {
        override fun onActionButtonClick(url: String, bannerType: BannerType) {
            accountAdapterListener.onBannerActionButtonClick(url = url, bannerType)
        }

        override fun onCloseBannerClick(bannerId: Long) {
            accountAdapterListener.onBannerCloseButtonClick(bannerId)
        }
    }

    private val governanceBaseBannerListener = object : BaseBannerViewHolder.BannerListener {
        override fun onActionButtonClick(url: String, bannerType: BannerType) {
            accountAdapterListener.onBannerActionButtonClick(url = url, bannerType)
        }

        override fun onCloseBannerClick(bannerId: Long) {
            accountAdapterListener.onBannerCloseButtonClick(bannerId)
        }
    }

    private val stakingBaseBannerListener = object : BaseBannerViewHolder.BannerListener {
        override fun onActionButtonClick(url: String, bannerType: BannerType) {
            accountAdapterListener.onBannerActionButtonClick(url = url, bannerType = bannerType)
        }

        override fun onCloseBannerClick(bannerId: Long) {
            accountAdapterListener.onBannerCloseButtonClick(bannerId)
        }
    }

    private val cardBaseBannerListener = object : BaseBannerViewHolder.BannerListener {
        override fun onActionButtonClick(url: String, bannerType: BannerType) {
            accountAdapterListener.onBannerActionButtonClick(url = url, bannerType = bannerType)
        }

        override fun onCloseBannerClick(bannerId: Long) {
            accountAdapterListener.onBannerCloseButtonClick(bannerId)
        }
    }

    private val backupBannerListener = object : BackupBannerViewHolder.Listener {
        override fun onActionButtonClick() {
            accountAdapterListener.onBackupBannerActionButtonClick()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseAccountListItem> {
        return when (viewType) {
            HEADER.ordinal -> HeaderViewHolder.create(parent, accountAdapterListener)
            ACCOUNT_SUCCESS.ordinal -> AccountItemViewHolder.create(parent, accountClickListener)
            ACCOUNT_ERROR.ordinal -> AccountErrorItemViewHolder.create(parent, accountErrorClickListener)
            GOVERNANCE_BANNER.ordinal -> GovernanceBannerViewHolder.create(governanceBaseBannerListener, parent)
            STAKING_BANNER.ordinal -> StakingBannerViewHolder.create(stakingBaseBannerListener, parent)
            CARD_BANNER.ordinal -> CardsBannerViewHolder.create(cardBaseBannerListener, parent)
            GENERIC_BANNER.ordinal -> GenericBannerViewHolder.create(baseBannerListener, parent)
            BACKUP_BANNER.ordinal -> BackupBannerViewHolder.create(parent, backupBannerListener)
            QUICK_ACTIONS.ordinal -> AccountsQuickActionsViewHolder.create(parent, accountAdapterListener)
            else -> throw Exception("$logTag: Item View Type is Unknown.")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseAccountListItem>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.ordinal
    }

    interface AccountAdapterListener : AccountsQuickActionsListener, HeaderViewHolder.OptionsClickListener {
        fun onSucceedAccountClick(publicKey: String)
        fun onFailedAccountClick(publicKey: String)
        fun onAccountItemLongPressed(publicKey: String)
        fun onBannerCloseButtonClick(bannerId: Long)
        fun onBannerActionButtonClick(url: String, bannerType: BannerType)
        fun onBackupBannerActionButtonClick()
    }

    companion object {
        private val logTag = AccountsAdapter::class.java.simpleName
    }
}
