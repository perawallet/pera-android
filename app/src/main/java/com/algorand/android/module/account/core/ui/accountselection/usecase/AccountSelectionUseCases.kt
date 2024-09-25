/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.account.core.ui.accountselection.usecase

import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.model.AccountType

interface CreateLoadedAccountConfiguration {
    suspend operator fun invoke(
        accountDetail: AccountDetail,
        showHoldings: Boolean,
        selectedCurrencySymbol: String
    ): BaseItemConfiguration.AccountItemConfiguration
}

interface CreateNotLoadedAccountConfiguration {
    suspend operator fun invoke(address: String): BaseItemConfiguration.AccountItemConfiguration
}

interface GetAccountSelectionAccountItems {
    suspend operator fun invoke(
        showHoldings: Boolean,
        showFailedAccounts: Boolean,
    ): List<BaseAccountSelectionListItem.BaseAccountItem>
}

interface GetAccountSelectionAccountsWhichCanSignTransaction {
    suspend operator fun invoke(
        showHoldings: Boolean,
        showFailedAccounts: Boolean,
        assetId: Long? = null,
        excludedAccountTypes: List<AccountType>? = null
    ): List<BaseAccountSelectionListItem.BaseAccountItem>
}

interface GetAccountSelectionContactItems {
    suspend operator fun invoke(): List<BaseAccountSelectionListItem.BaseAccountItem>
}

interface GetAccountSelectionItemsFromAccountAddress {
    suspend operator fun invoke(accountAddress: String?): BaseAccountSelectionListItem.BaseAccountItem.AccountItem?
}

interface GetAccountSelectionNameServiceItems {
    suspend operator fun invoke(query: String): List<BaseAccountSelectionListItem.BaseAccountItem.NftDomainAccountItem>
}
