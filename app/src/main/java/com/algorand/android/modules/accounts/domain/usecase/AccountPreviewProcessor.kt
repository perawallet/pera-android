package com.algorand.android.modules.accounts.domain.usecase

import com.algorand.android.R
import com.algorand.android.accountcore.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.accountcore.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.accountinfo.component.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.android.accountinfo.component.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.android.asb.component.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.android.banner.domain.model.Banner
import com.algorand.android.banner.ui.mapper.BaseBannerItemMapper
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.core.component.domain.model.AccountTotalValue
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValue
import com.algorand.android.core.component.domain.usecase.GetNotBackedUpAccounts
import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.mapper.AccountPreviewMapper
import com.algorand.android.modules.accounts.domain.mapper.AccountListItemMapper
import com.algorand.android.modules.accounts.domain.mapper.PortfolioValueItemMapper
import com.algorand.android.modules.accounts.domain.model.AccountPreview
import com.algorand.android.modules.accounts.domain.model.BaseAccountListItem
import com.algorand.android.modules.accounts.domain.model.BasePortfolioValueItem
import com.algorand.android.modules.notification.domain.usecase.NotificationStatusUseCase
import com.algorand.android.modules.tutorialdialog.data.model.Tutorial
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbolOrName
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.module.swap.component.reddot.domain.usecase.GetSwapFeatureRedDotVisibility
import com.algorand.android.utils.Event
import com.algorand.android.utils.formatAsCurrency
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("LongParameterList")
class AccountPreviewProcessor @Inject constructor(
    private val baseBannerItemMapper: BaseBannerItemMapper,
    private val accountListItemMapper: AccountListItemMapper,
    private val getSwapFeatureRedDotVisibility: GetSwapFeatureRedDotVisibility,
    private val getNotBackedUpAccounts: GetNotBackedUpAccounts,
    private val isThereAnyCachedErrorAccount: IsThereAnyCachedErrorAccount,
    private val isThereAnyCachedSuccessAccount: IsThereAnyCachedSuccessAccount,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val notificationStatusUseCase: NotificationStatusUseCase,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val accountPreviewMapper: AccountPreviewMapper,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val portfolioValueItemMapper: PortfolioValueItemMapper,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val accountItemConfigMapper: AccountItemConfigurationMapper,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getSortedAccountsByPreference: GetSortedAccountsByPreference,
    private val getAccountAsbBackUpStatus: GetAccountAsbBackUpStatus
) {

    suspend fun prepareAccountPreview(
        banner: Banner?,
        isTestnetBadgeVisible: Boolean,
        tutorial: Tutorial?,
        notificationPermissionEvent: Event<Unit>?
    ): AccountPreview {
        return withContext(Dispatchers.Default) {
            var primaryAccountValue = BigDecimal.ZERO
            var secondaryAccountValue = BigDecimal.ZERO

            val baseAccountListItems = getBaseAccountListItems(onAccountValueCalculated = {
                primaryAccountValue += it.primaryAccountValue
                secondaryAccountValue += it.secondaryAccountValue
            }).apply {
                val bannerItem = getBannerItemOrNull(baseBanner = banner)
                // TODO: Remove test banner
                insertQuickActionsItem(this)
                getBackupBannerOrNull()?.also { backupBanner ->
                    add(BANNER_ITEM_INDEX, backupBanner)
                }
                if (bannerItem != null) add(BANNER_ITEM_INDEX, bannerItem)
            }
            val portfolioValueItem = if (!isThereAnyCachedErrorAccount(excludeNoAuthAccounts = true)) {
                getPortfolioValueSuccessItem(primaryAccountValue, secondaryAccountValue)
            } else if (isThereAnyCachedSuccessAccount(excludeNoAuthAccounts = true)) {
                getPortfolioValuePartialErrorItem(primaryAccountValue, secondaryAccountValue)
            } else {
                portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
            }
            val giftCardsTutorialDisplayEvent = with(tutorial) {
                if (this == Tutorial.GIFT_CARDS) Event(id) else null
            }
            val swapTutorialDisplayEvent = with(tutorial) {
                if (this == Tutorial.SWAP) Event(id) else null
            }
            val accountAddressCopyDisplayEvent = with(tutorial) {
                if (this == Tutorial.ACCOUNT_ADDRESS_COPY) Event(id) else null
            }
            val hasNewNotification = notificationStatusUseCase.hasNewNotification()
            accountPreviewMapper.getSuccessAccountPreview(
                accountListItems = baseAccountListItems,
                isTestnetBadgeVisible = isTestnetBadgeVisible,
                portfolioValueItem = portfolioValueItem,
                hasNewNotification = hasNewNotification,
                onSwapTutorialDisplayEvent = swapTutorialDisplayEvent,
                onAccountAddressCopyTutorialDisplayEvent = accountAddressCopyDisplayEvent,
                onGiftCardsTutorialDisplayEvent = giftCardsTutorialDisplayEvent,
                notificationPermissionEvent = notificationPermissionEvent
            )
        }
    }

    suspend fun createAccountErrorItemList(): List<BaseAccountListItem> {
        val sortedAccountListItems = getSortedAccountsByPreference(
            onLoadedAccountConfiguration = {
                accountItemConfigMapper(
                    accountAddress = address,
                    accountDisplayName = getAccountDisplayName(address),
                    accountIconDrawablePreview = getAccountIconDrawablePreview(address),
                    accountType = accountType,
                    showWarningIcon = true
                )
            },
            onFailedAccountConfiguration = {
                accountItemConfigMapper(
                    accountAddress = this,
                    accountDisplayName = getAccountDisplayName(this),
                    accountIconDrawablePreview = getAccountIconDrawablePreview(this),
                    accountType = null,
                    showWarningIcon = true
                )
            }
        )
        if (sortedAccountListItems.isEmpty()) return emptyList()
        return mutableListOf<BaseAccountListItem>().apply {
            add(BaseAccountListItem.HeaderItem(R.string.accounts))
            val baseAccountList = sortedAccountListItems.map { accountListItem ->
                if (accountListItem.itemConfiguration.showWarning == true) {
                    accountListItemMapper.mapToErrorAccountItem(
                        accountListItem = accountListItem,
                        canCopyable = accountListItem.itemConfiguration.accountType != AccountType.NoAuth
                    )
                } else {
                    accountListItemMapper.mapToAccountItem(
                        accountListItem = accountListItem,
                        canCopyable = accountListItem.itemConfiguration.accountType != AccountType.NoAuth
                    )
                }
            }
            addAll(baseAccountList)
            insertQuickActionsItem(this)
        }
    }

    private fun getPortfolioValueSuccessItem(
        primaryAccountValue: BigDecimal,
        secondaryAccountValue: BigDecimal
    ): BasePortfolioValueItem.SuccessPortfolioValueItem {
        return portfolioValueItemMapper.mapToPortfolioValuesSuccessItem(
            formattedPrimaryAccountValue = primaryAccountValue.formatAsCurrency(getPrimaryCurrencySymbolOrName()),
            formattedSecondaryAccountValue = secondaryAccountValue.formatAsCurrency(getSecondaryCurrencySymbol())
        )
    }

    private fun getPortfolioValuePartialErrorItem(
        primaryAccountValue: BigDecimal,
        secondaryAccountValue: BigDecimal
    ): BasePortfolioValueItem.PartialErrorPortfolioValueItem {
        return portfolioValueItemMapper.mapToPortfolioValuesPartialErrorItem(
            formattedPrimaryAccountValue = primaryAccountValue.formatAsCurrency(getPrimaryCurrencySymbolOrName()),
            formattedSecondaryAccountValue = secondaryAccountValue.formatAsCurrency(getSecondaryCurrencySymbol())
        )
    }

    private suspend fun getBaseAccountListItems(
        onAccountValueCalculated: (AccountTotalValue) -> Unit
    ): MutableList<BaseAccountListItem> {
        val isPrimaryCurrencyAlgo = isPrimaryCurrencyAlgo()
        val isSecondaryCurrencyAlgo = !isPrimaryCurrencyAlgo
        val sortedAccountListItems = getSortedAccountsByPreference(
            onLoadedAccountConfiguration = {
                val accountBalance = getAccountTotalValue(address, true).also { accountValue ->
                    if (accountType != AccountType.NoAuth) {
                        onAccountValueCalculated.invoke(accountValue)
                    }
                }
                val isAccountBackedUp = getAccountAsbBackUpStatus(address)
                accountItemConfigMapper(
                    accountAddress = address,
                    accountDisplayName = getAccountDisplayName(address),
                    accountIconDrawablePreview = getAccountIconDrawablePreview(address),
                    accountType = accountType,
                    accountPrimaryValueText = accountBalance.primaryAccountValue.formatAsCurrency(
                        symbol = getPrimaryCurrencySymbol().orEmpty(),
                        isCompact = true,
                        isFiat = !isPrimaryCurrencyAlgo
                    ),
                    accountSecondaryValueText = accountBalance.secondaryAccountValue.formatAsCurrency(
                        symbol = getSecondaryCurrencySymbol(),
                        isCompact = true,
                        isFiat = !isSecondaryCurrencyAlgo
                    ),
                    accountPrimaryValue = accountBalance.primaryAccountValue,
                    accountSecondaryValue = accountBalance.secondaryAccountValue,
                    startSmallIconResource = if (isAccountBackedUp) null else R.drawable.ic_error_negative
                )
            }, onFailedAccountConfiguration = {
                accountItemConfigMapper(
                    accountAddress = this,
                    accountDisplayName = getAccountDisplayName(this),
                    accountIconDrawablePreview = getAccountIconDrawablePreview(this),
                    showWarningIcon = true
                )
            }
        )
        val baseAccountList = sortedAccountListItems.map { accountListItem ->
            if (accountListItem.itemConfiguration.showWarning == true) {
                accountListItemMapper.mapToErrorAccountItem(
                    accountListItem = accountListItem,
                    canCopyable = accountListItem.itemConfiguration.accountType != AccountType.NoAuth
                )
            } else {
                accountListItemMapper.mapToAccountItem(
                    accountListItem = accountListItem,
                    canCopyable = accountListItem.itemConfiguration.accountType != AccountType.NoAuth
                )
            }
        }
        return mutableListOf<BaseAccountListItem>().apply {
            if (baseAccountList.isNotEmpty()) {
                add(BaseAccountListItem.HeaderItem(R.string.accounts))
                addAll(baseAccountList)
            }
        }
    }

    private suspend fun insertQuickActionsItem(accountsList: MutableList<BaseAccountListItem>) {
        accountsList.add(
            index = QUICK_ACTIONS_ITEM_INDEX,
            element = accountListItemMapper.mapToQuickActionsItem(
                isSwapButtonSelected = getSwapFeatureRedDotVisibility()
            )
        )
    }

    private fun getBannerItemOrNull(baseBanner: Banner?): BaseAccountListItem.BaseBannerItem? {
        return baseBanner?.let { banner ->
            val isButtonVisible = !banner.buttonTitle.isNullOrBlank() && !banner.buttonUrl.isNullOrBlank()
            val isTitleVisible = !banner.title.isNullOrBlank()
            val isDescriptionVisible = !banner.description.isNullOrBlank()
            with(baseBannerItemMapper) {
                when (banner) {
                    is Banner.Governance -> {
                        mapToGovernanceBannerItem(banner, isButtonVisible, isTitleVisible, isDescriptionVisible)
                    }

                    is Banner.Generic -> {
                        mapToGenericBannerItem(banner, isButtonVisible, isTitleVisible, isDescriptionVisible)
                    }
                }
            }
        }
    }

    private suspend fun getBackupBannerOrNull(): BaseAccountListItem.BackupBannerItem? {
        val accounts = getNotBackedUpAccounts()
        return if (accounts.isNotEmpty()) {
            BaseAccountListItem.BackupBannerItem(accounts)
        } else {
            null
        }
    }

    companion object {
        private const val QUICK_ACTIONS_ITEM_INDEX = 0
        private const val BANNER_ITEM_INDEX = 1
    }
}
