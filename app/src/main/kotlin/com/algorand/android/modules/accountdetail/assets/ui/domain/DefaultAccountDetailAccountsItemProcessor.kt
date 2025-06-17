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

package com.algorand.android.modules.accountdetail.assets.ui.domain

import com.algorand.android.R
import com.algorand.android.modules.accountdetail.assets.ui.mapper.AccountDetailAssetItemMapper
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.AccountPortfolioItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.RequiredMinimumBalanceItem
import com.algorand.android.modules.accountdetail.assets.ui.model.QuickActionItem
import com.algorand.android.modules.accountdetail.assets.ui.model.QuickActionItem.AssetInbox
import com.algorand.android.modules.accountdetail.assets.ui.model.QuickActionItem.AssetInboxActive
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite.CachedInfo
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.modules.swap.reddot.domain.usecase.GetSwapFeatureRedDotVisibilityUseCase
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.android.ui.common.amount.mapper.AmountRendererTypeMapper
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoDisplayString
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequest
import com.algorand.wallet.privacy.domain.model.PrivacyMode
import com.algorand.wallet.privacy.domain.usecase.GetPrivacyModeFlow
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class DefaultAccountDetailAccountsItemProcessor @Inject constructor(
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val getPrivacyModeFlow: GetPrivacyModeFlow,
    private val amountRendererTypeMapper: AmountRendererTypeMapper,
    private val getAssetInboxRequest: GetAssetInboxRequest,
    private val getSwapFeatureRedDotVisibility: GetSwapFeatureRedDotVisibilityUseCase,
    private val accountDetailAssetItemMapper: AccountDetailAssetItemMapper,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer
) : AccountDetailAccountsItemProcessor {

    override fun getAccountDetailsItemsFlow(address: String, query: String?): Flow<List<AccountDetailAccountsItem>> {
        return combine(getAccountLiteCacheFlow(), getPrivacyModeFlow()) { accountLiteCacheStatus, privacyMode ->
            val accountLite = (accountLiteCacheStatus as? AccountLiteCacheStatus.Data)
                ?.accountLites
                ?.get(address)
            if (accountLite?.cachedInfo == null) return@combine emptyList()
            getAccountDetailAccountItems(query, accountLite, accountLite.cachedInfo, privacyMode)
        }.distinctUntilChanged()
    }

    private suspend fun getAccountDetailAccountItems(
        query: String?,
        accountLite: AccountLite,
        cachedInfo: CachedInfo,
        privacyMode: PrivacyMode
    ): List<AccountDetailAccountsItem> {
        return mutableListOf<AccountDetailAccountsItem>().apply {
            add(createAccountPortfolioItem(cachedInfo, privacyMode))
            add(createRequiredMinimumBalanceItem(cachedInfo))
            add(createQuickActionItemList(accountLite))
            if (!accountLite.isBackedUp && cachedInfo.primaryAccountValue > BigDecimal.ZERO) {
                add(accountDetailAssetItemMapper.mapToBackupWarningItem(isBackedUp = false))
            }
            val hasAccountAuthority = cachedInfo.type.canSignTransaction()
            add(accountDetailAssetItemMapper.mapToTitleItem(R.string.assets, hasAccountAuthority))
            add(accountDetailAssetItemMapper.mapToSearchViewItem(query.orEmpty()))
        }
    }

    private suspend fun hasInboxItem(address: String): Boolean {
        val addressRequest = getAssetInboxRequest(address) ?: return false
        return addressRequest.requestCount > 0
    }

    private fun createAccountPortfolioItem(cachedInfo: CachedInfo, privacyMode: PrivacyMode): AccountPortfolioItem {
        val amountRenderType = amountRendererTypeMapper(privacyMode)
        val primaryAmount = PeraAmount(cachedInfo.primaryAccountValue)
        val secondaryAmount = PeraAmount(cachedInfo.secondaryAccountValue)
        return AccountPortfolioItem(
            getCompactPrimaryAmountRenderer(primaryAmount, amountRenderType).getDisplayValue(),
            getCompactSecondaryAmountRenderer(secondaryAmount, amountRenderType).getDisplayValue()
        )
    }

    private fun createRequiredMinimumBalanceItem(cachedInfo: CachedInfo): RequiredMinimumBalanceItem {
        val minBalance = cachedInfo.minRequiredBalance
        val formattedRequiredMinimumBalance = minBalance.formatAsAlgoDisplayString().formatAsAlgoAmount()
        return accountDetailAssetItemMapper.mapToRequiredMinimumBalanceItem(formattedRequiredMinimumBalance)
    }

    private suspend fun createQuickActionItemList(
        accountLite: AccountLite
    ): AccountDetailAccountsItem.QuickActionItemContainer {
        val quickActionItemList = mutableListOf<QuickActionItem>().apply {
            val isWatchAccount = accountLite.cachedInfo?.type == AccountType.NoAuth
            if (isWatchAccount) {
                addAll(getWatchAccountQuickActionItems())
            } else {
                addAll(getAuthAccountQuickActionItem(accountLite.address))
            }
            add(QuickActionItem.MoreButton)
        }
        return accountDetailAssetItemMapper.mapToQuickActionItemContainer(quickActionItemList)
    }

    private fun getWatchAccountQuickActionItems(): List<QuickActionItem> {
        return listOf(QuickActionItem.CopyAddressButton, QuickActionItem.ShowAddressButton)
    }

    private suspend fun getAuthAccountQuickActionItem(address: String): List<QuickActionItem> {
        return mutableListOf<QuickActionItem>().apply {
            val isSwapSelected = getSwapFeatureRedDotVisibility.getSwapFeatureRedDotVisibility()
            val inboxItem = if (hasInboxItem(address)) AssetInboxActive else AssetInbox
            add(accountDetailAssetItemMapper.mapToSwapQuickActionItem(isSwapSelected))
            add(QuickActionItem.BuyAlgoButton)
            add(inboxItem)
        }
    }
}
