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

package com.algorand.android.ui.accountstatus.viewmodel

import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.AccountAction
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import javax.inject.Inject

internal class DefaultAccountStatusAccountActionProcessor @Inject constructor() : AccountStatusAccountActionProcessor {

    override suspend fun getAccountActions(accountLite: AccountLite): List<AccountAction> {
        val accountActions = mutableListOf<AccountAction>()

        if (accountLite.cachedInfo?.type?.canSignTransaction() == true) {
            accountActions.add(AccountAction.RekeyToLedger)
            accountActions.add(AccountAction.RekeyToStandard)
        }

        if (accountLite.registrationType.hasSignerDetails) {
            accountActions.add(AccountAction.RescanRekeyedAddresses)
        }

        return accountActions
    }
}
