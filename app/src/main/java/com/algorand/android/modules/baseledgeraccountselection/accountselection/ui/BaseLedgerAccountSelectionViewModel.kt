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

package com.algorand.android.modules.baseledgeraccountselection.accountselection.ui

import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.rekey.model.AccountSelectionListItem
import com.algorand.android.modules.rekey.model.AccountSelectionListItem.AccountItem
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount.LedgerAccount
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount.RekeyedAccount
import com.algorand.android.modules.rekey.model.SelectedLedgerAccounts

abstract class BaseLedgerAccountSelectionViewModel : BaseViewModel() {

    abstract fun onNewAccountSelected(accountItem: AccountItem)

    abstract val accountSelectionList: List<AccountSelectionListItem>

    private val accountSelectionAccountList: List<AccountItem>
        get() = accountSelectionList.filterIsInstance<AccountItem>()

    fun getSelectedAccounts(): SelectedLedgerAccounts? {
        val rekeyedAccounts = accountSelectionAccountList.mapNotNull { accountItem ->
            (accountItem.selectedLedgerAccount as? RekeyedAccount).takeIf { accountItem.isSelected }
        }

        val ledgerAccounts = accountSelectionAccountList.mapNotNull { accountItem ->
            (accountItem.selectedLedgerAccount as? LedgerAccount).takeIf { accountItem.isSelected }
        }
        if (rekeyedAccounts.isEmpty() && ledgerAccounts.isEmpty()) return null
        return SelectedLedgerAccounts(rekeyedAccounts, ledgerAccounts)
    }

    fun getAuthAccountOf(
        accountSelectionListItem: AccountItem
    ): AccountItem? {
        return accountSelectionAccountList.firstOrNull {
            it.selectedLedgerAccount is LedgerAccount && it.address == accountSelectionListItem.address
        }
    }

    fun getRekeyedAccountOf(
        accountSelectionListItem: AccountItem
    ): Array<AccountItem> {
        return accountSelectionAccountList.filter {
            (it.selectedLedgerAccount as? RekeyedAccount)?.authDetail?.address == accountSelectionListItem.address
        }.toTypedArray()
    }
}
