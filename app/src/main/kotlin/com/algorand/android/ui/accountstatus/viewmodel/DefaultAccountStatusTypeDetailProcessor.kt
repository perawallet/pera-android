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

import com.algorand.android.modules.accountcore.ui.usecase.GetWalletIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.AccountStatusTypeDetail
import com.algorand.wallet.account.custom.domain.usecase.GetHdSeedCustomName
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.local.domain.usecase.GetHdSeedId
import javax.inject.Inject

internal class DefaultAccountStatusTypeDetailProcessor @Inject constructor(
    private val getHdSeedId: GetHdSeedId,
    private val getHdSeedCustomName: GetHdSeedCustomName,
    private val getWalletIconDrawablePreview: GetWalletIconDrawablePreview
) : AccountStatusTypeDetailProcessor {

    override suspend fun getAccountStatusTypeDetail(accountLite: AccountLite): AccountStatusTypeDetail? {
        return when (accountLite.cachedInfo?.type) {
            AccountType.Algo25 -> AccountStatusTypeDetail.Algo25
            AccountType.LedgerBle -> AccountStatusTypeDetail.Ledger
            AccountType.NoAuth -> AccountStatusTypeDetail.NoAuth
            AccountType.Rekeyed -> AccountStatusTypeDetail.Rekeyed
            AccountType.RekeyedAuth -> AccountStatusTypeDetail.RekeyedAuth
            AccountType.HdKey -> getHdKeyAccountStatusTypeDetail(accountLite)
            else -> null
        }
    }

    private suspend fun getHdKeyAccountStatusTypeDetail(accountLite: AccountLite): AccountStatusTypeDetail.HdKey? {
        val hdSeed = getHdSeedId(accountLite.address) ?: return null
        return AccountStatusTypeDetail.HdKey(
            hdSeed,
            getHdSeedCustomName(hdSeed).orEmpty(),
            iconDrawablePreview = getWalletIconDrawablePreview()
        )
    }
}
