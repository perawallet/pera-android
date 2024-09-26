/*
 * Copyright 2022 Pera Wallet, LDA
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

package com.algorand.android.usecase

import com.algorand.android.R
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAlgoAssetName
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.core.BaseUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountTotalValue
import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.mapper.LedgerInformationAccountItemMapper
import com.algorand.android.mapper.LedgerInformationAssetItemMapper
import com.algorand.android.mapper.LedgerInformationCanSignByItemMapper
import com.algorand.android.mapper.LedgerInformationTitleItemMapper
import com.algorand.android.models.LedgerInformationListItem
import com.algorand.android.modules.rekey.model.AccountSelectionListItem
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencyName
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.utils.formatAsCurrency
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("LongParameterList")
class LedgerInformationUseCase @Inject constructor(
    private val ledgerInformationTitleItemMapper: LedgerInformationTitleItemMapper,
    private val ledgerInformationAccountItemMapper: LedgerInformationAccountItemMapper,
    private val ledgerInformationAssetItemMapper: LedgerInformationAssetItemMapper,
    private val ledgerInformationCanSignByItemMapper: LedgerInformationCanSignByItemMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getPrimaryCurrencyName: GetPrimaryCurrencyName,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getAccountInformation: GetAccountInformation,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getAssetName: GetAssetName,
    private val getAlgoAssetName: GetAlgoAssetName,
    private val getAccountOwnedAssetsData: GetAccountOwnedAssetsData
) : BaseUseCase() {

    suspend fun getLedgerInformationListItem(
        selectedAccountItem: AccountSelectionListItem.AccountItem,
        rekeyedAccountSelectionListItem: Array<AccountSelectionListItem.AccountItem>?,
        authLedgerAccount: AccountSelectionListItem.AccountItem?
    ): List<LedgerInformationListItem> {
        return prepareLedgerInformationListItem(
            selectedAccountItem = selectedAccountItem,
            rekeyedAccountSelectionListItem = rekeyedAccountSelectionListItem,
            authLedgerAccount = authLedgerAccount
        )
    }

    private suspend fun prepareLedgerInformationListItem(
        selectedAccountItem: AccountSelectionListItem.AccountItem,
        rekeyedAccountSelectionListItem: Array<AccountSelectionListItem.AccountItem>?,
        authLedgerAccount: AccountSelectionListItem.AccountItem?
    ): List<LedgerInformationListItem> {
        return withContext(Dispatchers.Default) {
            return@withContext mutableListOf<LedgerInformationListItem>().apply {
                val portfolioValue = getPortfolioValue(selectedAccountItem.address)
                addAll(createLedgerAccountItem(selectedAccountItem, portfolioValue))
                addAll(createAssetItems(selectedAccountItem.address))
                addAll(createCanSignByItems(authLedgerAccount))
                addAll(createCanSignableAccounts(selectedAccountItem, rekeyedAccountSelectionListItem))
            }
        }
    }

    private suspend fun createLedgerAccountItem(
        selectedAccountItem: AccountSelectionListItem.AccountItem,
        portfolioValue: String
    ): List<LedgerInformationListItem> {
        val isAccountRekeyed = selectedAccountItem.selectedLedgerAccount is SelectedLedgerAccount.RekeyedAccount
        return mutableListOf<LedgerInformationListItem>().apply {
            add(ledgerInformationTitleItemMapper.mapTo(R.string.account_details))
            add(
                ledgerInformationAccountItemMapper.mapTo(
                    accountAddress = selectedAccountItem.address,
                    portfolioValue = portfolioValue,
                    accountDisplayName = getAccountDisplayName(selectedAccountItem.address),
                    accountIconDrawablePreview = AccountIconDrawablePreview(
                        iconTintResId = R.color.wallet_3_icon,
                        iconResId = if (isAccountRekeyed) R.drawable.ic_rekey_shield else R.drawable.ic_ledger,
                        backgroundColorResId = R.color.wallet_3
                    )
                )
            )
        }
    }

    private suspend fun createAssetItems(address: String): List<LedgerInformationListItem> {
        val accountInformation = getAccountInformation(address) ?: return emptyList()
        return mutableListOf<LedgerInformationListItem>().apply {
            val algoAssetData = getAccountBaseOwnedAssetData(accountInformation.address, ALGO_ASSET_ID)
                as? OwnedAssetData
            add(ledgerInformationTitleItemMapper.mapTo(R.string.assets))
            if (algoAssetData != null) {
                getAlgoAssetName().run {
                    add(ledgerInformationAssetItemMapper.mapTo(algoAssetData, fullName, shortName))
                }
            }
            val accountAssets = getAccountOwnedAssetsData(accountInformation.address, false)
            accountAssets.forEach {
                val assetName = getAssetName(it.name)
                val assetShortName = getAssetName(it.shortName)
                add(ledgerInformationAssetItemMapper.mapTo(it, assetName, assetShortName))
            }
        }
    }

    private fun createCanSignByItems(
        authLedgerAccount: AccountSelectionListItem.AccountItem?
    ): List<LedgerInformationListItem> {
        val isAccountRekeyed = authLedgerAccount?.selectedLedgerAccount is SelectedLedgerAccount.RekeyedAccount
        return mutableListOf<LedgerInformationListItem>().apply {
            authLedgerAccount?.run {
                add(ledgerInformationTitleItemMapper.mapTo(R.string.can_be_signed_by))
                val ledgerInformationCanSignByItem = ledgerInformationCanSignByItemMapper.mapTo(
                    accountAddress = authLedgerAccount.address,
                    accountIconDrawablePreview = AccountIconDrawablePreview(
                        iconTintResId = R.color.wallet_3_icon,
                        iconResId = if (isAccountRekeyed) R.drawable.ic_rekey_shield else R.drawable.ic_ledger,
                        backgroundColorResId = R.color.wallet_3
                    )
                )
                add(ledgerInformationCanSignByItem)
            }
        }
    }

    private fun createCanSignableAccounts(
        selectedAccountItem: AccountSelectionListItem.AccountItem,
        rekeyedAccountSelectionListItem: Array<AccountSelectionListItem.AccountItem>?
    ): List<LedgerInformationListItem> {
        return mutableListOf<LedgerInformationListItem>().apply {
            if (selectedAccountItem.selectedLedgerAccount !is SelectedLedgerAccount.LedgerAccount) {
                if (rekeyedAccountSelectionListItem.isNullOrEmpty()) {
                    return emptyList()
                }
                add(ledgerInformationTitleItemMapper.mapTo(R.string.can_sign_for_these))
                rekeyedAccountSelectionListItem.forEach {
                    val isAccountRekeyed = it.selectedLedgerAccount is SelectedLedgerAccount.RekeyedAccount
                    val ledgerInformationCanSignByItem = ledgerInformationCanSignByItemMapper.mapTo(
                        accountAddress = it.address,
                        accountIconDrawablePreview = AccountIconDrawablePreview(
                            iconTintResId = R.color.wallet_3_icon,
                            iconResId = if (isAccountRekeyed) R.drawable.ic_rekey_shield else R.drawable.ic_ledger,
                            backgroundColorResId = R.color.wallet_3
                        )
                    )
                    add(ledgerInformationCanSignByItem)
                }
            }
        }
    }

    private suspend fun getPortfolioValue(address: String): String {
        val selectedCurrencySymbol = getPrimaryCurrencySymbol() ?: getPrimaryCurrencyName()
        val primaryValue = getAccountTotalValue(address, true).primaryAccountValue
        return primaryValue.formatAsCurrency(selectedCurrencySymbol, isFiat = !isPrimaryCurrencyAlgo())
    }
}
