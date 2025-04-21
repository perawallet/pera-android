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

package com.algorand.android.modules.accounts.ui.viewmodel

import com.algorand.android.R
import com.algorand.android.banner.domain.model.BaseBanner
import com.algorand.android.mapper.AccountPreviewMapper
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreviewByType
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.ui.mapper.BaseAccountListItemBannerItemMapper
import com.algorand.android.modules.accounts.ui.model.AccountPreview
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.modules.accounts.ui.model.PortfolioItemProcessorData
import com.algorand.android.modules.accountsorting.ui.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.notification.domain.usecase.NotificationStatusUseCase
import com.algorand.android.modules.swap.reddot.domain.usecase.GetSwapFeatureRedDotVisibilityUseCase
import com.algorand.android.utils.formatAsCurrency
import com.algorand.wallet.account.custom.domain.usecase.GetAccountsCustomInfo
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.remoteconfig.domain.usecase.IMMERSVE_BUTTON_TOGGLE
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.remoteconfig.domain.usecase.STAKING_BUTTON_TOGGLE
import java.math.BigDecimal
import javax.inject.Inject

@Suppress("LongParameterList")
class AccountPreviewProcessor @Inject constructor(
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getSwapFeatureRedDotVisibility: GetSwapFeatureRedDotVisibilityUseCase,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val portfolioItemProcessor: AccountsPreviewPortfolioItemProcessor,
    private val notificationStatusUseCase: NotificationStatusUseCase,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val accountPreviewMapper: AccountPreviewMapper,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType,
    private val bannerItemMapper: BaseAccountListItemBannerItemMapper,
    private val backupBannerProcessor: AccountsPreviewBackupBannerProcessor,
    private val getLocalAccounts: GetLocalAccounts,
    private val getAccountsCustomInfo: GetAccountsCustomInfo,
    private val getAccountRegistrationType: GetAccountRegistrationType,
    private val sortAccountsBySortingPreference: SortAccountsBySortingPreference
) {

    suspend fun prepareAccountPreview(
        localAccounts: List<LocalAccount>,
        accountLites: Map<String, AccountLite>,
        banner: BaseBanner?,
        assetInboxCount: Int
    ): AccountPreview {
        val accountList = mutableListOf<BaseAccountListItem>()
        var primaryAccountValue = BigDecimal.ZERO
        var secondaryAccountValue = BigDecimal.ZERO
        val currencyData = getCurrencyData()

        insertQuickActionsItem(accountList)

        backupBannerProcessor.getBackupBanner(accountLites)?.also { backupBanner ->
            accountList.add(BANNER_ITEM_INDEX, backupBanner)
        }
        bannerItemMapper.map(banner)?.let { bannerItem ->
            accountList.add(BANNER_ITEM_INDEX, bannerItem)
        }

        val accountItems = sortAccountsBySortingPreference.sortAccountLites(accountLites).map { (_, accountLite) ->
            if (accountLite.cachedInfo != null) {
                primaryAccountValue += accountLite.cachedInfo.primaryAccountValue
                secondaryAccountValue += accountLite.cachedInfo.secondaryAccountValue
                getAccountSuccessItem(accountLite, currencyData)
            } else {
                getAccountErrorItem(accountLite)
            }
        }

        if (accountItems.isNotEmpty()) {
            accountList.add(BaseAccountListItem.HeaderItem(R.string.accounts))
            accountList.addAll(accountItems)
        }

        val portfolioData = getPortfolioItemProcessorData(primaryAccountValue, secondaryAccountValue, currencyData)
        return accountPreviewMapper.getSuccessAccountPreview(
            accountListItems = accountList,
            portfolioValueItem = portfolioItemProcessor.getPortfolioItem(portfolioData, localAccounts),
            hasNewNotification = notificationStatusUseCase.hasNewNotification(),
            assetInboxCount = assetInboxCount
        )
    }

    suspend fun createAccountErrorItemList(): List<BaseAccountListItem> {
        val localAccounts = getLocalAccounts()
        val customInfos = getAccountsCustomInfo(localAccounts.map { it.algoAddress })
        val accountErrorItems = localAccounts.map { localAccount ->
            val customInfo = customInfos[localAccount.algoAddress]
            val displayName = getAccountDisplayName(localAccount.algoAddress, customInfo?.customName, type = null)
            val registrationType = getAccountRegistrationType(localAccount)
            BaseAccountListItem.AccountErrorItem(
                address = localAccount.algoAddress,
                primaryDisplayName = displayName.primaryDisplayName,
                secondaryDisplayName = displayName.secondaryDisplayName.orEmpty(),
                accountIconDrawablePreview = getAccountIconDrawablePreviewByType(registrationType),
                canCopyable = registrationType != AccountRegistrationType.NoAuth
            )
        }

        if (accountErrorItems.isEmpty()) return emptyList()
        return mutableListOf<BaseAccountListItem>().apply {
            add(BaseAccountListItem.HeaderItem(R.string.accounts))
            insertQuickActionsItem(this)
        }
    }

    private suspend fun getAccountSuccessItem(
        accountLite: AccountLite,
        currencyData: CurrencyData
    ): BaseAccountListItem.AccountSuccessItem {
        val address = accountLite.address
        val displayName = getAccountDisplayName(address, accountLite.customName, accountLite.cachedInfo?.type)
        return BaseAccountListItem.AccountSuccessItem(
            address = address,
            primaryDisplayName = displayName.primaryDisplayName,
            secondaryDisplayName = displayName.secondaryDisplayName.orEmpty(),
            accountIconDrawablePreview = getAccountIconDrawablePreviewByType(accountLite.cachedInfo!!.type),
            formattedPrimaryValue = accountLite.cachedInfo.getFormattedPrimaryValue(currencyData),
            formattedSecondaryValue = accountLite.cachedInfo.getFormattedSecondaryValue(currencyData),
            canCopyable = accountLite.cachedInfo.type != AccountType.NoAuth,
            startSmallIconResource = accountLite.getStartSmallIconResource()
        )
    }

    private fun AccountLite.CachedInfo.getFormattedPrimaryValue(currencyData: CurrencyData): String {
        return primaryAccountValue.formatAsCurrency(
            symbol = currencyData.primaryCurrencySymbol,
            isCompact = true,
            isFiat = !currencyData.isPrimaryCurrencyAlgo
        )
    }

    private fun AccountLite.CachedInfo.getFormattedSecondaryValue(currencyData: CurrencyData): String {
        return secondaryAccountValue.formatAsCurrency(
            symbol = currencyData.secondaryCurrencySymbol,
            isCompact = true,
            isFiat = !currencyData.isSecondaryCurrencyAlgo
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
        val displayName = getAccountDisplayName(address, accountLite.customName, accountLite.cachedInfo?.type)
        return BaseAccountListItem.AccountErrorItem(
            address = address,
            primaryDisplayName = displayName.primaryDisplayName,
            secondaryDisplayName = displayName.secondaryDisplayName.orEmpty(),
            accountIconDrawablePreview = getAccountIconDrawablePreviewByType(accountLite.registrationType),
            canCopyable = accountLite.registrationType != AccountRegistrationType.NoAuth
        )
    }

    private suspend fun insertQuickActionsItem(accountsList: MutableList<BaseAccountListItem>) {
        accountsList.add(
            index = QUICK_ACTIONS_ITEM_INDEX,
            element = BaseAccountListItem.QuickActionsItem(
                isSwapButtonSelected = getSwapFeatureRedDotVisibility.getSwapFeatureRedDotVisibility(),
                isImmersveEnabled = isFeatureToggleEnabled(IMMERSVE_BUTTON_TOGGLE),
                isStakingEnabled = isFeatureToggleEnabled(STAKING_BUTTON_TOGGLE)
            )
        )
    }

    private fun getCurrencyData(): CurrencyData {
        return CurrencyData(
            primaryCurrencySymbol = getPrimaryCurrencySymbol().orEmpty(),
            secondaryCurrencySymbol = getSecondaryCurrencySymbol(),
            isPrimaryCurrencyAlgo = isPrimaryCurrencyAlgo()
        )
    }

    private fun getPortfolioItemProcessorData(
        primaryAccountValue: BigDecimal,
        secondaryAccountValue: BigDecimal,
        currencyData: CurrencyData
    ): PortfolioItemProcessorData {
        return PortfolioItemProcessorData(
            totalPrimaryValue = primaryAccountValue,
            totalSecondaryValue = secondaryAccountValue,
            primaryCurrencySymbol = currencyData.primaryCurrencySymbol,
            secondaryCurrencySymbol = currencyData.secondaryCurrencySymbol
        )
    }

    companion object {
        private const val QUICK_ACTIONS_ITEM_INDEX = 0
        private const val BANNER_ITEM_INDEX = 1
    }
}

private data class CurrencyData(
    val primaryCurrencySymbol: String,
    val secondaryCurrencySymbol: String,
    val isPrimaryCurrencyAlgo: Boolean
) {
    val isSecondaryCurrencyAlgo: Boolean
        get() = !isPrimaryCurrencyAlgo
}
