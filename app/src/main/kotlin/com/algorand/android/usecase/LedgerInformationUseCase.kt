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
import com.algorand.android.core.BaseUseCase
import com.algorand.android.mapper.LedgerInformationAccountItemMapper
import com.algorand.android.mapper.LedgerInformationAssetItemMapper
import com.algorand.android.mapper.LedgerInformationCanSignByItemMapper
import com.algorand.android.mapper.LedgerInformationTitleItemMapper
import com.algorand.android.models.LedgerInformationListItem
import com.algorand.android.modules.accountcore.domain.model.AccountTotalValue
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountTotalValue
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accounticon.ui.mapper.AccountIconDrawablePreviewMapper
import com.algorand.android.modules.currency.domain.usecase.CurrencyUseCase
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.modules.rekey.model.AccountSelectionListItem
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount
import com.algorand.android.utils.formatAsCurrency
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("LongParameterList")
class LedgerInformationUseCase @Inject constructor(
    private val parityUseCase: ParityUseCase,
    private val ledgerInformationTitleItemMapper: LedgerInformationTitleItemMapper,
    private val ledgerInformationAccountItemMapper: LedgerInformationAccountItemMapper,
    private val ledgerInformationAssetItemMapper: LedgerInformationAssetItemMapper,
    private val ledgerInformationCanSignByItemMapper: LedgerInformationCanSignByItemMapper,
    private val currencyUseCase: CurrencyUseCase,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val accountIconDrawablePreviewMapper: AccountIconDrawablePreviewMapper,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val getAccountInformation: GetAccountInformation
) : BaseUseCase() {

    suspend fun getLedgerInformationListItem(
        selectedLedgerAccount: AccountSelectionListItem.AccountItem,
        rekeyedAccountSelectionListItem: List<AccountSelectionListItem.AccountItem>?,
        authLedgerAccount: AccountSelectionListItem.AccountItem?
    ): List<LedgerInformationListItem> {
        val accountInformation = getAccountInformation(selectedLedgerAccount.address) ?: return emptyList()
        return prepareLedgerInformationListItem(
            accountInformation = accountInformation,
            selectedLedgerAccount = selectedLedgerAccount,
            rekeyedAccountSelectionListItem = rekeyedAccountSelectionListItem,
            authLedgerAccount = authLedgerAccount
        )
    }

    private suspend fun prepareLedgerInformationListItem(
        accountInformation: AccountInformation,
        selectedLedgerAccount: AccountSelectionListItem.AccountItem,
        rekeyedAccountSelectionListItem: List<AccountSelectionListItem.AccountItem>?,
        authLedgerAccount: AccountSelectionListItem.AccountItem?
    ): List<LedgerInformationListItem> {
        return withContext(Dispatchers.Default) {
            return@withContext mutableListOf<LedgerInformationListItem>().apply {
                val selectedCurrencySymbol = parityUseCase.getPrimaryCurrencySymbolOrName()
                val accountBalance = getAccountTotalValue(accountInformation, includeAlgo = true)
                val portfolioValue = getPortfolioValue(accountBalance, selectedCurrencySymbol)
                addAll(createLedgerAccountItem(accountInformation, portfolioValue))
                addAll(createAssetItems(accountInformation))
                addAll(createCanSignByItems(authLedgerAccount))
                addAll(createCanSignableAccounts(selectedLedgerAccount, rekeyedAccountSelectionListItem))
            }
        }
    }

    private suspend fun createLedgerAccountItem(
        accountInformation: AccountInformation,
        portfolioValue: String
    ): List<LedgerInformationListItem> {
        val isAccountRekeyed = accountInformation.isRekeyed()
        return mutableListOf<LedgerInformationListItem>().apply {
            add(ledgerInformationTitleItemMapper.mapTo(R.string.account_details))
            add(
                ledgerInformationAccountItemMapper.mapTo(
                    accountAddress = accountInformation.address,
                    portfolioValue = portfolioValue,
                    accountDisplayName = getAccountDisplayName(accountInformation.address),
                    accountIconDrawablePreview = accountIconDrawablePreviewMapper.mapToAccountIconDrawablePreview(
                        iconTintResId = R.color.wallet_3_icon,
                        iconResId = if (isAccountRekeyed) R.drawable.ic_rekey_shield else R.drawable.ic_ledger,
                        backgroundColorResId = R.color.wallet_3
                    )
                )
            )
        }
    }

    private suspend fun createAssetItems(accountInformation: AccountInformation): List<LedgerInformationListItem> {
        return mutableListOf<LedgerInformationListItem>().apply {
            add(ledgerInformationTitleItemMapper.mapTo(R.string.assets))
            val algoAssetData = getAccountBaseOwnedAssetData(accountInformation.address, ALGO_ID)
            if (algoAssetData != null) {
                add(ledgerInformationAssetItemMapper.mapTo(algoAssetData))
            }
            if (accountInformation.getAssetHoldingIds().isNotEmpty()) {
                accountInformation.getAssetHoldingIds().forEach {
                    val accountAssetData = getAccountBaseOwnedAssetData(accountInformation.address, it)
                        ?: return@forEach
                    add(ledgerInformationAssetItemMapper.mapTo(accountAssetData))
                }
            }
        }
    }

    private fun createCanSignByItems(
        authLedgerAccount: AccountSelectionListItem.AccountItem?
    ): List<LedgerInformationListItem> {
        return mutableListOf<LedgerInformationListItem>().apply {
            authLedgerAccount?.run {
                add(ledgerInformationTitleItemMapper.mapTo(R.string.can_be_signed_by))
                val ledgerInformationCanSignByItem = ledgerInformationCanSignByItemMapper.mapTo(
                    accountAddress = address,
                    accountIconDrawablePreview = accountIconDrawablePreviewMapper.mapToAccountIconDrawablePreview(
                        iconTintResId = R.color.wallet_3_icon,
                        iconResId = R.drawable.ic_ledger,
                        backgroundColorResId = R.color.wallet_3
                    )
                )
                add(ledgerInformationCanSignByItem)
            }
        }
    }

    private fun createCanSignableAccounts(
        selectedLedgerAccount: AccountSelectionListItem.AccountItem,
        rekeyedAccountSelectionListItem: List<AccountSelectionListItem.AccountItem>?
    ): List<LedgerInformationListItem> {
        return mutableListOf<LedgerInformationListItem>().apply {
            if (selectedLedgerAccount.selectedLedgerAccount !is SelectedLedgerAccount.LedgerAccount) {
                if (rekeyedAccountSelectionListItem.isNullOrEmpty()) {
                    return emptyList()
                }
                add(ledgerInformationTitleItemMapper.mapTo(R.string.can_sign_for_these))
                rekeyedAccountSelectionListItem.forEach {
                    val ledgerInformationCanSignByItem = ledgerInformationCanSignByItemMapper.mapTo(
                        accountAddress = it.address,
                        accountIconDrawablePreview = accountIconDrawablePreviewMapper.mapToAccountIconDrawablePreview(
                            iconTintResId = R.color.wallet_3_icon,
                            iconResId = R.drawable.ic_rekey_shield,
                            backgroundColorResId = R.color.wallet_3
                        )
                    )
                    add(ledgerInformationCanSignByItem)
                }
            }
        }
    }

    private fun getPortfolioValue(totalValue: AccountTotalValue, symbol: String): String {
        val totalHoldings = totalValue.primaryAccountValue
        val isSelectedPrimaryCurrencyFiat = !currencyUseCase.isPrimaryCurrencyAlgo()
        return totalHoldings.formatAsCurrency(symbol, isFiat = isSelectedPrimaryCurrencyFiat)
    }
}
