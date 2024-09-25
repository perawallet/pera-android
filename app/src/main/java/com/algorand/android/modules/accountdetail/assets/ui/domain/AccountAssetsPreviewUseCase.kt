/*
 * Copyright 2022 Pera Wallet, LDA
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
import com.algorand.android.core.component.assetdata.model.AccountAssetData
import com.algorand.android.core.component.assetdata.usecase.GetAccountAssetDataFlow
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.PendingAssetData.AdditionAssetData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.PendingAssetData.BasePendingCollectibleData.PendingAdditionCollectibleData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.PendingAssetData.BasePendingCollectibleData.PendingDeletionCollectibleData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.PendingAssetData.DeletionAssetData
import com.algorand.android.core.component.domain.usecase.GetAccountCollectibleDataFlow
import com.algorand.android.modules.accountdetail.assets.ui.mapper.AccountAssetsPreviewMapper
import com.algorand.android.modules.accountdetail.assets.ui.mapper.AccountDetailAssetItemMapper
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountAssetsPreview
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.AccountPortfolioItem
import com.algorand.android.modules.accountdetail.assets.ui.model.QuickActionItem
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldDisplayNFTInAssetsPreferenceUseCase
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldDisplayOptedInNFTInAssetsPreferenceUseCase
import com.algorand.android.modules.assets.filter.domain.usecase.ShouldHideZeroBalanceAssetsPreferenceUseCase
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.modules.sorting.assetsorting.ui.usecase.AssetItemSortUseCase
import com.algorand.android.parity.domain.usecase.GetSelectedCurrencyDetailFlow
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencyName
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.module.swap.component.reddot.domain.usecase.GetSwapFeatureRedDotVisibility
import com.algorand.android.usecase.GetFormattedAccountMinimumBalanceUseCase
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsCurrency
import com.algorand.android.utils.isGreaterThan
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@SuppressWarnings("LongParameterList")
class AccountAssetsPreviewUseCase @Inject constructor(
    private val getAccountAssetDataFlow: GetAccountAssetDataFlow,
    private val getAccountDetail: GetAccountDetail,
    private val accountDetailAssetItemMapper: AccountDetailAssetItemMapper,
    private val getSelectedCurrencyDetailFlow: GetSelectedCurrencyDetailFlow,
    private val assetItemSortUseCase: AssetItemSortUseCase,
    private val getSwapFeatureRedDotVisibility: GetSwapFeatureRedDotVisibility,
    private val getFormattedAccountMinimumBalanceUseCase: GetFormattedAccountMinimumBalanceUseCase,
    private val shouldHideZeroBalanceAssetsPreferenceUseCase: ShouldHideZeroBalanceAssetsPreferenceUseCase,
    private val shouldDisplayNFTInAssetsPreferenceUseCase: ShouldDisplayNFTInAssetsPreferenceUseCase,
    private val shouldDisplayOptedInNFTInAssetsPreferenceUseCase: ShouldDisplayOptedInNFTInAssetsPreferenceUseCase,
    private val getAccountCollectibleDataFlow: GetAccountCollectibleDataFlow,
    private val accountAssetsPreviewMapper: AccountAssetsPreviewMapper,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getPrimaryCurrencyName: GetPrimaryCurrencyName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol
) {

    fun fetchAccountDetail(accountAddress: String, query: String): Flow<AccountAssetsPreview> {
        return combine(
            getAccountAssetDataFlow(accountAddress, true),
            getAccountCollectibleDataFlow(accountAddress),
            getSelectedCurrencyDetailFlow()
        ) { accountAssetData, accountNFTData, _ ->
            var primaryAccountValue = BigDecimal.ZERO
            var secondaryAccountValue = BigDecimal.ZERO
            val assetItemList = createAssetListItems(
                accountAssetData = accountAssetData,
                query = query,
                onCalculationDone = { primaryAssetsValue, secondaryAssetsValue ->
                    primaryAccountValue += primaryAssetsValue
                    secondaryAccountValue += secondaryAssetsValue
                }
            )
            val accountDetail = getAccountDetail(accountAddress)
            val collectibleItemList = createNFTListItems(
                accountNFTData = accountNFTData,
                query = query,
                onCalculationDone = { primaryNFTsValue, secondaryNFTsValue ->
                    primaryAccountValue += primaryNFTsValue
                    secondaryAccountValue += secondaryNFTsValue
                },
                accountType = accountDetail.accountType
            )
            val isWatchAccount = accountDetail.accountType == AccountType.NoAuth
            val accountDetailAssetsItemList = mutableListOf<AccountDetailAssetsItem>().apply {
                val accountPortfolioItem = createAccountPortfolioItem(primaryAccountValue, secondaryAccountValue)
                add(accountPortfolioItem)
                val requiredMinimumBalanceItem = createRequiredMinimumBalanceItem(accountAddress)
                add(requiredMinimumBalanceItem)
                add(createQuickActionItemList(isWatchAccount))
                val hasAccountAuthority = accountDetail.accountType?.canSignTransaction() == true
                val isBackedUp = accountDetail.isBackedUp
                if (!isBackedUp) {
                    add(accountDetailAssetItemMapper.mapToBackupWarningItem(isBackedUp = false))
                }
                add(accountDetailAssetItemMapper.mapToTitleItem(R.string.assets, hasAccountAuthority))
                add(accountDetailAssetItemMapper.mapToSearchViewItem(query))
                addAll(assetItemSortUseCase.sortAssets(assetItemList + collectibleItemList))
                if (assetItemList.isEmpty() && collectibleItemList.isEmpty()) {
                    add(accountDetailAssetItemMapper.mapToNoAssetFoundViewItem())
                }
            }
            accountAssetsPreviewMapper.mapToAccountAssetsPreview(
                accountDetailAssetsItemList = accountDetailAssetsItemList,
                isWatchAccount = isWatchAccount
            )
        }
    }

    private suspend fun createQuickActionItemList(
        isWatchAccount: Boolean
    ): AccountDetailAssetsItem.QuickActionItemContainer {
        val quickActionItemList = mutableListOf<QuickActionItem>().apply {
            if (isWatchAccount) {
                add(QuickActionItem.CopyAddressButton)
                add(QuickActionItem.ShowAddressButton)
            } else {
                val isSwapSelected = getSwapFeatureRedDotVisibility()
                add(accountDetailAssetItemMapper.mapToSwapQuickActionItem(isSwapSelected))
                add(QuickActionItem.BuySellButton)
                add(QuickActionItem.SendButton)
            }
            add(QuickActionItem.MoreButton)
        }
        return accountDetailAssetItemMapper.mapToQuickActionItemContainer(quickActionItemList)
    }

    private suspend fun createAssetListItems(
        accountAssetData: AccountAssetData,
        query: String,
        onCalculationDone: (BigDecimal, BigDecimal) -> Unit
    ): List<AccountDetailAssetsItem.BaseAssetItem> {
        var primaryAssetsValue = BigDecimal.ZERO
        var secondaryAssetsValue = BigDecimal.ZERO
        val eliminatedAssetList = accountAssetData.eliminateAssetsByFilteringPreferences()
        return mutableListOf<AccountDetailAssetsItem.BaseAssetItem>().apply {
            eliminatedAssetList.forEach { accountAssetData ->
                (accountAssetData as? OwnedAssetData)?.let { assetData ->
                    primaryAssetsValue += assetData.parityValueInSelectedCurrency.amountAsCurrency
                    secondaryAssetsValue += assetData.parityValueInSecondaryCurrency.amountAsCurrency
                }
                if (shouldDisplayAsset(accountAssetData, query)) {
                    add(createAssetListItem(accountAssetData) ?: return@forEach)
                }
            }
        }.also { onCalculationDone.invoke(primaryAssetsValue, secondaryAssetsValue) }
    }

    private suspend fun AccountAssetData.eliminateAssetsByFilteringPreferences(): List<BaseAccountAssetData> {
        return if (shouldHideZeroBalanceAssetsPreferenceUseCase()) {
            val visibleAssets = ownedAssetData.filter {
                it.isAlgo || it.amount > BigInteger.ZERO
            }
            mutableListOf<BaseAccountAssetData>().apply {
                addAll(pendingAdditionAssetData)
                addAll(visibleAssets)
            }
        } else {
            ownedAssetData + pendingAdditionAssetData + pendingDeletionAssetData
        }
    }

    private suspend fun createNFTListItems(
        accountNFTData: List<BaseAccountAssetData>,
        query: String,
        onCalculationDone: (BigDecimal, BigDecimal) -> Unit,
        accountType: AccountType?
    ): MutableList<AccountDetailAssetsItem.BaseAssetItem> {
        var primaryNFTsValue = BigDecimal.ZERO
        var secondaryNFTsValue = BigDecimal.ZERO
        val eliminatedNFTList = eliminateNFTsRegardingByFilteringPreferenceIfNeed(accountNFTData)
        return mutableListOf<AccountDetailAssetsItem.BaseAssetItem>().apply {
            eliminatedNFTList.forEach { accountNftData ->
                (accountNftData as? BaseOwnedCollectibleData)?.let { nftData ->
                    primaryNFTsValue += nftData.parityValueInSelectedCurrency.amountAsCurrency
                    secondaryNFTsValue += nftData.parityValueInSecondaryCurrency.amountAsCurrency
                }
                if (shouldDisplayAsset(accountNftData, query)) {
                    add(
                        createNFTListItem(
                            assetData = accountNftData,
                            isHoldingByWatchAccount = accountType == AccountType.NoAuth,
                            nftListingViewType = NFTListingViewType.LINEAR_VERTICAL
                        ) ?: return@forEach
                    )
                }
            }
        }.also { onCalculationDone.invoke(primaryNFTsValue, secondaryNFTsValue) }
    }

    private suspend fun eliminateNFTsRegardingByFilteringPreferenceIfNeed(
        accountNFTData: List<BaseAccountAssetData>
    ): List<BaseAccountAssetData> {
        val shouldDisplayNFTInAssetsPreference = shouldDisplayNFTInAssetsPreferenceUseCase()
        val shouldDisplayOptedInNFTInAssetsPreference = shouldDisplayOptedInNFTInAssetsPreferenceUseCase()
        return when {
            shouldDisplayNFTInAssetsPreference && !shouldDisplayOptedInNFTInAssetsPreference -> {
                accountNFTData.filter { it is BaseOwnedCollectibleData && it.isOwnedByTheUser }
            }

            shouldDisplayNFTInAssetsPreference -> accountNFTData
            else -> emptyList()
        }
    }

    private fun shouldDisplayAsset(asset: BaseAccountAssetData, query: String): Boolean {
        val trimmedQuery = query.trim()
        with(asset) {
            return id.toString().contains(trimmedQuery, ignoreCase = true) ||
                shortName?.contains(trimmedQuery, ignoreCase = true) == true ||
                name?.contains(trimmedQuery, ignoreCase = true) == true
        }
    }

    private fun createAccountPortfolioItem(
        primaryAccountValue: BigDecimal,
        secondaryAccountValue: BigDecimal
    ): AccountPortfolioItem {
        val selectedCurrencySymbol = getPrimaryCurrencySymbol() ?: getPrimaryCurrencyName()
        val secondaryCurrencySymbol = getSecondaryCurrencySymbol()
        val formattedPrimaryAccountValue = primaryAccountValue.formatAsCurrency(selectedCurrencySymbol)
        val formattedSecondaryAccountValue = secondaryAccountValue.formatAsCurrency(secondaryCurrencySymbol)
        return AccountPortfolioItem(formattedPrimaryAccountValue, formattedSecondaryAccountValue)
    }

    private suspend fun createRequiredMinimumBalanceItem(
        accountAddress: String
    ): AccountDetailAssetsItem.RequiredMinimumBalanceItem {
        val accountMinimumBalance = getFormattedAccountMinimumBalanceUseCase.getFormattedAccountMinimumBalance(
            accountAddress = accountAddress
        )
        val formattedRequiredMinimumBalance = accountMinimumBalance.formatAsAlgoAmount()
        return accountDetailAssetItemMapper.mapToRequiredMinimumBalanceItem(
            formattedRequiredMinimumBalance = formattedRequiredMinimumBalance
        )
    }

    private suspend fun createAssetListItem(
        assetData: BaseAccountAssetData
    ): AccountDetailAssetsItem.BaseAssetItem? {
        return with(accountDetailAssetItemMapper) {
            when (assetData) {
                is OwnedAssetData -> mapToOwnedAssetItem(assetData)
                is AdditionAssetData -> mapToPendingAdditionAssetItem(assetData)
                is DeletionAssetData -> mapToPendingRemovalAssetItem(assetData)
                // TODO: 24.03.2022 We should use interface instead of using when
                else -> null
            }
        }
    }

    private suspend fun createNFTListItem(
        assetData: BaseAccountAssetData,
        isHoldingByWatchAccount: Boolean,
        nftListingViewType: NFTListingViewType
    ): AccountDetailAssetsItem.BaseAssetItem? {
        return with(accountDetailAssetItemMapper) {
            when (assetData) {
                is BaseOwnedCollectibleData -> {
                    val isOwned = assetData.amount isGreaterThan BigInteger.ZERO
                    val isAmountVisible = assetData.amount isGreaterThan BigInteger.ONE
                    mapToOwnedNFTItem(
                        accountAssetData = assetData,
                        isHoldingByWatchAccount = isHoldingByWatchAccount,
                        isOwned = isOwned,
                        isAmountVisible = isAmountVisible,
                        shouldDecreaseOpacity = !isOwned || isHoldingByWatchAccount,
                        nftListingViewType = nftListingViewType
                    )
                }

                is PendingAdditionCollectibleData -> mapToPendingAdditionNFTITem(assetData)
                is PendingDeletionCollectibleData -> mapToPendingRemovalNFTItem(assetData)
                // TODO: We should use interface instead of using when
                else -> null
            }
        }
    }

    companion object {
        const val QUICK_ACTIONS_INDEX = 2
    }
}
