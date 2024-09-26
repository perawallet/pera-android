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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.accountselection.ui.usecase

import com.algorand.android.R
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.mapper.LedgerAccountSelectionAccountItemMapper
import com.algorand.android.mapper.LedgerAccountSelectionInstructionItemMapper
import com.algorand.android.modules.rekey.model.AccountSelectionListItem
import com.algorand.android.modules.rekey.model.AccountSelectionListItem.SearchType
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount
import com.algorand.android.modules.rekey.rekeytoledgeraccount.accountselection.ui.mapper.RekeyLedgerAccountSelectionPreviewMapper
import com.algorand.android.modules.rekey.rekeytoledgeraccount.accountselection.ui.model.RekeyLedgerAccountSelectionNavArgs
import com.algorand.android.modules.rekey.rekeytoledgeraccount.accountselection.ui.model.RekeyLedgerAccountSelectionPreview
import com.algorand.android.utils.extensions.addFirst
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class RekeyLedgerAccountSelectionPreviewUseCase @Inject constructor(
    private val rekeyLedgerAccountSelectionPreviewMapper: RekeyLedgerAccountSelectionPreviewMapper,
    private val ledgerAccountSelectionInstructionItemMapper: LedgerAccountSelectionInstructionItemMapper,
    private val ledgerAccountSelectionAccountItemMapper: LedgerAccountSelectionAccountItemMapper,
    private val fetchAndCacheAssets: FetchAndCacheAssets,
    private val getAccountDisplayName: GetAccountDisplayName
) {

    fun getUpdatedPreviewAccordingToAccountSelection(
        previousPreview: RekeyLedgerAccountSelectionPreview,
        accountItem: AccountSelectionListItem.AccountItem
    ): RekeyLedgerAccountSelectionPreview {
        val updatedList = previousPreview.accountSelectionListItems.map {
            if (it is AccountSelectionListItem.AccountItem) {
                it.copy(isSelected = it.address == accountItem.address)
            } else {
                it
            }
        }
        return previousPreview.copy(
            accountSelectionListItems = updatedList,
            isActionButtonEnabled = updatedList.filterIsInstance<AccountSelectionListItem.AccountItem>().any {
                it.isSelected
            }
        )
    }

    suspend fun getRekeyLedgerAccountSelectionPreview(
        ledgerAccountsNavArgs: List<RekeyLedgerAccountSelectionNavArgs.LedgerAccountsNavArgs>,
        bluetoothAddress: String,
        bluetoothName: String?,
        accountAddress: String
    ) = flow {
        val ledgerAccounts = mutableListOf<AccountSelectionListItem>().apply {
            ledgerAccountsNavArgs.forEachIndexed { index, ledgerAccount ->

                if (isAccountNotEligibleToRekey(ledgerAccount, accountAddress)) return@forEachIndexed

                // Cache ledger accounts assets
                fetchAndCacheAssets(ledgerAccount.assetHoldingIds, false)

                val authAccountDetail = SelectedLedgerAccount.LedgerAccount(
                    address = ledgerAccount.authAddress.orEmpty(),
                    bleAddress = bluetoothAddress,
                    bleName = bluetoothName,
                    indexInLedger = index
                )
                val accountDisplayName = getAccountDisplayName(ledgerAccount.address)
                // Since we don't have ledger account in our local, we have to create their drawable manually
                val accountIconDrawablePreview = AccountIconDrawablePreview(
                    backgroundColorResId = R.color.wallet_3,
                    iconTintResId = R.color.wallet_3_icon,
                    iconResId = R.drawable.ic_ledger
                )
                val authAccountSelectionListItem = ledgerAccountSelectionAccountItemMapper.mapTo(
                    address = ledgerAccount.address,
                    selectorDrawableRes = R.drawable.selector_found_account_radio,
                    accountDisplayName = accountDisplayName,
                    accountIconDrawablePreview = accountIconDrawablePreview,
                    selectedLedgerAccount = SelectedLedgerAccount.RekeyedAccount(
                        address = ledgerAccount.address,
                        authDetail = authAccountDetail
                    )
                )
                add(authAccountSelectionListItem)
            }
            val instructionItem = ledgerAccountSelectionInstructionItemMapper.mapTo(
                accountSize = size,
                searchType = SearchType.REKEY
            )
            addFirst(instructionItem)
        }
        val preview = rekeyLedgerAccountSelectionPreviewMapper.mapToRekeyLedgerAccountSelectionPreview(
            isLoading = false,
            accountSelectionListItems = ledgerAccounts,
            isActionButtonEnabled = ledgerAccounts.filterIsInstance<AccountSelectionListItem.AccountItem>().any {
                it.isSelected
            }
        )
        emit(preview)
    }.onStart {
        val loadingState = rekeyLedgerAccountSelectionPreviewMapper.mapToRekeyLedgerAccountSelectionPreview(
            isLoading = true,
            accountSelectionListItems = emptyList(),
            isActionButtonEnabled = false
        )
        emit(loadingState)
    }.distinctUntilChanged()

    private fun isAccountNotEligibleToRekey(
        ledgerAccountInformation: RekeyLedgerAccountSelectionNavArgs.LedgerAccountsNavArgs,
        accountAddress: String
    ): Boolean {
        // To prevent chain rekey, we shouldn't display rekeyed ledger accounts
        val isRekeyed = ledgerAccountInformation.isRekeyed
        // Don't show the same ledger account in the rekey list
        val isTheSameAccount = accountAddress == ledgerAccountInformation.address
        return isRekeyed || isTheSameAccount
    }
}
