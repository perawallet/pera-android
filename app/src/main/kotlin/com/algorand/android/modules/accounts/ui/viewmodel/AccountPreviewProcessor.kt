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

package com.algorand.android.modules.accounts.ui.viewmodel

import com.algorand.android.R
import com.algorand.android.mapper.AccountPreviewMapper
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreviewByType
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.ui.mapper.BaseAccountListItemBannerItemMapper
import com.algorand.android.modules.accounts.ui.model.AccountPreview
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.modules.accountsorting.ui.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.modules.notification.domain.usecase.NotificationStatusUseCase
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.android.ui.common.amount.mapper.AmountRendererTypeMapper
import com.algorand.wallet.account.custom.domain.usecase.GetAccountsCustomInfo
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.banner.domain.model.Banner
import com.algorand.wallet.privacy.domain.model.PrivacyMode
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import java.math.BigDecimal
import javax.inject.Inject

@Suppress("LongParameterList")
class AccountPreviewProcessor @Inject constructor(
    private val getTotalInboxCount: GetTotalInboxCount,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val portfolioItemProcessor: AccountsPreviewPortfolioItemProcessor,
    private val notificationStatusUseCase: NotificationStatusUseCase,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val accountPreviewMapper: AccountPreviewMapper,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val bannerItemMapper: BaseAccountListItemBannerItemMapper,
    private val getAccountsCustomInfo: GetAccountsCustomInfo,
    private val getAccountType: GetAccountType,
    private val sortAccountsBySortingPreference: SortAccountsBySortingPreference,
    private val amountRendererTypeMapper: AmountRendererTypeMapper,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer,
    private val getLocalAccount: GetLocalAccount
) {

    suspend fun prepareAccountPreview(
        localAccounts: List<LocalAccount>,
        accountLites: Map<String, AccountLite>,
        banner: Banner?,
        privacyMode: PrivacyMode,
        spotBanners: List<SpotBanner>
    ): AccountPreview {
        val assetInboxCount = getTotalInboxCount()
        val amountRenderType = amountRendererTypeMapper(privacyMode)
        val accountList = mutableListOf<BaseAccountListItem>()

        accountList.add(BaseAccountListItem.WalletChartItem)

        insertQuickActionsItem(accountList)

        bannerItemMapper.map(banner)?.let { bannerItem ->
            accountList.add(bannerItem)
        }

        if (spotBanners.isNotEmpty()) {
            accountList.add(BaseAccountListItem.SpotBannerItem(spotBanners))
        }

        val accountItems = getAccountItems(accountLites, amountRenderType)

        if (accountItems.isNotEmpty()) {
            accountList.add(BaseAccountListItem.HeaderItem(R.string.accounts))
            accountList.addAll(accountItems)
        }

        val portfolio = portfolioItemProcessor
            .getPortfolioItem(accountLites, amountRenderType, localAccounts, privacyMode)
        return accountPreviewMapper.getSuccessAccountPreview(
            accountListItems = accountList,
            portfolioValueItem = portfolio,
            hasNewNotification = notificationStatusUseCase.hasNewNotification(),
            assetInboxCount = assetInboxCount
        )
    }

    private suspend fun getAccountItems(
        accountLites: Map<String, AccountLite>,
        rendererType: AmountRenderer.RenderType
    ): List<BaseAccountListItem> {
        return sortAccountsBySortingPreference.sortAccountLites(accountLites)
            .map { (_, accountLite) ->
                if (accountLite.cachedInfo != null) {
                    getAccountSuccessItem(accountLite, accountLite.cachedInfo, rendererType)
                } else {
                    getAccountErrorItem(accountLite)
                }
            }
    }

    suspend fun createAccountErrorItemList(localAccounts: List<LocalAccount>): List<BaseAccountListItem> {
        val customInfos = getAccountsCustomInfo(localAccounts.map { it.algoAddress })
        val accountErrorItems = localAccounts
            .mapNotNull { localAccount ->
                val accountType = getAccountType(localAccount.algoAddress) ?: return@mapNotNull null
                val customInfo = customInfos[localAccount.algoAddress]
                val displayName = getAccountDisplayName(
                    address = localAccount.algoAddress,
                    name = customInfo?.customName,
                    type = accountType
                )
                BaseAccountListItem.AccountErrorItem(
                    address = localAccount.algoAddress,
                    primaryDisplayName = displayName.primaryDisplayName,
                    secondaryDisplayName = displayName.secondaryDisplayName.orEmpty(),
                    accountIconDrawablePreview = getAccountIconDrawablePreviewByType(accountType),
                    canCopyable = accountType != AccountType.NoAuth
                )
            }

        if (accountErrorItems.isEmpty()) return emptyList()
        return mutableListOf<BaseAccountListItem>().apply {
            insertQuickActionsItem(this)
            add(BaseAccountListItem.HeaderItem(R.string.accounts))
        }
    }

    private suspend fun getAccountSuccessItem(
        accountLite: AccountLite,
        cachedInfo: AccountLite.CachedInfo,
        amountRenderType: AmountRenderer.RenderType
    ): BaseAccountListItem.AccountSuccessItem {
        val address = accountLite.address
        val displayName = getAccountDisplayName(accountLite)
        val primaryAmount = PeraAmount(cachedInfo.primaryAccountValue)
        val secondaryAmount = PeraAmount(cachedInfo.secondaryAccountValue)
        val participantCount = if (cachedInfo.type == AccountType.Joint) {
            (getLocalAccount(address) as? LocalAccount.Joint)?.participantAddresses?.size?.takeIf { it > 0 }
        } else {
            null
        }
        return BaseAccountListItem.AccountSuccessItem(
            address = address,
            primaryDisplayName = displayName.primaryDisplayName,
            secondaryDisplayName = displayName.secondaryDisplayName.orEmpty(),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountLite),
            formattedPrimaryValue = getCompactPrimaryAmountRenderer(primaryAmount, amountRenderType),
            formattedSecondaryValue = getCompactSecondaryAmountRenderer(secondaryAmount, amountRenderType),
            canCopyable = cachedInfo.type != AccountType.NoAuth,
            startSmallIconResource = accountLite.getStartSmallIconResource(),
            participantCount = participantCount
        )
    }

    private fun AccountLite.getStartSmallIconResource(): Int? {
        val safePrimaryValue = cachedInfo?.primaryAccountValue ?: BigDecimal.ZERO
        return if (!isBackedUp && safePrimaryValue > BigDecimal.ZERO) {
            R.drawable.ic_error_negative
        } else {
            null
        }
    }

    private suspend fun getAccountErrorItem(accountLite: AccountLite): BaseAccountListItem.AccountErrorItem {
        val address = accountLite.address
        val accountType = getAccountType(address) ?: AccountType.NoAuth
        val displayName = getAccountDisplayName(accountLite)
        return BaseAccountListItem.AccountErrorItem(
            address = address,
            primaryDisplayName = displayName.primaryDisplayName,
            secondaryDisplayName = displayName.secondaryDisplayName.orEmpty(),
            accountIconDrawablePreview = getAccountIconDrawablePreviewByType(accountType),
            canCopyable = accountType != AccountType.NoAuth
        )
    }

    private fun insertQuickActionsItem(accountsList: MutableList<BaseAccountListItem>) {
        accountsList.add(
            BaseAccountListItem.QuickActionsItem(
                isStakingEnabled = isFeatureToggleEnabled(FeatureToggle.STAKING.key),
                isXoSwapEnabled = isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key)
            )
        )
    }
}
