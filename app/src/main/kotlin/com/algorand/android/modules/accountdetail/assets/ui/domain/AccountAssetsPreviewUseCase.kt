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

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.algorand.android.R
import com.algorand.android.modules.accountdetail.assets.ui.mapper.AccountDetailAssetItemMapper
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem.AccountPortfolioItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.QuickActionItem
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.modules.accountsorting.domain.usecase.GetAssetCollectibleLiteSortType
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldDisplayNFTInAssetsPreferenceUseCase
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldDisplayOptedInNFTInAssetsPreferenceUseCase
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldHideZeroBalanceAssetsPreferenceUseCase
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencyName
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbol
import com.algorand.android.modules.swap.reddot.domain.usecase.GetSwapFeatureRedDotVisibilityUseCase
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoDisplayString
import com.algorand.android.utils.formatAsCurrency
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequest
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.usecase.GetAssetCollectibleLitesFlow
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@SuppressWarnings("LongParameterList")
class AccountAssetsPreviewUseCase @Inject constructor(
    private val accountDetailAssetItemMapper: AccountDetailAssetItemMapper,
    private val getSwapFeatureRedDotVisibility: GetSwapFeatureRedDotVisibilityUseCase,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getPrimaryCurrencyName: GetPrimaryCurrencyName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val getAssetInboxRequest: GetAssetInboxRequest,
    private val getAssetCollectibleLitesFlow: GetAssetCollectibleLitesFlow,
    private val shouldHideZeroBalanceAssetsPreferenceUseCase: ShouldHideZeroBalanceAssetsPreferenceUseCase,
    private val shouldDisplayNFTInAssetsPreferenceUseCase: ShouldDisplayNFTInAssetsPreferenceUseCase,
    private val shouldDisplayOptedInNFTInAssetsPreferenceUseCase: ShouldDisplayOptedInNFTInAssetsPreferenceUseCase,
    private val getAssetCollectibleLiteSortType: GetAssetCollectibleLiteSortType
) {

    fun getAccountDetailsItemsFlow(address: String, query: String?): Flow<List<AccountDetailAccountsItem>> {
        return getAccountLiteCacheFlow().map { accountLiteCacheStatus ->
            val accountLite = (accountLiteCacheStatus as? AccountLiteCacheStatus.Data)
                ?.accountLites
                ?.get(address)
            if (accountLite?.cachedInfo == null) return@map emptyList()
            getAccountDetailAccountItems(query, accountLite, accountLite.cachedInfo)
        }.distinctUntilChanged()
    }

    suspend fun getAssetsPagingFlow(
        scope: CoroutineScope,
        address: String,
        query: String
    ): Flow<PagingData<AccountDetailAssetsItem>> {
        val assetCollectibleLiteQuery = getPaginationQuery(address, query)
        return getAssetCollectibleLitesFlow(assetCollectibleLiteQuery).cachedIn(scope).map { pagingData ->
            pagingData.map { assetLite ->
                accountDetailAssetItemMapper.mapToAssetListItem(assetLite, false)
            }.insertSeparators { assetItem1: AccountDetailAssetsItem?, assetItem2: AccountDetailAssetsItem? ->
                if (assetItem1 == null && assetItem2 == null) {
                    accountDetailAssetItemMapper.mapToNoAssetFoundViewItem()
                } else {
                    null
                }
            }
        }.distinctUntilChanged()
    }

    private suspend fun getPaginationQuery(address: String, searchKeyword: String): AssetCollectibleLiteQuery {
        return AssetCollectibleLiteQuery(
            addresses = listOf(address),
            searchKeyword = searchKeyword,
            filterOutZeroAmount = shouldHideZeroBalanceAssetsPreferenceUseCase(),
            filterOutCollectibles = !shouldDisplayNFTInAssetsPreferenceUseCase(),
            filterOutCollectiblesWithZeroAmount = !shouldDisplayOptedInNFTInAssetsPreferenceUseCase(),
            sortType = getAssetCollectibleLiteSortType()
        )
    }

    private suspend fun hasInboxItem(address: String): Boolean {
        val addressRequest = getAssetInboxRequest(address) ?: return false
        return addressRequest.requestCount > 0
    }

    private suspend fun getAccountDetailAccountItems(
        query: String?,
        accountLite: AccountLite,
        cachedInfo: AccountLite.CachedInfo
    ): List<AccountDetailAccountsItem> {
        val isWatchAccount = cachedInfo.type == AccountType.NoAuth
        val hasInboxItem = hasInboxItem(accountLite.address)
        return mutableListOf<AccountDetailAccountsItem>().apply {
            add(createAccountPortfolioItem(cachedInfo))
            add(createRequiredMinimumBalanceItem(cachedInfo))
            add(createQuickActionItemList(isWatchAccount, hasInboxItem))
            if (!accountLite.isBackedUp && cachedInfo.primaryAccountValue > BigDecimal.ZERO) {
                add(accountDetailAssetItemMapper.mapToBackupWarningItem(isBackedUp = false))
            }
            val hasAccountAuthority = cachedInfo.type.canSignTransaction()
            add(accountDetailAssetItemMapper.mapToTitleItem(R.string.assets, hasAccountAuthority))
            add(accountDetailAssetItemMapper.mapToSearchViewItem(query.orEmpty()))
        }
    }

    private suspend fun createQuickActionItemList(
        isWatchAccount: Boolean,
        hasInboxItem: Boolean
    ): AccountDetailAccountsItem.QuickActionItemContainer {
        val quickActionItemList = mutableListOf<QuickActionItem>().apply {
            if (isWatchAccount) {
                add(QuickActionItem.CopyAddressButton)
                add(QuickActionItem.ShowAddressButton)
            } else {
                val isSwapSelected = getSwapFeatureRedDotVisibility.getSwapFeatureRedDotVisibility()
                val inboxItem = if (hasInboxItem) {
                    QuickActionItem.AssetInboxActive
                } else {
                    QuickActionItem.AssetInbox
                }
                add(accountDetailAssetItemMapper.mapToSwapQuickActionItem(isSwapSelected))
                add(QuickActionItem.BuyAlgoButton)
                add(inboxItem)
            }
            add(QuickActionItem.MoreButton)
        }
        return accountDetailAssetItemMapper.mapToQuickActionItemContainer(quickActionItemList)
    }

    private fun createAccountPortfolioItem(cachedInfo: AccountLite.CachedInfo): AccountPortfolioItem {
        val selectedCurrencySymbol = getPrimaryCurrencySymbol() ?: getPrimaryCurrencyName()
        val secondaryCurrencySymbol = getSecondaryCurrencySymbol()
        val formattedPrimaryAccountValue = cachedInfo.primaryAccountValue.formatAsCurrency(selectedCurrencySymbol)
        val formattedSecondaryAccountValue = cachedInfo.secondaryAccountValue.formatAsCurrency(secondaryCurrencySymbol)
        return AccountPortfolioItem(formattedPrimaryAccountValue, formattedSecondaryAccountValue)
    }

    private fun createRequiredMinimumBalanceItem(
        cachedInfo: AccountLite.CachedInfo
    ): AccountDetailAccountsItem.RequiredMinimumBalanceItem {
        val minBalance = cachedInfo.minRequiredBalance
        val formattedRequiredMinimumBalance = minBalance.formatAsAlgoDisplayString().formatAsAlgoAmount()
        return accountDetailAssetItemMapper.mapToRequiredMinimumBalanceItem(
            formattedRequiredMinimumBalance = formattedRequiredMinimumBalance
        )
    }

    companion object {
        const val QUICK_ACTIONS_INDEX = 2
    }
}
