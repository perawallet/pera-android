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

package com.algorand.android.modules.onboarding.pairledger.accountselection.ui.usecase

import com.algorand.android.R
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountcore.ui.model.AccountIconResource
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.FetchRekeyedAccounts
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.mapper.LedgerAccountSelectionAccountItemMapper
import com.algorand.android.mapper.LedgerAccountSelectionInstructionItemMapper
import com.algorand.android.modules.onboarding.pairledger.accountselection.ui.mapper.RegisterLedgerAccountSelectionPreviewMapper
import com.algorand.android.modules.onboarding.pairledger.accountselection.ui.model.RegisterLedgerAccountSelectionNavArgs
import com.algorand.android.modules.onboarding.pairledger.accountselection.ui.model.RegisterLedgerAccountSelectionPreview
import com.algorand.android.modules.rekey.model.AccountSelectionListItem
import com.algorand.android.modules.rekey.model.AccountSelectionListItem.SearchType
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount
import com.algorand.android.utils.extensions.addFirst
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@SuppressWarnings("LongParameterList")
class RegisterLedgerAccountSelectionPreviewUseCase @Inject constructor(
    private val registerLedgerAccountSelectionPreviewMapper: RegisterLedgerAccountSelectionPreviewMapper,
    private val ledgerAccountSelectionInstructionItemMapper: LedgerAccountSelectionInstructionItemMapper,
    private val ledgerAccountSelectionAccountItemMapper: LedgerAccountSelectionAccountItemMapper,
    private val fetchRekeyedAccounts: FetchRekeyedAccounts,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val fetchAndCacheAssets: FetchAndCacheAssets,
) {

    fun getUpdatedPreviewAccordingToAccountSelection(
        previousPreview: RegisterLedgerAccountSelectionPreview,
        accountItem: AccountSelectionListItem.AccountItem
    ): RegisterLedgerAccountSelectionPreview {
        val updatedList = previousPreview.accountSelectionListItems.map {
            if (it is AccountSelectionListItem.AccountItem && it.address == accountItem.address) {
                it.copy(isSelected = !it.isSelected)
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

    @SuppressWarnings("LongMethod")
    fun getRegisterLedgerAccountSelectionPreview(
        ledgerAccountsInformation: List<RegisterLedgerAccountSelectionNavArgs.LedgerAccountsNavArgs>,
        bluetoothAddress: String,
        bluetoothName: String?
    ) = flow {
        val ledgerAccountAddress = ledgerAccountsInformation.map { it.address }
        val ledgerAccounts = mutableListOf<AccountSelectionListItem.AccountItem>().apply {
            ledgerAccountsInformation.forEachIndexed { index, ledgerAccountInformation ->
                // Cache ledger accounts assets
                fetchAndCacheAssets(ledgerAccountInformation.assetHoldingIds, false)

                val isRekeyed = ledgerAccountInformation.isRekeyed
                val accountDisplayName = getAccountDisplayName(ledgerAccountInformation.address)

                val authAccountDetail = SelectedLedgerAccount.LedgerAccount(
                    address = ledgerAccountInformation.address,
                    bleAddress = bluetoothAddress,
                    bleName = bluetoothName,
                    indexInLedger = index
                )
                // Since we don't have ledger account in our local, we have to create their drawable manually
                val accountIconDrawablePreview = AccountIconDrawablePreview(
                    backgroundColorResId = AccountIconResource.LEDGER.backgroundColorResId,
                    iconTintResId = AccountIconResource.LEDGER.iconTintResId,
                    iconResId = if (isRekeyed) R.drawable.ic_rekey_shield else AccountIconResource.LEDGER.iconResId
                )
                val authAccountSelectionListItem = ledgerAccountSelectionAccountItemMapper.mapTo(
                    selectorDrawableRes = R.drawable.checkbox_selector,
                    accountDisplayName = accountDisplayName,
                    accountIconDrawablePreview = accountIconDrawablePreview,
                    address = ledgerAccountInformation.address,
                    selectedLedgerAccount = authAccountDetail
                )
                add(authAccountSelectionListItem)

                val rekeyedAccountSelectionListItems = getRekeyedAccountsOfAuthAccount(
                    rekeyAdminAddress = ledgerAccountInformation.address,
                    ledgerDetail = authAccountDetail
                ).filter { it.address !in ledgerAccountAddress }
                addAll(rekeyedAccountSelectionListItems)
            }
        }.toMutableList<AccountSelectionListItem>().apply {
            val instructionItem = ledgerAccountSelectionInstructionItemMapper.mapTo(
                accountSize = size,
                searchType = SearchType.REGISTER
            )
            addFirst(instructionItem)
        }

        val preview = registerLedgerAccountSelectionPreviewMapper.mapToRegisterLedgerAccountSelectionPreview(
            isLoading = false,
            accountSelectionListItems = ledgerAccounts,
            isActionButtonEnabled = ledgerAccounts.filterIsInstance<AccountSelectionListItem.AccountItem>().any {
                it.isSelected
            }
        )
        emit(preview)
    }.onStart {
        val loadingState = registerLedgerAccountSelectionPreviewMapper.mapToRegisterLedgerAccountSelectionPreview(
            isLoading = true,
            accountSelectionListItems = emptyList(),
            isActionButtonEnabled = false
        )
        emit(loadingState)
    }.distinctUntilChanged()

    private suspend fun getRekeyedAccountsOfAuthAccount(
        rekeyAdminAddress: String,
        ledgerDetail: SelectedLedgerAccount.LedgerAccount
    ): List<AccountSelectionListItem.AccountItem> {
        return fetchRekeyedAccounts(rekeyAdminAddress).use(
            onSuccess = { rekeyedAccountList ->
                createAccountListFromResponse(
                    rekeyedAccountsList = rekeyedAccountList,
                    rekeyAdminAddress = rekeyAdminAddress,
                    ledgerDetail = ledgerDetail
                )
            },
            onFailed = { _, _ ->
                emptyList()
            }
        )
    }

    private suspend fun createAccountListFromResponse(
        rekeyedAccountsList: List<AccountInformation>,
        rekeyAdminAddress: String,
        ledgerDetail: SelectedLedgerAccount.LedgerAccount
    ): List<AccountSelectionListItem.AccountItem> {
        return rekeyedAccountsList.mapNotNull { accountInfo ->
            if (accountInfo.address != rekeyAdminAddress) {
                // Cache rekeyed accounts assets
                fetchAndCacheAssets(accountInfo.getAssetHoldingIds(), false)

                val accountDisplayName = getAccountDisplayName(accountInfo.address)
                // Since we don't have rekeyed accounts to ledger in our local, we have to
                // create their drawable manually
                val accountIconDrawablePreview = AccountIconDrawablePreview(
                    backgroundColorResId = R.color.wallet_3,
                    iconResId = R.drawable.ic_rekey_shield,
                    iconTintResId = R.color.wallet_3_icon
                )
                ledgerAccountSelectionAccountItemMapper.mapTo(
                    address = accountInfo.address,
                    selectorDrawableRes = R.drawable.checkbox_selector,
                    accountDisplayName = accountDisplayName,
                    accountIconDrawablePreview = accountIconDrawablePreview,
                    selectedLedgerAccount = SelectedLedgerAccount.RekeyedAccount(
                        address = accountInfo.address,
                        authDetail = ledgerDetail
                    )
                )
            } else {
                null
            }
        }.orEmpty()
    }
}
